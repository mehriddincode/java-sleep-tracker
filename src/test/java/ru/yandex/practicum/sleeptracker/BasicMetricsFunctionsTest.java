package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicMetricsFunctionsTest {

    private SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                quality
        );
    }

    @Test
    void totalSessions_emptyList_returnsZero() {
        SleepAnalysisResult result = new TotalSessionsFunction().analyze(Collections.emptyList());
        assertEquals("0", result.getValue());
    }

    @Test
    void totalSessions_threeSessions_returnsThree() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.NORMAL),
                session("2025-10-03T14:00", "2025-10-03T15:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new TotalSessionsFunction().analyze(sessions);
        assertEquals("3", result.getValue());
    }

    @Test
    void minDuration_singleSession_returnsItsMinutes() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new MinDurationFunction().analyze(sessions);
        assertEquals("480", result.getValue());
    }

    @Test
    void minDuration_multipleSessions_returnsSmallest() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL),
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new MinDurationFunction().analyze(sessions);
        assertEquals("50", result.getValue());
    }

    @Test
    void minDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new MinDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    @Test
    void maxDuration_singleSession_returnsItsMinutes() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new MaxDurationFunction().analyze(sessions);
        assertEquals("480", result.getValue());
    }

    @Test
    void maxDuration_multipleSessions_returnsLargest() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL),
                session("2025-10-03T22:00", "2025-10-04T08:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new MaxDurationFunction().analyze(sessions);
        assertEquals("600", result.getValue());
    }

    @Test
    void maxDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new MaxDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    @Test
    void avgDuration_singleSession_returnsThatDuration() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new AvgDurationFunction().analyze(sessions);
        assertEquals("480.0", result.getValue());
    }

    @Test
    void avgDuration_twoSessions_returnsAverage() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T05:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new AvgDurationFunction().analyze(sessions);

        assertEquals("420.0", result.getValue());
    }

    @Test
    void avgDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new AvgDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    @Test
    void badQualityCount_noBadSessions_returnsZero() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.NORMAL)
        );
        SleepAnalysisResult result = new BadQualityCountFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    @Test
    void badQualityCount_twoBadSessions_returnsTwo() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.BAD),
                session("2025-10-03T14:00", "2025-10-03T15:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new BadQualityCountFunction().analyze(sessions);
        assertEquals("2", result.getValue());
    }

}
