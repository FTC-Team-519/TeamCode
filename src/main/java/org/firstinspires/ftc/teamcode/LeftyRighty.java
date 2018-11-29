package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "LeftyRighty", group = "Test OpModes")
public class LeftyRighty extends OpMode {

    private Servo lefty;
    private Servo righty;

    @Override
    public void init() {
        lefty = new Servo(hardwareMap, "lefty");
        righty = new Servo(hardwareMap,"righty");


    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        telemetry.addData("Lefty", lefty.getPosition() + "");
        telemetry.addData("Righty", righty.getPosition() + "");

        lefty.setPosition(gamepad1.left_stick_x);
        righty.setPosition(gamepad1.right_stick_x);
    }
}
