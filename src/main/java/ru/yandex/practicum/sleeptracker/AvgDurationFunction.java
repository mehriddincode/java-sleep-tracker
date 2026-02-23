package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.Locale;

public class AvgDurationFunction implements SleepAnalysisFunction {

    private static final String MESSAGE = "Средняя продолжительность сессии (мин)";
    private static final String NO_DATA = "нет данных";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        String result = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .average()
                .stream()
                .mapToObj(avg -> String.format(Locale.US, "%.1f", avg))
                .findFirst()
                .orElse(NO_DATA);

        return new SleepAnalysisResult(MESSAGE, result);
    }
}
