package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "GOATAutonomous", group = "Iterative Opmode")

public class GOATAutonomous extends OpMode {

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;


    private ElapsedTime parkerElapsedTime;
    private ElapsedTime parkerJuniorElapsedTime;

    private Servo marker;
    private Servo parker;
    private Servo righty;
    private Servo lefty;
    private Servo parkerjr;


    private boolean parkerMovingOut = false;
    private boolean parkerMovingIn = false;


    private MotorUtil motorUtil;
    private LimitSwitch limitSwitch;
    private Accelerometer accelerometer;
    private StepCounter stepCounter;
    private ElapsedTime elapsedTime;
    private Motor climber;

    private Vuforia vuforia;
    private double CLIMBER_TIMEOUT = 12;

    @Override
    public void init() {
        limitSwitch = new LimitSwitch(hardwareMap);
        climber = new Motor(hardwareMap, "climber");
        // climber.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        //climber.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        accelerometer = new Accelerometer(hardwareMap);
        elapsedTime = new ElapsedTime();
        stepCounter = new StepCounter(0, elapsedTime);
        vuforia = new Vuforia(hardwareMap);
        frontLeft = new Motor(hardwareMap, "motor1");
        frontRight = new Motor(hardwareMap, "motor2");
        backLeft = new Motor(hardwareMap, "motor3");
        backRight = new Motor(hardwareMap, "motor4");

        frontRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);

        motorUtil = new MotorUtil(frontRight, frontLeft, backRight, backLeft);

        marker = new Servo(hardwareMap, "marker");
        parker = new Servo(hardwareMap, "parker");

