package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "ParkerTest", group = "Iterative OpMode")

public class TestStuff extends OpMode {

    Gamepad driver;
    Gamepad gunner;

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    private Servo righty;
    private Servo lefty;

    private Servo parkerjr;

    private Motor slider;
    private Motor climber;
    private Motor vertical;
    private Motor collector;

    // front right, back right, backleft, frontleft
    private Servo marker;
    private Servo parker;

    private LimitSwitch limitSwitch;

    private float x;
    private float y;
    private float z;

    private float gunnerLeftStickY;
    private float gunnerRightStickY;

    private boolean flipDriveDirection = false;

    private double servoPosition = 0;
    private static final double INCREMENT = 0.005d;

    @Override
    public void init() {

        driver = gamepad1;

        parkerjr = new Servo(hardwareMap, "parkerjr");
        //parker = new Servo(hardwareMap, "parker");
    }

    @Override
    public void start() {

        parkerjr.setPosition(servoPosition);
        //parker.setPosition(.5);
    }

    @Override
    public void loop() {
        if (driver.dpad_up ) {
            servoPosition += INCREMENT;
        } else if (driver.dpad_down) {
            servoPosition -= INCREMENT;
        }
        parkerjr.setPosition(servoPosition);
        telemetry.addData("Servo Position of Parker Jr", parkerjr.getPosition());
    }
}
