package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {

    // ===================== Helper =====================

    private SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                quality
        );
    }

    // ===================== TotalSessionsFunction =====================

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

    // ===================== MinDurationFunction =====================

    @Test
    void minDuration_singleSession_returnsItsMinutes() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD) // 480 min
        );
        SleepAnalysisResult result = new MinDurationFunction().analyze(sessions);
        assertEquals("480", result.getValue());
    }

    @Test
    void minDuration_multipleSessions_returnsSmallest() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD), // 480
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL), // 50
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.BAD)  // 390
        );
        SleepAnalysisResult result = new MinDurationFunction().analyze(sessions);
        assertEquals("50", result.getValue());
    }

    @Test
    void minDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new MinDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    // ===================== MaxDurationFunction =====================

    @Test
    void maxDuration_singleSession_returnsItsMinutes() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD) // 480 min
        );
        SleepAnalysisResult result = new MaxDurationFunction().analyze(sessions);
        assertEquals("480", result.getValue());
    }

    @Test
    void maxDuration_multipleSessions_returnsLargest() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD), // 480
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL), // 50
                session("2025-10-03T22:00", "2025-10-04T08:00", SleepQuality.BAD)  // 600
        );
        SleepAnalysisResult result = new MaxDurationFunction().analyze(sessions);
        assertEquals("600", result.getValue());
    }

    @Test
    void maxDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new MaxDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    // ===================== AvgDurationFunction =====================

    @Test
    void avgDuration_singleSession_returnsThatDuration() {
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD) // 480 min
        );
        SleepAnalysisResult result = new AvgDurationFunction().analyze(sessions);
        assertEquals("480.0", result.getValue());
    }

    @Test
    void avgDuration_twoSessions_returnsAverage() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD), // 480
                session("2025-10-02T23:00", "2025-10-03T05:00", SleepQuality.GOOD)  // 360
        );
        SleepAnalysisResult result = new AvgDurationFunction().analyze(sessions);
        // average = (480 + 360) / 2 = 420.0
        assertEquals("420.0", result.getValue());
    }

    @Test
    void avgDuration_emptyList_returnsNoData() {
        SleepAnalysisResult result = new AvgDurationFunction().analyze(Collections.emptyList());
        assertEquals("нет данных", result.getValue());
    }

    // ===================== BadQualityCountFunction =====================

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

    // ===================== SleeplessNightsFunction =====================

    /**
     * All nights are slept — 0 sleepless nights.
     */
    @Test
    void sleeplessNights_noSleeplessNights_returnsZero() {
        // Oct 1-2: 23:15 - 07:30 (night covered)
        // Oct 2-3: 23:50 - 06:40 (night covered)
        // Oct 3-4: 23:40 - 08:00 (night covered)
        // Period: night Oct1 to night Oct3 = 3 nights, 0 sleepless
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T23:50", "2025-10-03T06:40", SleepQuality.NORMAL),
                session("2025-10-03T23:40", "2025-10-04T08:00", SleepQuality.BAD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    /**
     * One night with only a daytime nap — that night is sleepless.
     * Matches example from spec: sleep was 17:00-23:00 → sleepless.
     */
    @Test
    void sleeplessNights_oneDaytimeNapOnly_countsOneSleeplessNight() {
        // Night Oct1→Oct2: sleep 23:00 - 07:00 → covered
        // Night Oct2→Oct3: only daytime nap 14:00-15:00 → sleepless
        // Night Oct3→Oct4: sleep 23:30 - 06:00 → covered
        // Period: Oct1 night → Oct3 night = 3 nights total, 1 sleepless
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:00", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T15:00", SleepQuality.NORMAL),
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("1", result.getValue());
    }

    /**
     * Session starts after midnight (before 06:00) — still counts as night sleep.
     * e.g. 2:00 - 5:00 covers the night window.
     */
    @Test
    void sleeplessNights_sessionStartsAfterMidnightBeforeSix_notSleepless() {
        // Sleep 02:00 - 05:00 on Oct 2 → covers night Oct1→Oct2
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:00", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T14:00", "2025-10-02T14:50", SleepQuality.NORMAL),
                session("2025-10-03T02:00", "2025-10-03T05:00", SleepQuality.GOOD),
                session("2025-10-03T23:50", "2025-10-04T06:10", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    /**
     * Session only at 07:00 - 11:00 — does NOT cross the 00:00-06:00 window → sleepless night.
     */
    @Test
    void sleeplessNights_sessionOnlyAfterSix_countsSleepless() {
        // Night Oct2→Oct3: sleep in only after 06:00 and only on Oct3 → sleepless
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T07:30", SleepQuality.GOOD),
                session("2025-10-02T07:00", "2025-10-02T11:00", SleepQuality.BAD), // after 06:00, sleepless Oct2 night
                session("2025-10-03T23:30", "2025-10-04T06:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("1", result.getValue());
    }

    /**
     * Month boundary: logging spans October to November.
     */
    @Test
    void sleeplessNights_monthBoundary_handledCorrectly() {
        // Night Oct31→Nov1: session 30.10 23:50 - 31.10 06:30 → covered
        // Night Nov1→Nov2 would be the next, but there's no session → sleepless? No, one session only.
        // Period: only 1 night total → 0 sleepless
        List<SleepingSession> sessions = Collections.singletonList(
                session("2025-10-30T23:50", "2025-10-31T06:30", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    /**
     * Edge: first session starts after noon → first night is the same calendar night.
     */
    @Test
    void sleeplessNights_firstSessionAfterNoon_firstNightIsSameNight() {
        // Starts with daytime nap after noon on Oct2 → first potential night is Oct2→Oct3
        // Then Oct2→Oct3 is covered by the second session
        // Period: Oct2 night only → 0 sleepless
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-02T13:00", "2025-10-02T14:00", SleepQuality.NORMAL),
                session("2025-10-02T23:00", "2025-10-03T07:00", SleepQuality.GOOD)
        );
        SleepAnalysisResult result = new SleeplessNightsFunction().analyze(sessions);
        assertEquals("0", result.getValue());
    }

    // ===================== ChronotypFunction =====================

    @Test
    void chronotype_allOwlSessions_returnsOwl() {
        // OWL: sleep after 23:00, wake after 09:00
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
        // LARK: sleep before 22:00, wake before 07:00
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T21:00", "2025-10-02T06:00", SleepQuality.GOOD),
                session("2025-10-02T21:30", "2025-10-03T06:30", SleepQuality.GOOD) // wake 06:30 >= 07:00? No, 06:30 < 07:00 → LARK
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.LARK.getDisplayName(), result.getValue());
    }

    @Test
    void chronotype_tieOwlAndLark_returnsPigeon() {
        // 1 OWL, 1 LARK → tie → PIGEON
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T23:15", "2025-10-02T09:30", SleepQuality.GOOD), // OWL
                session("2025-10-02T21:00", "2025-10-03T06:00", SleepQuality.GOOD)  // LARK
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.PIGEON.getDisplayName(), result.getValue());
    }

    @Test
    void chronotype_mixedMajorityPigeon_returnsPigeon() {
        List<SleepingSession> sessions = Arrays.asList(
                session("2025-10-01T22:30", "2025-10-02T08:00", SleepQuality.GOOD), // PIGEON
                session("2025-10-02T22:00", "2025-10-03T08:30", SleepQuality.GOOD), // PIGEON
                session("2025-10-03T23:15", "2025-10-04T09:30", SleepQuality.GOOD)  // OWL
        );
        SleepAnalysisResult result = new ChronotypFunction().analyze(sessions);
        assertEquals(ChronoType.PIGEON.getDisplayName(), result.getValue());
    }
}