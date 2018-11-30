package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Teleop", group = "Iterative OpMode")
public class Teleop extends OpMode {

    Gamepad driver;
    Gamepad gunner;

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    private Servo righty;
    private Servo lefty;

    private boolean parkerMoving = false;
    private Motor slider;
    private Motor climber;
    private Motor vertical;
    private Motor collector;

    private ElapsedTime parkerElapsedTime;
    private ElapsedTime parkerJuniorElapsedTime;
    // front right, back right, backleft, frontleft
    private Servo marker;
    private Servo parker;
    private Servo parkerjr;

    private LimitSwitch limitSwitch;

    private float x;
    private float y;
    private float z;

    private float gunnerLeftStickY;
    private float gunnerRightStickY;

    private boolean flipDriveDirection = false;

    private double parkerCurrentPosition;
    private double parkerPositionIncrement = .01;

    private void updateJoyStickValues() {
        y = driver.left_stick_y;
        x = driver.left_stick_x;
        z = driver.right_stick_x;

        // NOTE: Check for deadzone before any shaping
        //y = adjustForDeadZone(y);
        //x = adjustForDeadZone(x);
        //z = adjustForDeadZone(z);

        gunnerLeftStickY = gunner.left_stick_y;
        gunnerRightStickY = gunner.right_stick_y;

        y = shapeInput(y);
        x = shapeInput(x);
        z = shapeInput(z);
    }

    private static float shapeInput(float input) {
        float shapedValue = 0.0f;
        if (input != 0.0f) {
            if (input < 0.0f) {
                shapedValue = input * -input;
            } else {
                shapedValue = input * input;
            }
        }

        return shapedValue;
    }

    public float getVerticalMotorPower() {
        float newY = gunnerRightStickY;

        // telemetry.addData("Vertical Motor Value", newY + "");
        if (newY < 0) {
            return newY * .9f;
        } else {

            return newY * 1.0f;
        }
    }

    public float getSliderMotorPower() {
        float newY = gunnerLeftStickY;

        telemetry.addData("Slider Motor Y Orig Value", newY + "");
        if (newY < 0) {
            return newY * .9f;
        } else {
            return newY * 1f;
        }
    }

    @Override
    public void start() {
        slider.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        vertical.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        righty.setPosition(0.5);
        lefty.setPosition(0.5);
        marker.setPosition(0.5);
        parker.setPosition(0.69d);
        marker.setPosition(1);
        parkerjr.setPosition(0.05d);
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
        marker = new Servo(hardwareMap, "marker");
        parker = new Servo(hardwareMap, "parker");
        righty = new Servo(hardwareMap, "righty");
        lefty = new Servo(hardwareMap, "lefty");
        limitSwitch = new LimitSwitch(hardwareMap);

        vertical.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // climber.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slider.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slider.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertical.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        parkerElapsedTime = new ElapsedTime();
        parkerJuniorElapsedTime = new ElapsedTime();
        parkerjr = new Servo(hardwareMap, "parkerjr");
    }

    @Override
    public void loop() {
        // check if orange stick is moved down, if so move it up automatically
        updateJoyStickValues();

        if (Math.abs(driver.left_stick_x) > Math.abs(driver.left_stick_y)) {
            y = 0;
        } else if (Math.abs(driver.left_stick_y) > Math.abs(driver.left_stick_x)) {
            x = 0;
        }

        if (flipDriveDirection) {
            x = -x;
            y = -y;
        }

        double pwr = -y;

        double[] motorPowers = MotorUtil.UpdateMotorPowers(pwr, x, z);

        if (!driver.x && !driver.b) {
            frontRight.getMotor().setPower(motorPowers[MotorUtil.FRONT_RIGHT]);
            frontLeft.getMotor().setPower(motorPowers[MotorUtil.FRONT_LEFT]);
            backRight.getMotor().setPower(motorPowers[MotorUtil.BACK_RIGHT]);
            backLeft.getMotor().setPower(motorPowers[MotorUtil.BACK_LEFT]);
        } else {
            if (driver.x) {
                frontLeft.getMotor().setPower(-1);
                frontRight.getMotor().setPower(0.95);//1
                backRight.getMotor().setPower(-1);
                backLeft.getMotor().setPower(0.95);//1
            } else {
                frontLeft.getMotor().setPower(1);
                frontRight.getMotor().setPower(-0.95);//1
                backRight.getMotor().setPower(1);
                backLeft.getMotor().setPower(-0.95);//1
            }
        }

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
            climber.getMotor().setPower(-1);
        } else {
            // climber.getMotor().setPower(0.15); // stall
            climber.getMotor().setPower(0);
        }

