package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "StrafeTest", group = "Test OpModes")
public class StrafeTest extends OpMode {

    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;

    boolean UpBeingPressed;
    boolean DownBeingPressed;


    double[] corrections = new double[4];
    double speedRatio = 1.0d;

    int currMotorAdjust;
    
    @Override
    public void init() {
        frontLeft = new Motor(hardwareMap, "motor1");
        frontRight = new Motor(hardwareMap, "motor2");
        backLeft = new Motor(hardwareMap, "motor3");
        backRight = new Motor(hardwareMap, "motor4");

        frontRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);


        speedRatio = 1.0d;

        for (int i = 0; i < 4; i++)
            corrections[i] = 1.0;

        currMotorAdjust = 0;
        UpBeingPressed = false;
        DownBeingPressed = false;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {

        if (gamepad1.x) currMotorAdjust = 0;
        if (gamepad1.y) currMotorAdjust = 1;
        if (gamepad1.a) currMotorAdjust = 2;
        if (gamepad1.b) currMotorAdjust = 3;

        if (gamepad1.right_bumper) {
            speedRatio += 0.001d;
        }

        if (gamepad1.left_bumper) {
            speedRatio -= 0.001d;
        }

        if (gamepad1.dpad_up && !UpBeingPressed) {
            corrections[currMotorAdjust] += 0.025;
            UpBeingPressed = true;
        }
        else if (UpBeingPressed && !gamepad1.dpad_up) {
            UpBeingPressed = false;
        }

        if (gamepad1.dpad_down && !DownBeingPressed) {
            corrections[currMotorAdjust] -= 0.025;
            DownBeingPressed = true;
        }
        else if (DownBeingPressed && !gamepad1.dpad_down) {
            DownBeingPressed = false;
        }

        if(gamepad1.dpad_left) {
            strafeLeft(speedRatio, corrections[1], corrections[3], corrections[0], corrections[2]);
        }
        else if (gamepad1.dpad_right) {
            strafeRight(speedRatio, corrections[1], corrections[3], corrections[0], corrections[2]);
        }
        else {
            stopMoving();
        }

        telemetry.addData("frontleft: ", corrections[0]);
        telemetry.addData("frontright: ", corrections[1]);
        telemetry.addData("backleft: ", corrections[2]);
        telemetry.addData("backright: ", corrections[3]);
        telemetry.addData("speedRatio: ", speedRatio);
        
    }

    public void strafeLeft (double power, double fr, double br, double fl, double bl) {

        frontRight.getMotor().setPower(fr*power);
        backRight.getMotor().setPower(br*(-power));
        frontLeft.getMotor().setPower(fl*(-power));
        backLeft.getMotor().setPower(bl*power);
    }
    public void strafeRight (double power, double fr, double br, double fl, double bl) {
        frontRight.getMotor().setPower(fr*(-power));
        backRight.getMotor().setPower(br*power);
        frontLeft.getMotor().setPower(fl*power);
        backLeft.getMotor().setPower(bl*(-power));
    }

    public void stopMoving(){
        frontLeft.getMotor().setPower(0.0d);
        frontRight.getMotor().setPower(0.0d);
        backLeft.getMotor().setPower(0.0d);
        backRight.getMotor().setPower(0.0d);
    }
}
