package com.fluentspeechapp.speechconverterv1.service.impl;

import com.fluentspeechapp.speechconverterv1.service.OpusService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class OpusServiceImpl implements OpusService {

    public void saveTempOpusFile(BinaryMessage message) {
        try {
            Path opusFile = Files.createTempFile("audio", ".opus");
            InputStream in = new ByteArrayInputStream(message.getPayload().array());
            Files.copy(in, opusFile, StandardCopyOption.REPLACE_EXISTING);

            // Создаем путь для временного файла WAV
            Path wavFile = Files.createTempFile("audio", ".wav");

            // Вызываем ffmpeg для конвертации
            convertOpusToWav(opusFile.toString(), wavFile.toString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }

    private void convertOpusToWav(String opusFilePath, String wavFilePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\PathPrograms\\bin\\ffmpeg.exe",
                "-y",
                "-i", opusFilePath,
                "-acodec", "pcm_s16le",
                "-ar", "16000",
                "-ac", "1",
                wavFilePath);

        processBuilder.redirectErrorStream(true); // Перенаправляем ошибки в стандартный поток вывода
        Process process = processBuilder.start();

        // Выводим ошибки ffmpeg в консоль
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("FFmpeg failed to convert the file. Exit code: " + exitCode);
        } else {
            System.out.println("Conversion completed successfully. WAV file path: " + wavFilePath);
        }

        // Проверяем наличие выходного файла WAV
        if (Files.exists(Path.of(wavFilePath))) {
            System.out.println("WAV file created at: " + wavFilePath);
        } else {
            throw new IOException("WAV file not found at: " + wavFilePath);
        }

        // Удаление временного файла Opus после конвертации
        Files.deleteIfExists(Path.of(opusFilePath));
    }

}
