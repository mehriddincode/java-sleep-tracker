package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypFunction implements SleepAnalysisFunction {

    private static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);

    private static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);

    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    private static final String MESSAGE = "Хронотип пользователя";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        List<SleepingSession> nightSessions = sessions.stream()
                .filter(this::isNightSession)
                .collect(Collectors.toList());

        String resultValue;

        if (nightSessions.isEmpty()) {
            resultValue = ChronoType.PIGEON.getDisplayName();
        } else {
            Map<ChronoType, Long> counts = nightSessions.stream()
                    .collect(Collectors.groupingBy(this::classifySession, Collectors.counting()));

            ChronoType dominant = counts.entrySet().stream()
                    .max(Comparator
                            .comparingLong(Map.Entry<ChronoType, Long>::getValue)
                            .thenComparing(e -> e.getKey() == ChronoType.PIGEON ? 1 : 0))
                    .map(Map.Entry::getKey)
                    .orElse(ChronoType.PIGEON);

            long maxCount = counts.values().stream().mapToLong(l -> l).max().orElse(0);
            long typesWithMax = counts.values().stream().filter(v -> v == maxCount).count();
            if (typesWithMax > 1 && dominant != ChronoType.PIGEON) {
                dominant = ChronoType.PIGEON;
            }

            resultValue = dominant.getDisplayName();
        }

        return new SleepAnalysisResult(MESSAGE, resultValue);
    }

    private boolean isNightSession(SleepingSession session) {

        boolean crossesMidnight = !session.getStartTime().toLocalDate()
                .equals(session.getEndTime().toLocalDate());

        boolean startsAfterMidnightBeforeSix =
                session.getStartTime().toLocalDate().equals(session.getEndTime().toLocalDate())
                        && session.getStartTime().toLocalTime().isBefore(NIGHT_END);

        return crossesMidnight || startsAfterMidnightBeforeSix;
    }

    private ChronoType classifySession(SleepingSession session) {
        LocalTime sleepTime = session.getStartTime().toLocalTime();
        LocalTime wakeTime = session.getEndTime().toLocalTime();

        boolean isOwl = !sleepTime.isBefore(OWL_SLEEP_AFTER) && !wakeTime.isBefore(OWL_WAKE_AFTER);

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
