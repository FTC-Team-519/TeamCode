package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class StepCounter {

    private ElapsedTime elapsedTime;
    private int step = 0;
    public StepCounter(int start, ElapsedTime elapsedTime) {
        this.elapsedTime = elapsedTime;
        step = start;
    }

    public void increment() {
        step += 1;
        elapsedTime.reset();
    }

    public void increment(int delta) {
        step += delta;
        elapsedTime.reset();
    }

    public void set(int to) {
        step = to;
        elapsedTime.reset();
    }

    public int getStep() {
        return step;
    }


}
