package java.util;

import def.js.Array;

import static def.dom.Globals.*;
import static jsweet.util.Lang.function;

public class Timer {
    static int nextSerialNumber = 0;
    private final String name;
    Array<TimerTask> timeouts = new Array<>();
    Array<TimerTask> intervals = new Array<>();

    public Timer() {
        this("Timer-" + ++nextSerialNumber, true);
    }

    public Timer(boolean daemon) {
        this();
    }

    public Timer(String name) {
        this(name, true);
    }

    public Timer(String name, boolean daemon) {
        this.name = name;
    }

    public void schedule(TimerTask task, long delay) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else {
            schedule(task, new Date(System.currentTimeMillis() + delay));
        }
    }

    public void schedule(TimerTask task, Date time) {
        task.nextExecutionTime = time.getTime();

        task.handle = (int) window.setTimeout(function(() -> {
            if (task.state != TimerTask.CANCELLED) {
                task.run();
                task.state = TimerTask.EXECUTED;
            }
            timeouts.splice(timeouts.indexOf(task), 1);
        }), time.getTime() - System.currentTimeMillis());
        timeouts.push(task);

        task.state = TimerTask.SCHEDULED;
    }

    public void schedule(TimerTask task, long delay, long period) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else {
            schedule(task, new Date(System.currentTimeMillis() + delay), period);
        }
    }

    public void schedule(TimerTask task, Date time, long period) {
        if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            task.period = period;
            task.nextExecutionTime = time.getTime();

            task.handle = (int) window.setTimeout(function(() -> {
                if (task.state != TimerTask.CANCELLED) {
                    task.run();
                    schedule(task, period, period);
                } else {
                    timeouts.splice(timeouts.indexOf(task), 1);
                }
            }), time.getTime() - System.currentTimeMillis());
            timeouts.push(task);

            task.state = TimerTask.SCHEDULED;
        }
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if (delay < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        } else {
            scheduleAtFixedRate(task, new Date(System.currentTimeMillis() + delay), period);
        }
    }

    public void scheduleAtFixedRate(TimerTask task, Date time, long period) {
        if (period <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        } else {
            task.period = period;
            task.nextExecutionTime = time.getTime();

            TimerTask start = new TimerTask() {
                @Override
                public void run() {
                    if (task.state != TimerTask.CANCELLED) {
                        task.nextExecutionTime = System.currentTimeMillis() + task.period;

                        task.handle = (int) window.setInterval(function(() -> {
                            if (task.state != TimerTask.CANCELLED) {
                                task.nextExecutionTime = System.currentTimeMillis() + task.period;
                                task.run();
                            } else {
                                clearInterval(task.handle);
                                intervals.splice(intervals.indexOf(task), 1);
                            }
                        }), task.period);
                        intervals.push(task);

                        task.run(); // first run
                    }
                }
            };

            schedule(start, time);
            task.handle = start.handle;
        }
    }

    public void cancel() {
        for (TimerTask task : timeouts) {
            clearTimeout(task.handle);
        }

        for (TimerTask task : intervals) {
            clearInterval(task.handle);
        }

        intervals = new Array<>();
        timeouts = new Array<>();
    }

    public int purge() {
        Array<TimerTask> newTimeouts = timeouts.filter(timerTask -> timerTask.handle != TimerTask.EXECUTED &&
                timerTask.handle != TimerTask.CANCELLED);
        Array<TimerTask> newIntervals = intervals.filter(timerTask -> timerTask.handle != TimerTask.EXECUTED &&
                timerTask.handle != TimerTask.CANCELLED);

        int purged = timeouts.length - newTimeouts.length + intervals.length - newIntervals.length;

        timeouts = newTimeouts;
        intervals = newIntervals;

        return purged;
    }
}
