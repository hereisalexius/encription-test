/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafiles;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author hereisalexius
 */
public class TimerMonitor {

    private static final TimerMonitor tm = new TimerMonitor();

    private long startTime;
    private long stopTime;

    private TimerMonitor() {
        this.startTime = 0;
        this.stopTime = 0;
    }

    public static TimerMonitor getInstance() {
        return tm;
    }

    public long start() {
        startTime = System.currentTimeMillis();
        return startTime;
    }

    public long stop() {
        stopTime = System.currentTimeMillis();
        return result();
    }

    public long result() {
        return stopTime - startTime;
    }

    public static String convertTime(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
