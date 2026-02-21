package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinDurationFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        String result = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .min()
                .stream()
                .mapToObj(String::valueOf)
                .findFirst()
                .orElse("нет данных");

        return new SleepAnalysisResult(
                "Минимальная продолжительность сессии (мин)",
                result
        );
    }
}
