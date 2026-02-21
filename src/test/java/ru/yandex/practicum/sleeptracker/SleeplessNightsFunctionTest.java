package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleeplessNightsFunctionTest {

    private SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                quality
        );
    }

    @Test
    void sleeplessNights_noSleeplessNights_returnsZero() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T23:50", "2025-10-03T06:40", SleepQuality.NORMAL),
                session("2025-10-03T23:40", "2025-10-04T08:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_oneDaytimeNapOnly_countsOneSleeplessNight() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:00", SleepQuality.NORMAL),
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("1", result.getValue());
    }

    @Test
    void sleeplessNights_sessionStartsAfterMidnightBeforeSix_notSleepless() {
        
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL),
                session("2025-10-03T02:00", "2025-10-03T05:00", SleepQuality.GOOD),
                session("2025-10-03T23:50", "2025-10-04T06:10", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_sessionOnlyAfterSix_countsSleepless() {
        
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T07:00", "2025-10-02T11:00", SleepQuality.BAD), 
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("1", result.getValue());
    }

    @Test
    void sleeplessNights_monthBoundary_handledCorrectly() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-30T23:50", "2025-10-31T06:30", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_firstSessionAfterNoon_firstNightIsSameNight() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-02T13:00", "2025-10-02T14:00", SleepQuality.NORMAL),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void sleeplessNights_firstSessionStartsAfterMidnight_countsCorrectly() {

        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-02T02:00", "2025-10-02T05:00", SleepQuality.GOOD),
                session("2025-10-04T02:00", "2025-10-04T05:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("1", result.getValue());
    }
}
