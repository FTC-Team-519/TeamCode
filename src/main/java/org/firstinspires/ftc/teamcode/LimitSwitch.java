package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LimitSwitch {

    private DigitalChannel limitSwitch;

    public LimitSwitch(HardwareMap map) {
        limitSwitch = map.get(DigitalChannel.class, "LimitSwitch");
        limitSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    public boolean hasHitLimit() {
        return !limitSwitch.getState();
    }
}
