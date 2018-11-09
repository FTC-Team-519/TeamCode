package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous (name="Autonomous", group="Iterative Opmode")

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
    private double CLIMBER_TIMEOUT = 5;

    @Override
    public void init() {
        limitSwitch = new LimitSwitch(hardwareMap);
        climber = new Motor(hardwareMap, "climber");
        climber.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        climber.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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
        switch(stepCounter.getStep()) {
            /* Case Layout
                Steps 0-3: move down to floor until hit limit switch
                Steps 4-?: Strafe off
             */
            case 0:
                vuforia.sampleGoldBlockPosition(telemetry);
                stepCounter.increment();
                break;
            case 1:
                climber.getMotor().setPower(.75);
                stepCounter.increment();
                break;
            case 2:
               if (limitSwitch.hasHitLimit() || elapsedTime.time() > CLIMBER_TIMEOUT) {
                   climber.getMotor().setPower(0);

                   stepCounter.increment();
               }
                break;
            case 3:
                telemetry.addData("Stopped Climber", "Due to " + (limitSwitch.hasHitLimit() ?  "limit switch" : "timeout"));
                stepCounter.increment();
                break;
            case 4:
                if (vuforia.getGoldBlockPosition() != null) {
                    stepCounter.increment();
                }
                break;
            case 5:
                GoldBlockPosition position = vuforia.getGoldBlockPosition();
                if (position == GoldBlockPosition.LEFT) {
                    // start strafing
                } else if (position == GoldBlockPosition.RIGHT) {
                    // start strafing
                } else {
                    // start strafing
                }
                stepCounter.increment();
                break;
            case 6:

                break;

        }
    }
}