package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleeplessNightsFunction implements SleepAnalysisFunction {

    // The "night window" – a night is considered slept if a session overlaps [00:00, 06:00).
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "0");
        }

        // Determine first and last date of the logging period.
        LocalDate firstDate = sessions.get(0).getStartTime().toLocalDate();
        LocalDate lastDate = sessions.get(sessions.size() - 1).getEndTime().toLocalDate();

        // Determine the first "potential" night.
        // If first session starts after 12:00 – the night is (firstDate -> firstDate+1).
        // If before 12:00 – the night is (firstDate-1 -> firstDate).
        LocalDate firstNightStart = sessions.get(0).getStartTime().getHour() >= 12
                ? firstDate
                : firstDate.minusDays(1);

        // Determine the last "potential" night.
        // The last night ends on lastDate (night is lastDate-1 -> lastDate).
        LocalDate lastNightStart = lastDate.minusDays(1);

        // Total number of nights in the logging period.
        int totalNights = Period.between(firstNightStart, lastNightStart.plusDays(1)).getDays();

        // Collect the set of nights that were "slept" –
        // i.e., nights (represented by their start date D) where a session overlaps [D+1 00:00, D+1 06:00).
        // A session overlaps the night window [D 23:xx ... D+1 06:00] if:
        //   - The session ends on date D+1 (or later) AND ends after 00:00 (always true) AND
        //     EITHER ends after 06:00 OR starts before 06:00 on D+1.
        //
        // More precisely: the session overlaps [nightDate 00:00, nightDate 06:00) where nightDate = D+1:
        //   session.start < nightDate 06:00  AND  session.end > nightDate 00:00
        //   => session.start < (D+1) 06:00   AND  session.end > (D+1) 00:00
        //   => (session.startDate < D+1) OR (session.startDate == D+1 AND session.startTime < 06:00)
        //      AND
        //      session.endDate >= D+1

        Set<LocalDate> sleptNights = sessions.stream()
                .flatMap(session -> coveredNights(session, firstNightStart, lastNightStart))
                .collect(Collectors.toSet());

        int sleeplessNights = totalNights - sleptNights.size();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                String.valueOf(sleeplessNights)
        );
    }

    /**
     * Returns a stream of "night start dates" (D) that the given session covers.
     * A night D is covered if the session overlaps the window [D+1 00:00, D+1 06:00).
     * Equivalently: session.start < (D+1) 06:00  AND  session.end > (D+1) 00:00
     *   => session covers midnight D->D+1  OR  session starts after midnight but before 06:00 on D+1.
     */
    private Stream<LocalDate> coveredNights(SleepingSession session, LocalDate firstNightStart,
                                            LocalDate lastNightStart) {
        // For a session to cover night D (where night = D 23:xx -> D+1 06:xx):
        // session.start < (D+1) 06:00  AND  session.end > (D+1) 00:00
        // We iterate over all nights and check, but since we must avoid loops we use streams.

        // The "night date" D means the night window is [(D+1) 00:00 .. (D+1) 06:00).
        // Collect all candidate night start dates in the period.
        long nightCount = Period.between(firstNightStart, lastNightStart.plusDays(1)).getDays();

        return Stream.iterate(firstNightStart, d -> d.plusDays(1))
                .limit(nightCount)
                .filter(nightDate -> {
                    // The sleep window we care about is (nightDate+1) 00:00 to (nightDate+1) 06:00
                    LocalDate windowDate = nightDate.plusDays(1);

                    // session.end > windowDate 00:00  ↔  session.endDate > windowDate
                    //                                    OR (session.endDate == windowDate AND session.endTime > 00:00)
                    //                                    – always true if endDate >= windowDate and duration > 0
                    boolean endAfterMidnight = !session.getEndTime().toLocalDate().isBefore(windowDate)
                            && (session.getEndTime().toLocalDate().isAfter(windowDate)
                            || session.getEndTime().toLocalTime().isAfter(LocalTime.MIDNIGHT));

                    // session.start < windowDate 06:00 ↔  startDate < windowDate
                    //                                      OR (startDate == windowDate AND startTime < 06:00)
                    boolean startBeforeSix = session.getStartTime().toLocalDate().isBefore(windowDate)
                            || (session.getStartTime().toLocalDate().equals(windowDate)
                            && session.getStartTime().toLocalTime().isBefore(NIGHT_END));

                    return endAfterMidnight && startBeforeSix;
                });
    }
}
