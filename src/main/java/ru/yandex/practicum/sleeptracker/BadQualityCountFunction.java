package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class BadQualityCountFunction implements SleepAnalysisFunction {

    private static final String MESSAGE = "Количество сессий с плохим качеством сна";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        long count = sessions.stream()
                .filter(s -> s.getQuality() == SleepQuality.BAD)
                .count();

        return new SleepAnalysisResult(MESSAGE, String.valueOf(count));
    }
}
