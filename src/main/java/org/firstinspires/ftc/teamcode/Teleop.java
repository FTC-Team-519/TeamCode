package org.firstinspires.ftc.teamcode;

import android.content.Context;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.FileOutputStream;

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
    private Servo scorer;

    private boolean parkerMovingOut = false;
    private boolean parkerMovingIn = true;

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
    private boolean resetHorizontalLift = false;

    private double parkerCurrentPosition;
    private double parkerPositionIncrement = .01;
    private double scorerValue = 0.1;

    private boolean IS_RECORDING_ENABLED = false; // todo: TURN OFF WHEN WE NEED TO.
    private FileOutputStream outputStream;
    private BlackBox.Recorder recorder;

    private void updateJoyStickValues() {
        y = driver.left_stick_y;
        x = driver.left_stick_x;
        z = driver.right_stick_x;

        // NOTE: Check for deadzone before any shaping
        //y = adjustForDeadZone(y);
        //x = adjustForDeadZone(x);
        //z = adjustForDeadZone(z);

        gunnerLeftStickY = -gunner.left_stick_y;  // NOTE: Switch if spools inverted
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
        float newY = shapeInput(gunnerLeftStickY);

        telemetry.addData("Slider Motor Y Orig Value", newY + "");
        if (newY < 0) {
            return newY * .9f;
        } else {
            return newY * 1f;
        }
    }

    @Override
    public void start() {
        super.start();
        slider.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        vertical.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        righty.setPosition(0.5);
        lefty.setPosition(0.5);
        marker.setPosition(0.5);
        //parker.setPosition(0.69d);
        marker.setPosition(1);
        //parkerjr.setPosition(0.05d);
    }

    @Override
    public void stop() {
        super.stop();
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        marker = new Servo(hardwareMap, "marker");
        parker = new Servo(hardwareMap, "parker");
        righty = new Servo(hardwareMap, "righty");
        lefty = new Servo(hardwareMap, "lefty");
        scorer = new Servo(hardwareMap, "scorer");
        limitSwitch = new LimitSwitch(hardwareMap);

        vertical.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // climber.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slider.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slider.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertical.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        parkerElapsedTime = new ElapsedTime();
        parkerJuniorElapsedTime = new ElapsedTime();

        if (IS_RECORDING_ENABLED) {
            // Attempt to initialize
            try {
                // Open a file named "recordedTeleop" in the app's folder.
                outputStream = hardwareMap.appContext.openFileOutput("recordedTeleop",
                        Context.MODE_PRIVATE);
                // Setup a hardware recorder.
                recorder = new BlackBox.Recorder(hardwareMap, outputStream);
            } catch (Exception e) {
                e.printStackTrace();
                requestOpModeStop();
            }
        }
        parkerjr = new Servo(hardwareMap, "parkerjr");
    }

    private boolean wantsToRecord = false;
    private ElapsedTime recordTimer = new ElapsedTime();
    private ElapsedTime frameTimer = new ElapsedTime();

    @Override
    public void loop() {
        // check if orange stick is moved down, if so move it up automatically
        updateJoyStickValues();


        if (driver.start && frameTimer.time() > 1) {
            frameTimer.reset();
            recordTimer.reset();
            wantsToRecord = !wantsToRecord;
        }

        telemetry.addData("Recording", wantsToRecord + "");
        if (IS_RECORDING_ENABLED && wantsToRecord) {
            try {
                double t = recordTimer.time();
                recorder.recordDevice("motor1", t);
                recorder.recordDevice("motor2", t);
                recorder.recordDevice("motor3", t);
                recorder.recordDevice("motor4", t);
                telemetry.addData("Recording time", t + "");
            } catch (Exception e) {
                e.printStackTrace();
                requestOpModeStop();
            }
        }

       /* if (Math.abs(driver.left_stick_x) > Math.abs(driver.left_stick_y)) {
            y = 0;
        } else if (Math.abs(driver.left_stick_y) > Math.abs(driver.left_stick_x)) {
            x = 0;
        }*/

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
            marker.setPosition(.6);
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
        if (gunner.b) {
            vertical.getMotor().setPower(verticalMotorPower);
        } else if (verticalMotorPower > 0) {
            if (vertical.getMotor().getCurrentPosition() > -5) {
                vertical.getMotor().setPower(0);
            } else {
                vertical.getMotor().setPower(verticalMotorPower);
            }
        } else {
            vertical.getMotor().setPower(verticalMotorPower);
        }


        /*if (gunner.right_bumper || gunner.right_trigger > 0) {
            scorer.getServo().setPosition(1.0);
        } else if (gunner.left_bumper || gunner.left_trigger > 0) {
            scorer.getServo().setPosition(0.0);
        } else {
            scorer.getServo().setPosition(0.5);
        }*/


        if (gunner.right_trigger > 0 && gunner.right_bumper) {
            collector.getMotor().setPower(-1.0);
        } else if (gunner.left_trigger > 0 && gunner.left_bumper) {
            collector.getMotor().setPower(1.0);
        } else if (gunner.left_trigger > 0 || gunner.left_bumper) {
            collector.getMotor().setPower(.6);
        } else if (gunner.right_bumper || gunner.right_trigger > 0) {
            collector.getMotor().setPower(-.6);
        } else {
            collector.getMotor().setPower(0);
        }

        if (gunner.dpad_up || gunner.dpad_down || gunner.dpad_right || gunner.dpad_left) {
            scorer.getServo().setPosition(1.0);
        } else {
            scorer.getServo().setPosition(.5);
        }
        /*if (gunner.dpad_up) {
            collector.getMotor().setPower(0.9);
        } else if (gunner.dpad_down) {
            collector.getMotor().setPower(-0.9);
        } else if (gunner.dpad_left) {
            collector.getMotor().setPower(-.6);
        } else if (gunner.dpad_right) {
            collector.getMotor().setPower(0.6);
        } else {
            collector.getMotor().setPower(0.0);
        }*/

        float sliderMotorPower = getSliderMotorPower();
        telemetry.addData("Slider Motor Power After Done", sliderMotorPower + "");
        telemetry.addData("Slider Encoder Value", slider.getMotor().getCurrentPosition() + "");
        if (gunner.a) {
            slider.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slider.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            vertical.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            vertical.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        if (gunner.b) {
            slider.getMotor().setPower(sliderMotorPower);
        } else if (sliderMotorPower < 0) {
            if (slider.getMotor().getCurrentPosition() > 0) {
                slider.getMotor().setPower(sliderMotorPower);
            } else {
                slider.getMotor().setPower(0);
            }
        } else {
            if (slider.getMotor().getCurrentPosition() < 3200) {
                slider.getMotor().setPower(sliderMotorPower);
            } else {
                slider.getMotor().setPower(0);
            }
        }

        if (gunner.y && !resetHorizontalLift) { //reset horizontal lift back to original position
           resetHorizontalLift = true;
        }

        if (resetHorizontalLift) {
            if (gunner.y) {
                if (slider.getMotor().getCurrentPosition() > 0) {
                    slider.getMotor().setPower(-0.3);
                } else if (slider.getMotor().getCurrentPosition() < 0) {
                    slider.getMotor().setPower(0.3);
                }
                else { // currentPosition = 0, do not move
                    slider.getMotor().setPower(0);
                }
            } else {  // y no longer pressed, abort
                slider.getMotor().setPower(0);
                resetHorizontalLift = false;
            }
        }

        if(slider.getMotor().getCurrentPosition() == 0) {
            slider.getMotor().setPower(0);
        }
        if (gunner.x) { //stall vertical
            vertical.getMotor().setPower(-0.3);
        }


        if (driver.left_bumper) {
            // Ensure that it's been held down for a second
            if (parkerElapsedTime.time() >= 1) {
                parkerElapsedTime.reset();
                if (parkerjr == null) {
                    parkerjr = new Servo(hardwareMap, "parkerjr");
                }

                parkerMovingOut = true;
                parker.setPosition(0);
                parkerjr.setPosition(1);
            }
        } else if (driver.right_bumper) {
            if (parkerElapsedTime.time() >= 1) {
                if (parkerjr == null) {
                } else {
                    parkerMovingIn = true;
                    parkerElapsedTime.reset();
                }
            }
        } else {
            parkerElapsedTime.reset();
        }

        if (parkerMovingOut) {
            if (parker.getPosition() <= .01) {
                if (parkerjr.getPosition() >= .99) {
                    telemetry.addData("->", "ParkerMovingOut finished.");
                    parkerMovingOut = false; // parker jr ends at .99
                } else {
                    telemetry.addData("ParkerJR position", parkerjr.getPosition());
                    parkerjr.setPosition(parkerjr.getPosition() + .029);
                }
            } else {
                parker.setPosition(parker.getPosition() - .009); // parker ends at .01, ends at
                telemetry.addData("->", "Parker position", parker.getPosition() + "");
                //parkerJuniorElapsedTime.reset();
            }
        } else if (parkerMovingIn) {
            if (parkerjr.getPosition() <= .01) {
                if (parker.getPosition() >= .69) {
                    telemetry.addData("<-", "ParkerMovingIn finished.");
                    parkerMovingIn = false;
                } else {
                    parker.setPosition(parker.getPosition() + .009);
                }
            } else {
                parkerjr.setPosition(parker.getPosition() - .029);
            }
        }

        telemetry.update();


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

