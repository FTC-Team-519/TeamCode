package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.HardwareMap;

// descend, sample, drive and park
// descend, sample, drive to safe zone and place team marker, (drive to crater? (park?))
public class Servo {
    private Servo servo;

    public Servo(HardwareMap map, String name) {
        servo = map.get(Servo.class, name);
    }

    public void setPosition(double to) {
        servo.setPosition(to);
    }

    public double getPosition() {
        return servo.getPosition();
    }
}
