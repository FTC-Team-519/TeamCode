package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous", group = "Iterative Opmode")

public class Autonomous extends OpMode {

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    private Servo marker;
    private Servo parker;

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
               // stepCounter.increment();
                // FIXME: Take this out
                stepCounter.set(4);
                break;
            case 1:
                climber.getMotor().setPower(1);
                stepCounter.increment();
                break;
            case 2:
                if (limitSwitch.hasHitLimit() || elapsedTime.time() > CLIMBER_TIMEOUT) {
                    climber.getMotor().setPower(0);
                }

                stepCounter.increment();

                break;
            case 3:
                telemetry.addData("Stopped Climber", "Due to " + (limitSwitch.hasHitLimit() ? "limit switch" : "timeout"));
                stepCounter.increment();
                break;
            case 4:
                if (vuforia.getGoldBlockPosition() != null) {
                   // stepCounter.increment();
                    //FIXME NEED TO TAKE THIS OUT
                    stepCounter.set(6);
                }
                break;
            case 5:

                if (elapsedTime.time() < 2) {
                    motorUtil.strafeLeft(.6);
                } else {
                    stepCounter.increment();
                }
                break;
            case 6:

                if (elapsedTime.time() < .6) {
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
                        if (elapsedTime.time() > 3) {
                            stepCounter.increment();
                        } else {
                            motorUtil.forward(.4);
                        }
                    } else {
                        motorUtil.turnLeft(.6, true);
                    }
                } else if (position == GoldBlockPosition.RIGHT) {
                    //start strafing
                    if (elapsedTime.time() > .5) {
                        if (elapsedTime.time() > 3) {
                            stepCounter.increment();
                        } else {
                            motorUtil.forward(.4);
                        }
                    } else {
                        motorUtil.turnRight(.6, true);
                    }
                } else {
                    //motorUtil.turnRight(.4, true);
                    motorUtil.forward(.4);
                    if (elapsedTime.time() > 1.5) {
                        stepCounter.increment();
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

        }
    }
}
