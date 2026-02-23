package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime startTime, LocalDateTime endTime, SleepQuality quality) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long durationMinutes() {
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }
}
