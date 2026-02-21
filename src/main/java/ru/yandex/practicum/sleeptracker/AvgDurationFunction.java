package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Locale;

public class AvgDurationFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        String result = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .average()
                .stream()
                .mapToObj(avg -> String.format(Locale.US, "%.1f", avg))
                .findFirst()
                .orElse("нет данных");

        return new SleepAnalysisResult(
                "Средняя продолжительность сессии (мин)",
                result
        );
    }
}
