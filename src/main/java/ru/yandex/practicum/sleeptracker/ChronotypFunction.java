package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypFunction implements SleepAnalysisFunction {

    // OWL:  sleep after 23:00, wake after 09:00
    private static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);

    // LARK: sleep before 22:00, wake before 07:00
    private static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);

    // Night window boundary: sessions must overlap [00:00, 06:00) to be counted as night sleep
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        // Keep only night sleep sessions (overlap the 00:00-06:00 window):
        // session.end > 00:00 on the next day (i.e., spans midnight OR starts after midnight but before 06:00)
        // AND session.start < 06:00 on that same morning.
        // A quick proxy: the session starts before 06:00 on its end date
        // OR the session spans midnight (startDate != endDate).
        List<SleepingSession> nightSessions = sessions.stream()
                .filter(this::isNightSession)
                .collect(Collectors.toList());

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя",
                    ChronoType.PIGEON.getDisplayName());
        }

        // Classify each night session
        Map<ChronoType, Long> counts = nightSessions.stream()
                .collect(Collectors.groupingBy(this::classifySession, Collectors.counting()));

        // Find the dominant chronotype; on tie, default to PIGEON
        ChronoType dominant = counts.entrySet().stream()
                .max(Comparator
                        .comparingLong(Map.Entry<ChronoType, Long>::getValue)
                        .thenComparing(e -> e.getKey() == ChronoType.PIGEON ? 1 : 0))
                .map(Map.Entry::getKey)
                .orElse(ChronoType.PIGEON);

        // If two types have the same count (and neither is PIGEON winning), default to PIGEON.
        long maxCount = counts.values().stream().mapToLong(l -> l).max().orElse(0);
        long typesWithMax = counts.values().stream().filter(v -> v == maxCount).count();
        if (typesWithMax > 1 && dominant != ChronoType.PIGEON) {
            dominant = ChronoType.PIGEON;
        }

        return new SleepAnalysisResult("Хронотип пользователя", dominant.getDisplayName());
    }

    /**
     * A session is a night session if it overlaps the [00:00, 06:00) window.
     * This means it crosses midnight OR starts between 00:00 and 06:00.
     */
    private boolean isNightSession(SleepingSession session) {
        // Crosses midnight: start date != end date
        boolean crossesMidnight = !session.getStartTime().toLocalDate()
                .equals(session.getEndTime().toLocalDate());

        // Starts after midnight (same date as end) but before 06:00
        boolean startsAfterMidnightBeforeSix =
                session.getStartTime().toLocalDate().equals(session.getEndTime().toLocalDate())
                        && session.getStartTime().toLocalTime().isBefore(NIGHT_END);

        return crossesMidnight || startsAfterMidnightBeforeSix;
    }

    private ChronoType classifySession(SleepingSession session) {
        LocalTime sleepTime = session.getStartTime().toLocalTime();
        LocalTime wakeTime = session.getEndTime().toLocalTime();

        // OWL: falls asleep after 23:00 AND wakes after 09:00
        boolean isOwl = !sleepTime.isBefore(OWL_SLEEP_AFTER) && !wakeTime.isBefore(OWL_WAKE_AFTER);

        // LARK: falls asleep before 22:00 AND wakes before 07:00
        boolean isLark = sleepTime.isBefore(LARK_SLEEP_BEFORE) && wakeTime.isBefore(LARK_WAKE_BEFORE);

        if (isOwl) {
            return ChronoType.OWL;
        } else if (isLark) {
            return ChronoType.LARK;
        } else {
            return ChronoType.PIGEON;
        }
    }
}
