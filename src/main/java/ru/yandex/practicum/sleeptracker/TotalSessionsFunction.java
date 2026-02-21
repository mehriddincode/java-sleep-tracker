package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class TotalSessionsFunction implements SleepAnalysisFunction {

    private static final String MESSAGE = "Всего сессий сна за период";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(MESSAGE, String.valueOf(sessions.size()));
    }
}
