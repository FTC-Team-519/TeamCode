package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Procrastination", group = "STEM Project")
public class Procrastination extends OpMode {

    private DcMotor rotation;
    private DcMotor bottom;
    private DcMotor top;
    private DcMotor conveyor;

    private static final int MAX_LOOPS_TO_SKIP = 10;
    private int loopsSkipped = 0;

    private double desiredMaxShootingPower = 1.0d;
    private int desiredRotationEnocderValue = 0;

    private boolean desiredMaxShootingPowerAdjusted = false;

    @Override
    public void init() {
        rotation = hardwareMap.dcMotor.get("rotation");
        bottom   = hardwareMap.dcMotor.get("bottom");
        top      = hardwareMap.dcMotor.get("top");
        conveyor = hardwareMap.dcMotor.get("conveyor");

        rotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        top.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    @Override
    public void start() {
        rotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        if (gamepad1.y) {
            desiredRotationEnocderValue = 0;  // Rotate back to starting position
            desiredMaxShootingPower = 1.0;    // Allow user full speed control again
            stop();  // Turn off all of the motors

            // NOTE: This just means don't process anything else while y is being pressed
            return;
        }

        if (gamepad1.dpad_up) {
            conveyor.setPower(-0.4);
        } else if (gamepad1.dpad_down) {
            conveyor.setPower(0.4);
        } else {
            conveyor.setPower(0.0d);
        }

        if (gamepad1.dpad_left && !desiredMaxShootingPowerAdjusted) {
            desiredMaxShootingPower = Math.max(0.0d, desiredMaxShootingPower - 0.01d);
            desiredMaxShootingPowerAdjusted = true;
        } else if (gamepad1.dpad_right && !desiredMaxShootingPowerAdjusted) {
            desiredMaxShootingPower = Math.min(1.0d, desiredMaxShootingPower + 0.01d);
            desiredMaxShootingPowerAdjusted = true;
        } else if (!gamepad1.dpad_left && !gamepad1.dpad_right){
            desiredMaxShootingPowerAdjusted = false;
        }

        double bottom_speed = Math.min(-gamepad1.left_stick_y, desiredMaxShootingPower);
        double top_speed    = Math.min(-gamepad1.left_stick_y, desiredMaxShootingPower);

        bottom.setPower(bottom_speed);
        top.setPower(top_speed);

        if (loopsSkipped >= MAX_LOOPS_TO_SKIP) {
            telemetry.addData("encoder", rotation.getCurrentPosition());
            telemetry.addData("bottom", bottom.getPower());
            telemetry.addData("top", top.getPower());
            telemetry.addData("desiredPower", desiredMaxShootingPower);
            telemetry.addData("desiredEncoder", desiredRotationEnocderValue);
            telemetry.update();

            loopsSkipped = 0;
        } else {
            ++loopsSkipped;
        }
    }

    @Override
    public void stop() {
        rotation.setPower(0.0d);
        bottom.setPower(0.0d);
        top.setPower(0.0d);
        conveyor.setPower(0.0d);
    }
}
