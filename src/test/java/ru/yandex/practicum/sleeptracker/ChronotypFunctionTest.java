package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChronotypFunctionTest {

    private SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                quality
        );
    }

    @Test
    void chronotype_allOwlSessions_returnsOwl() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T09:30", SleepQuality.GOOD),
                session("2025-10-02T23:30", "2025-10-03T09:15", SleepQuality.GOOD),
                session("2025-10-03T23:45", "2025-10-04T10:00", SleepQuality.NORMAL)
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.OWL.getDisplayName(), result.getValue());
    }

    @Test
    void chronotype_allLarkSessions_returnsLark() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T21:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T21:30", "2025-10-03T06:30", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.LARK.getDisplayName(), result.getValue());
    }

    @Test
    void chronotype_tieOwlAndLark_returnsPigeon() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T09:30", SleepQuality.GOOD),
                session("2025-10-02T21:00", "2025-10-03T06:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.PIGEON.getDisplayName(), result.getValue());
    }

    @Test
    void chronotype_mixedMajorityPigeon_returnsPigeon() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T22:30", "2025-10-02T08:00", SleepQuality.GOOD),
                session("2025-10-02T22:00", "2025-10-03T08:30", SleepQuality.GOOD),
                session("2025-10-03T23:15", "2025-10-04T09:30", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.PIGEON.getDisplayName(), result.getValue());
    }
}
