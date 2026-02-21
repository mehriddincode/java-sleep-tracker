package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleeplessNightsFunction implements SleepAnalysisFunction {

    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final String MESSAGE = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult(MESSAGE, "0");
        }

        SleepingSession firstSession = sessions.get(0);
        SleepingSession lastSession = sessions.get(sessions.size() - 1);

        LocalDate firstDate = firstSession.getStartTime().toLocalDate();
        LocalDate lastDate = lastSession.getEndTime().toLocalDate();

        LocalDate firstNightStart = firstSession.getStartTime().getHour() < 12 
                ? firstDate.minusDays(1) 
                : firstDate;

        LocalDate lastNightStart = lastDate.minusDays(1);

        int totalNights = (int) ChronoUnit.DAYS.between(firstNightStart, lastNightStart.plusDays(1));

        Set<LocalDate> sleptNights = sessions.stream()
                .flatMap(this::getCoveredNights)
                .collect(Collectors.toSet());

        long sleptValidNights = sleptNights.stream()
                .filter(date -> !date.isBefore(firstNightStart) && !date.isAfter(lastNightStart))
                .count();

        long sleeplessNights = totalNights - sleptValidNights;

        return new SleepAnalysisResult(MESSAGE, String.valueOf(sleeplessNights));
    }

    private Stream<LocalDate> getCoveredNights(SleepingSession session) {
        LocalDate startNight = session.getStartTime().toLocalDate().minusDays(1);
        LocalDate endNight = session.getEndTime().toLocalDate();
        
        long daysBetween = ChronoUnit.DAYS.between(startNight, endNight.plusDays(1));

        return Stream.iterate(startNight, d -> d.plusDays(1))
                .limit(daysBetween)
                .filter(nightDate -> {
                    LocalDate windowDate = nightDate.plusDays(1);
                    boolean endAfterMidnight = !session.getEndTime().toLocalDate().isBefore(windowDate)
                            && (session.getEndTime().toLocalDate().isAfter(windowDate)
                            || session.getEndTime().toLocalTime().isAfter(LocalTime.MIDNIGHT));

                    boolean startBeforeSix = session.getStartTime().toLocalDate().isBefore(windowDate)
                            || (session.getStartTime().toLocalDate().equals(windowDate)
                            && session.getStartTime().toLocalTime().isBefore(NIGHT_END));

                    return endAfterMidnight && startBeforeSix;
                });
    }
}
