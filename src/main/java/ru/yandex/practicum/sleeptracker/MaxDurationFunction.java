package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxDurationFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        String result = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .max()
                .stream()
                .mapToObj(String::valueOf)
                .findFirst()
                .orElse("нет данных");

        return new SleepAnalysisResult(
                "Максимальная продолжительность сессии (мин)",
                result
        );
    }
}
