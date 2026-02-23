package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private final List<SleepAnalysisFunction> functions = Arrays.asList(
            new TotalSessionsFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AvgDurationFunction(),
            new BadQualityCountFunction(),
            new SleeplessNightsFunction(),
            new ChronotypFunction()
    );

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Ошибка: укажите путь к файлу лога сна в качестве аргумента.");
            return;
        }

        List<SleepingSession> sessions = loadSessions(args[0]);

        SleepTrackerApp app = new SleepTrackerApp();

        System.out.println("=== Анализ сна ===");
        System.out.println("Загружено сессий: " + sessions.size());
        System.out.println("------------------");

        app.functions.stream()
                .map(f -> f.analyze(sessions))
                .forEach(result -> System.out.println(result.getDescription() + ": " + result.getValue()));

        System.out.println("==================");
    }

    public static List<SleepingSession> loadSessions(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath))
                .filter(line -> !line.isBlank())
                .map(SleepTrackerApp::parseLine)
                .collect(Collectors.toList());
    }

    private static SleepingSession parseLine(String line) {
        String[] parts = line.trim().split(";");
        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());
        return new SleepingSession(start, end, quality);
    }
}