        if (driver.y) {
            flipDriveDirection = false;
        } else if (driver.a) {
            flipDriveDirection = true;
        }

        if (driver.right_bumper) {
            marker.setPosition(.2);
        }

        /* Gunner Button Scheme */
        // climber dpad up, dpad down for moving up and down
        // vertical left analog stick up / down
        // right analog stick slider up / down
        // right trigger held down to spin collector
        // left trigger spin reverse collector to spit out
        // ------ignore limit switch hold down b and will ignore limit switch, lock where it is when b held down

        telemetry.addData("Vertical Encoder Value", vertical.getMotor().getCurrentPosition());
        float verticalMotorPower = getVerticalMotorPower();
        if (verticalMotorPower > 0) {
            if (vertical.getMotor().getCurrentPosition() > -5) {
                vertical.getMotor().setPower(0);
            } else {
                vertical.getMotor().setPower(verticalMotorPower);
            }
        } else {
            vertical.getMotor().setPower(verticalMotorPower);
        }

        if (gunner.right_bumper || gunner.right_trigger > 0) {
            collector.getMotor().setPower(-.9);
        } else if (gunner.left_bumper || gunner.left_trigger > 0) {
            collector.getMotor().setPower(.6);
        } else {
            collector.getMotor().setPower(0);
        }


        float sliderMotorPower = getSliderMotorPower();
        telemetry.addData("Slider Motor Power After Done", sliderMotorPower + "");
        telemetry.addData("Slider Encoder Value", slider.getMotor().getCurrentPosition() + "");
        if (gunner.a) {
            slider.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slider.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        if (sliderMotorPower > 0) {
            if (gunner.b) {
                slider.getMotor().setPower(sliderMotorPower);
            } else {
                if (slider.getMotor().getCurrentPosition() < 0) {
                    slider.getMotor().setPower(sliderMotorPower);
                } else {
                    slider.getMotor().setPower(0);
                }
            }
        } else {
            if (gunner.b) {
                slider.getMotor().setPower(sliderMotorPower);
            } else {
                if (slider.getMotor().getCurrentPosition() > -1270) {
                    slider.getMotor().setPower(sliderMotorPower);
                } else {
                    slider.getMotor().setPower(0);
                }
            }
        }

        if (gunner.x) {
            // Ensure that it's been held down for a second
            if (parkerElapsedTime.time() >= 1) {
                parkerElapsedTime.reset();
                parkerMoving = true;
                //parker.setPosition(0);
                //parkerjr.setPosition(1);
            }
        } else {
            parkerElapsedTime.reset();
        }

        if (parkerMoving) {
            if (parker.getPosition() <= .01) {
                if (parkerjr.getPosition() >= .99) {
                    parkerMoving = false;
                } else {
                    parkerjr.setPosition(parkerjr.getPosition() + .029);
                }
            } else {
                parker.setPosition(parker.getPosition() - .009);
                //parkerJuniorElapsedTime.reset();
            }
        }

        /*if (parkerMoving) {
            if (parkerJuniorElapsedTime.time() > 1.5) {
                parkerjr.setPosition(1);
                if (parkerJuniorElapsedTime.time() > 2) {
                    parkerMoving = false;
                }
            } else {
                parkerjr.setPosition(.1);
            }
        } else {
            parkerJuniorElapsedTime.reset();
        }*/

    }
}