        marker.setPosition(1);
        righty = new Servo(hardwareMap, "righty");
        lefty = new Servo(hardwareMap, "lefty");

    }

    @Override
    public void start() {
        parker.setPosition(0.7);
        righty.setPosition(.5); // 0
        lefty.setPosition(.5); // 1
    }

    @Override
    public void loop() {
        switch (stepCounter.getStep()) {
            /* Case Layout
                Steps 0-3: move down to floor until hit limit switch
                Steps 4-?: Strafe off
             */
            case 0:
                vuforia.sampleGoldBlockPosition(telemetry);
                stepCounter.increment();
                // FIXME: Take this out
                //  stepCounter.set(4);
                break;
            case 1:
                climber.getMotor().setPower(1);
                stepCounter.increment();
                break;
            case 2:
                if (limitSwitch.hasHitLimit() || elapsedTime.time() > CLIMBER_TIMEOUT) {
                    climber.getMotor().setPower(0);
                    stepCounter.increment();

                }


                break;
            case 3:
                telemetry.addData("Stopped Climber", "Due to " + (limitSwitch.hasHitLimit() ? "limit switch" : "timeout"));
                stepCounter.increment();
                break;
            case 4:
                if (vuforia.getGoldBlockPosition() != null) {
                    stepCounter.increment();
                    //FIXME NEED TO TAKE THIS OUT
                    //  stepCounter.set(6);
                }
                break;
            case 5:

                if (elapsedTime.time() < 0.5) { //.75 usually
                    motorUtil.strafeLeft(.7); //.6
                } else {
                    stepCounter.increment();
                }
                break;
            case 6:
                lefty.setPosition(.3);
                righty.setPosition(.7);
                if (elapsedTime.time() < .4) {
                    motorUtil.forward(.5);
                } else {

                    stepCounter.increment();
                }
                break;
            case 7:
                GoldBlockPosition position = vuforia.getGoldBlockPosition();
                if (position == GoldBlockPosition.LEFT) {
                    //start strafing
                    if (elapsedTime.time() > .5) {
                        if (elapsedTime.time() > 3.1) {
                            // lefty.setPosition(.7);
                            //righty.setPosition(.3);
                            lefty.setPosition(0);
                            righty.setPosition(1);
                            if (elapsedTime.time() > 4.6) {
                                if (elapsedTime.time() > 6.4) {
                                    stepCounter.increment();
                                } else {
                                    motorUtil.forward(.5);
                                }
                            } else {
                                motorUtil.turnRight(.5, true);
                            }
                        } else {
                            motorUtil.forward(.4);
                        }
                    } else {
                        motorUtil.turnLeft(.6, true);
                    }
                } else if (position == GoldBlockPosition.RIGHT) {
                    //start strafing
                    if (elapsedTime.time() > .68) { //.625+.15
                        if (elapsedTime.time() > 3) { // was 3.5
                            lefty.setPosition(0);
                            righty.setPosition(1);
                            if (elapsedTime.time() > 4.1) { // was 4.5
                                if (elapsedTime.time() > 4.7) {
                                    if (elapsedTime.time() > 5) { // was 5.1
                                        if (elapsedTime.time() > 5.2) {
                                            if (elapsedTime.time() > 5.5) { // was 5.4
                                                if (elapsedTime.time() > 7) {
                                                    if (elapsedTime.time() > 8.5) {
                                                        if (elapsedTime.time() > 8.55) {
                                                            stepCounter.increment();
                                                        } else {
                                                            motorUtil.forward(.5);
                                                        }
                                                    } else {
                                                        motorUtil.turnRight(.5, true);
                                                    }
                                                } else {
                                                    motorUtil.forward(.4);
                                                }
                                            } else {
                                                motorUtil.turnLeft(.5, true);
                                            }
                                        } else {
                                            motorUtil.forward(.4);
                                        }
                                    } else {
                                        motorUtil.turnLeft(.5, true);
                                    }
                                } else {
                                    motorUtil.forward(.4);
                                }
                            } else {
                                motorUtil.turnLeft(.5, true);
                            }
                        } else {
                            motorUtil.forward(.4);
                        }
                    } else {
                        motorUtil.turnRight(.6, true);
                    }
                } else {
                    //motorUtil.turnRight(.4, true);
                    //motorUtil.forward(.5);
                    if (elapsedTime.time() > 3.2) {
                        //lefty.setPosition(0);
                        //righty.setPosition(1);
                        if (elapsedTime.time() > 4.1) {
                            if (elapsedTime.time() > 4.25) {
                                stepCounter.increment();
                            } else {
                                motorUtil.forward(.5);
                            }
                        } else {
                            motorUtil.turnRight(.5, true);
                        }
                    } else {
                        motorUtil.forward(.5);
                    }
                }
                break;

            case 8:
                motorUtil.stopMoving();

                /*
                    motorUtil.forward(double power);
                    elapsedTime.time() > timeYouWantItToWaitUntil (in seconds)
                    motorUtil.turnLeft(double power, boolean turnInPlace)

                    stepCounter.increment();

                    marker.getServo().setPosition();
                    parker.getServo().setPosition();
                 */
                stepCounter.increment();
                break;
            case 9:
                if (marker.getPosition() < .3) {
                    stepCounter.increment();
                }
                marker.setPosition(.6);
                stepCounter.increment();
                break;
            case 10:
                lefty.setPosition(0.5);
                righty.setPosition(0.5);
                if (elapsedTime.time() > 1.5) {
                  //  lefty.setPosition(0.3);
                    //righty.setPosition(0.7);
                    if (elapsedTime.time() > 2.0) {

                            stepCounter.increment();
                    } else {
                        motorUtil.forward(.5);
                    }
                } else {
                    motorUtil.turnRight(.5, true);
                }

        break;

            case 11:
                    // Ensure that it's been held down for a second
                    if (parkerElapsedTime.time() >= 1) {
                        parkerElapsedTime.reset();
                        if (parkerjr == null) {
                            parkerjr = new Servo(hardwareMap, "parkerjr");
                        }

                        parkerMovingOut = true;
                        //parker.setPosition(0);
                        //parkerjr.setPosition(1);
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
                    stepCounter.increment();
                    break;
        }

    }
}
