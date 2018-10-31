package org.firstinspires.ftc.teamcode;

public class StepCounter {

    private int step = 0;
    public StepCounter(int start) {
        step = start;
    }

    public void increment() {
        step += 1;
    }

    public void increment(int delta) {
        step += delta;
    }

    public void set(int to) {
        step = to;
    }

    public int getStep() {
        return step;
    }


}
