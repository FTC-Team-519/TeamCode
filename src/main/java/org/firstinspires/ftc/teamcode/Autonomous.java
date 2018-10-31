package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous (name="Autonomous", group="Iterative Opmode")

public class Autonomous extends OpMode {

    private LimitSwitch limitSwitch;
    private Accelerometer accelerometer;
    private StepCounter stepCounter;
    private ElapsedTime elapsedTime;
    private Motor climber;

    private double CLIMBER_TIMEOUT = 5;
    @Override
    public void init() {
        limitSwitch = new LimitSwitch(hardwareMap);
        climber = new Motor(hardwareMap, "climber");
        climber.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        climber.getMotor().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        accelerometer = new Accelerometer(hardwareMap);
        stepCounter = new StepCounter(0);
        elapsedTime = new ElapsedTime();

    }

    @Override
    public void loop() {
        switch(stepCounter.getStep()) {
            case 0:
                elapsedTime.reset();
                /*telemetry.addData("Has Hit Limit Switch", limitSwitch.hasHitLimit() + " | " + elapsedTime.time());
                telemetry.addData("Position Offset(?)", accelerometer.getAccelerometer().getPosition().toString());
                telemetry.addData("Accelerometer Reading", accelerometer.getAccelerometer().getAcceleration().toString());
                */

                stepCounter.set(5);

                break;
            case 1:
                climber.getMotor().setPower(.75);

                elapsedTime.reset();
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

                break;
            case 5:

                accelerometer.startRecordingDistance();
                stepCounter.increment();
                elapsedTime.reset();
                break;
            case 6:
                accelerometer.sampleDistance(elapsedTime.time());
                telemetry.addData("Distance", accelerometer.getDistance() + " m");
                break;

        }
    }
}
