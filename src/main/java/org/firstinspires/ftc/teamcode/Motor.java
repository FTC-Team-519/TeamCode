package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motor {

    private DcMotor motor;

    public Motor(HardwareMap map, String name) {
        motor = map.get(DcMotor.class, name);
    }

    public DcMotor getMotor() {
        return motor;
    }
}
