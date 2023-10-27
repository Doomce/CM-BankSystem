package net.dom.bank.Util;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.Calendar;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import net.dom.bank.Bank;

public class Timer
{
    private final Bank bank;
    
    public Timer(final Bank bank) {
        this.bank = bank;
        ExecuteEvery24H(23, 0, 0, 1728000L);
    }
    
    public void ExecuteEvery24H(int Hour, int Minute, int Second, long GameTick) {
        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long seconds = duration.getSeconds();
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(11) > Hour) {
            calendar.add(5, 1);
        }
        else if (calendar.get(11) == Hour) {
            if (calendar.get(12) > Minute) {
                calendar.add(5, 1);
            }
            else if (calendar.get(12) == Minute && calendar.get(13) >= Second) {
                calendar.add(5, 1);
            }
        }
        calendar.set(11, Hour);
        calendar.set(12, Minute);
        calendar.set(13, Second);
        calendar.set(14, 0);
        Instant restart = calendar.toInstant();
        Duration restartDuration = Duration.between(midnight, restart);
        long restartSeconds = restartDuration.getSeconds();
        long finalSeconds = restartSeconds - seconds;
        Bukkit.getScheduler().runTaskTimerAsynchronously(bank, () -> {
            Bank.getModule().Interest.InterestTask();
        }, finalSeconds * 20L, GameTick);
    }
}
