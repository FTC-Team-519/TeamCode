package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name = "Teleop", group = "Iterative OpMode")
public class Teleop extends OpMode {

    Gamepad driver;
    Gamepad gunner;

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    private Motor slider;
    private Motor climber;
    private Motor vertical;
    private Motor collector;

    // front right, back right, backleft, frontleft
    private Servo parker;

    private LimitSwitch limitSwitch;

    private float x;
    private float y;
    private float z;

    private float gunnerLeftStickY;
    private boolean flipDriveDirection = false;

    private void updateJoyStickValues() {
        y = driver.left_stick_y;
        x = driver.left_stick_x;
        z = driver.right_stick_x;

        // NOTE: Check for deadzone before any shaping
        //y = adjustForDeadZone(y);
        //x = adjustForDeadZone(x);
        //z = adjustForDeadZone(z);

        gunnerLeftStickY = gunner.left_stick_y;
        y = shapeInput(y);
        x = shapeInput(x);
        z = shapeInput(z);
    }

    private static float shapeInput(float input) {
        float shapedValue = 0.0f;
        if (input != 0.0f) {
            if (input < 0.0f) {
                shapedValue = input * -input;
            }
            else {
                shapedValue = input * input;
            }
        }

        return shapedValue;
    }

    public float getVerticalMotorPower() {
        float newY = gunnerLeftStickY;

        if (newY < 0) {
            return newY * .8f;
        } else {
            return newY * 1.0f;
        }
    }

    @Override
    public void init() {
        driver = gamepad1;
        gunner = gamepad2;
        frontLeft = new Motor(hardwareMap, "motor1");
        frontRight = new Motor(hardwareMap, "motor2");
        backLeft = new Motor(hardwareMap, "motor3");
        backRight = new Motor(hardwareMap, "motor4");

        frontRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);

        vertical = new Motor(hardwareMap, "vertical");
        climber = new Motor(hardwareMap, "climber");
        slider = new Motor(hardwareMap, "slider"); // encoder
        collector = new Motor(hardwareMap, "collector");
        parker = new Servo(hardwareMap, "parker");
        limitSwitch = new LimitSwitch(hardwareMap);

        vertical.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        // check if orange stick is moved down, if so move it up automatically
        updateJoyStickValues();

        if (Math.abs(driver.left_stick_x)>Math.abs(driver.left_stick_y)){
            y = 0;
        }
        else if (Math.abs(driver.left_stick_y) > Math.abs(driver.left_stick_x)){
            x = 0;
        }

        double pwr = -y;

        double[] motorPowers = MotorUtil.UpdateMotorPowers(pwr, x, z);

        frontRight.getMotor().setPower(motorPowers[MotorUtil.FRONT_RIGHT]);
        frontLeft.getMotor().setPower(motorPowers[MotorUtil.FRONT_LEFT]);
        backRight.getMotor().setPower(motorPowers[MotorUtil.BACK_RIGHT]);
        backLeft.getMotor().setPower(motorPowers[MotorUtil.BACK_LEFT]);

        /* Driver Button Scheme */
        // forward, backward for driving on analog stick
        // left, right strafe left, strafe right

        // right joystick left,right turning
        //x strafe left, b strafe right
        //y sets inversion off, a is inverting

        telemetry.addData("LimitSwitch hit limit", limitSwitch.hasHitLimit() + ";");
        if (driver.dpad_up && !limitSwitch.hasHitLimit()) {
            climber.getMotor().setPower(1);
        } else if (driver.dpad_down) {
            climber.getMotor().setPower(1);
        } else {
            // climber.getMotor().setPower(0.15); // stall
        }

        if (driver.x) {
            // do strafe test
        } else if (driver.b) {
            // do strafe test
        }

        if (driver.y) {
            flipDriveDirection = false;
        } else if (driver.a) {
            flipDriveDirection = true;
        }

        if (flipDriveDirection) {
            x = -x;
            y = -y;
        }

        /* Gunner Button Scheme */
        // climber dpad up, dpad down for moving up and down
        // vertical left analog stick up / down
        // right analog stick slider up / down
        // right trigger held down to spin collector
        // left trigger spin reverse collector to spit out
        // ------ignore limit switch hold down b and will ignore limit switch, lock where it is when b held down


        if (gunner.b) { // override stall
            vertical.getMotor().setPower(.15); // stall
        } else {
            vertical.getMotor().setPower(getVerticalMotorPower());
        }

        if (gunner.right_bumper) {
            collector.getMotor().setPower(-.9);
        } else if (gunner.left_bumper) {
            collector.getMotor().setPower(.9);
        } else {
            collector.getMotor().setPower(0);
        }



    }
}


