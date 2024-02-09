package com.fluentspeechapp.speechconverterv1.service.impl;

import com.fluentspeechapp.speechconverterv1.service.LevenshteinService;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LevenshteinServiceImpl implements LevenshteinService {

    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final AtomicInteger wordIndex = new AtomicInteger(0);
    private final Map<String, Integer> wordsMap = new LinkedHashMap<>();   //можно попробовать linkedHashSet
    private String lastProcessedText = ""; // Хранит последний обработанный текст



    @Override
    public String handleMessage(String incomingFragment) {
        String[] words = incomingFragment.split("\\s+");

        for (String word : words) {
            wordsMap.putIfAbsent(word, wordIndex.getAndIncrement());
        }

        return buildOrderedText();
    }

    private String buildOrderedText() {
        StringBuilder processedText = new StringBuilder();

        wordsMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> processedText.append(entry.getKey()).append(" "));

        wordsMap.clear(); // Очищаем map после построения текста
        wordIndex.set(0); // Сбрасываем индекс слова

        String trim = processedText.toString().trim();

        if (isSimilar(trim, lastProcessedText)) {
            // Если тексты слишком похожи, можно пропустить дальнейшую обработку или отправку
            return "";
        } else {
            // Сохраняем новый обработанный текст как последний обработанный результат
            lastProcessedText = trim;
            // Продолжаем обработку или отправку текста
            return trim;
        }

    }

    private boolean isSimilar(String text1, String text2) {
        int distance = levenshteinDistance.apply(text1, text2);
        int similarityThreshold = 7; // Задайте порог схожести
        return distance <= similarityThreshold;
    }
}
