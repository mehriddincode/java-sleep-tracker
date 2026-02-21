package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinDurationFunction implements SleepAnalysisFunction {

    private static final String MESSAGE = "Минимальная продолжительность сессии (мин)";
    private static final String NO_DATA = "нет данных";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        String result = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .min()
                .stream()
                .mapToObj(String::valueOf)
                .findFirst()
                .orElse(NO_DATA);

        return new SleepAnalysisResult(MESSAGE, result);
    }
}
