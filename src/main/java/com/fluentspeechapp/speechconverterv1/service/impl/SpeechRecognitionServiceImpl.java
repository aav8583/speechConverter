package com.fluentspeechapp.speechconverterv1.service.impl;

import com.fluentspeechapp.speechconverterv1.service.SpeechRecognitionService;
import com.google.api.gax.rpc.*;
import com.google.cloud.speech.v1.*;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpeechRecognitionServiceImpl implements SpeechRecognitionService {

    private SpeechClient speechClient;

    @PostConstruct
    public void initialize() {
        try {
            speechClient = SpeechClient.create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanUp() {
        if (speechClient != null) {
            speechClient.close();
        }
    }


    @Override
    public String recognizeSpeechFromWavFile(Path wavFilePath) {
        return null;
    }

//    @Async("mainExecutor")
    @Override
    public void streamingRecognizeSpeech(byte[] data) {
        try {
                RecognitionConfig recConfig =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                                .setLanguageCode("ru-RU")
                                .setSampleRateHertz(16000)
                                .setModel("default")
                                .build();

                StreamingRecognitionConfig config =
                        StreamingRecognitionConfig.newBuilder().setConfig(recConfig).build();

                class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
                    private final SettableFuture<List<T>> future = SettableFuture.create();
                    private final List<T> messages = new java.util.ArrayList<T>();

                    @Override
                    public void onNext(T message) {
                        messages.add(message);
                    }

                    @Override
                    public void onError(Throwable t) {
                        future.setException(t);
                    }

                    @Override
                    public void onCompleted() {
                        future.set(messages);
                    }

                    // Returns the SettableFuture object to get received messages / exceptions.
                    public SettableFuture<List<T>> future() {
                        return future;
                    }
                }

                ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver =
                        new ResponseApiStreamingObserver<>();

                BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable =
                        speechClient.streamingRecognizeCallable();

                ApiStreamObserver<StreamingRecognizeRequest> requestObserver =
                        callable.bidiStreamingCall(responseObserver);

                // The first request must **only** contain the audio configuration:
                requestObserver.onNext(
                        StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build());


                requestObserver.onNext(
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(data))
                                .build());

                // Mark transmission as completed after sending the data.
                requestObserver.onCompleted();

                List<StreamingRecognizeResponse> responses = responseObserver.future().get();

                for (StreamingRecognizeResponse response : responses) {
                    // For streaming recognize, the results list has one is_final result (if available) followed
                    // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
                    // Just print the first result here.
                    StreamingRecognitionResult result = response.getResultsList().get(0);
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    System.out.printf("Transcript : %s\n", alternative.getTranscript());
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void streamingMicRecognize(byte[] payload) throws Exception {
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {
                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {
                        }

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);
                        }

                        public void onComplete() {
                            for (StreamingRecognizeResponse response : responses) {
                                StreamingRecognitionResult result = response.getResultsList().get(0);
                                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                                System.out.printf("Transcript : %s\n", alternative.getTranscript());
                            }
                        }

                        public void onError(Throwable t) {
                            System.out.println(t);
                        }
                    };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(16000)
                            .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);
            // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
            // bigEndian: false
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info targetInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat); // Set the system information to read from the microphone audio stream

            if (!AudioSystem.isLineSupported(targetInfo)) {
                System.out.println("Microphone not supported");
                System.exit(0);
            }
            // Target data line captures the audio stream the microphone produces.
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking");
            long startTime = System.currentTimeMillis();
            // Audio Input Stream
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            while (true) {
                long estimatedTime = System.currentTimeMillis() - startTime;
                byte[] data = new byte[6400];
                audio.read(data);
                if (estimatedTime > 60000) { // 60 seconds
                    System.out.println("Stop speaking.");
                    targetDataLine.stop();
                    targetDataLine.close();
                    break;
                }
                request =
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(data))
                                .build();
                clientStream.send(request);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        responseObserver.onComplete();
    }
}
