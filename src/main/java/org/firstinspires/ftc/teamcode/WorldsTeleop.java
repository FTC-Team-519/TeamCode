package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Worlds", group="Iterative Opmode")
//@Disabled
public class WorldsTeleop extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor shooter;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor midCollector;
    private DcMotor frontCollector;
    private DcMotor topCollector;
    private ColorSensor color;
    private OpticalDistanceSensor ods;
    private float x;
    private float y;
    private float z;

    private int sillyCounter = 0;

    private double[] motorPowers = new double[4];
    private static final int FRONT_LEFT  = 0;
    private static final int FRONT_RIGHT = 1;
    private static final int BACK_LEFT   = 2;
    private static final int BACK_RIGHT  = 3;

    private static final float DEAD_ZONE = 0.2f;
    private static final double MAX_SPEED = 1.0d;

    int previousTickCount = 0;


    boolean keepShooterSpinning = false;
    double SHOOTER_REVERSE_SPEED = 0.6d;
    double SHOOTER_FORWARD_SPEED = -1.0d;
    double currentShooterSpeed = 0.0d;

    boolean flipped = false;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Add telemetry data





        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).


        frontLeft  = hardwareMap.get(DcMotor.class, "motor4");
        frontRight = hardwareMap.get(DcMotor.class, "motor2");
        backLeft = hardwareMap.get(DcMotor.class, "motor3");
        backRight = hardwareMap.get(DcMotor.class, "motor1");
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        topCollector = hardwareMap.dcMotor.get("topCollector");
        midCollector = hardwareMap.dcMotor.get("feeder");
        frontCollector = hardwareMap.dcMotor.get("collector");
        shooter = hardwareMap.dcMotor.get("shooter");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery

        // Tell the driver that initialization is complete.
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {


        //   getTelemetryUtil().addData("Start", getClass().getSimpleName() + " Start.");
        //  getTelemetryUtil().sendTelemetry();
        runtime.reset();



    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        Gamepad driver = gamepad1;
        Gamepad gunner = gamepad2;

        updateJoyStickValues();

        if (driver.y) {
            flipped = false;
        }
        else if (driver.a) {
            flipped = true;
        }

        if (flipped) {
            x = -x;
            y = -y;
        }

        if (Math.abs(driver.left_stick_x)>Math.abs(driver.left_stick_y)){
            y = 0;
        }
        else if (Math.abs(driver.left_stick_y) > Math.abs(driver.left_stick_x)){
            x = 0;
        }
        else if (driver.x) {
            x = -100;
            y = 0;
        }
        else if (driver.b) {
            x = 100;
            y = 0;
        }

        // Forward/backward power is left_stick_y, but forward is -1.0 reading, so invert
        double pwr = -y;

        motorPowers[FRONT_RIGHT] = pwr - x - z;
        motorPowers[FRONT_LEFT] = 1.25*(pwr + x + z);
        motorPowers[BACK_RIGHT] = 1.25*(pwr + x - z);
        motorPowers[BACK_LEFT] = pwr - x + z;
        normalizeCombinedPowers(motorPowers);

        frontRight.setPower(reducePower(motorPowers[FRONT_RIGHT]));
        frontLeft.setPower(reducePower(motorPowers[FRONT_LEFT]));
        backRight.setPower(reducePower(motorPowers[BACK_RIGHT]));
        backLeft.setPower(reducePower(motorPowers[BACK_LEFT]));

       /* if (sillyCounter > 25) {
            sillyCounter = 0;
            getTelemetryUtil().addData("Start", "fR: " + frontRight.getPower() +
                    ", fL: " + frontLeft.getPower() +
                    ", bR: " + backRight.getPower() +
                    ", bL: " + backLeft.getPower());
            getTelemetryUtil().sendTelemetry();
        }
        else {
            ++sillyCounter;
        }
        */

        // Forward/backward power is left_stick_y, but forward is -1.0 reading, so invert

//        if (gamepad1.x) {
//            currPower = 0.1d;
//        }
//        else if (gamepad1.y) {
//            currPower = 0.2d;
//        }
//        else if (gamepad1.b) {
//            currPower = 0.4d;
//        }
//        else if (gamepad1.a) {
//            currPower = 0.6d;
//        }
//        else if (gamepad1.left_bumper) {
//            currPower = 0.8d;
//        }
//        else if (gamepad1.right_bumper) {
//            currPower = 1.0d;
//        }

        if (gunner.x || gunner.a || gunner.b) {
            if (gunner.x) {
                currentShooterSpeed = SHOOTER_REVERSE_SPEED;
                keepShooterSpinning = false;
            }
            else if (gunner.b) {
                currentShooterSpeed = SHOOTER_FORWARD_SPEED;
                keepShooterSpinning = false;
            }
            else if (gunner.a) {
                currentShooterSpeed = 0.0d;
                keepShooterSpinning = false;
            }
        }
        else {
            if (! keepShooterSpinning) {
                currentShooterSpeed = 0.0d;
            }
            /** Driver potentially saved controls
             if (driver.x) {
             currPower = -1.0d;
             } else if (driver.b) {
             currPower = 1.0d;
             }
             **/
        }

        // Negative, as the wheel needs to go in reverse direction (could reverse motor actually)
        shooter.setPower(currentShooterSpeed);

        // Gunner

        if (gunner.right_bumper) {
            topCollector.setPower(0.6);
            midCollector.setPower(0.6);
        }

        /*if (gunner.right_trigger) {
            midCollector.setPower(0.5);
        } */
        else if (gunner.left_bumper) {
            topCollector.setPower(-0.6);
            midCollector.setPower(-0.6);

        }
        else {
            topCollector.setPower(0.0);
            midCollector.setPower(0.0);
        }

        // Driver
        if (driver.right_bumper) {
            frontCollector.setPower(-1.0);
            //midCollector.setPower(0.25);
        }
        else if (driver.left_bumper) {
            frontCollector.setPower(1.0);
            //midCollector.setPower(-0.25);
        }
        else if ((driver.left_trigger > 0) || (driver.right_trigger > 0)){
            frontCollector.setPower(0.0);
            //midCollector.setPower(0.0);
        }
/*
        getTelemetryUtil().addData("RGB: ", "" + color.red() + "," + color.green() + "," + color.blue());
        getTelemetryUtil().addData("ARGB", " " + color.argb());

        if (timerComponent.targetReached(1.0f)) {
            int currentTicks = shooter.getController().getMotorCurrentPosition(1);
            float ticksPerSecond = currentTicks - previousTickCount;
            previousTickCount = currentTicks;
            getTelemetryUtil().addData("RPM", "ticksPerMinute: " + ticksPerSecond);
        }

        getTelemetryUtil().sendTelemetry();

        // Might want to have a more effective combination
        //frontRight.setPower(Range.clip(pwr - x - z, -MAX_SPEED, MAX_SPEED));
        //frontLeft.setPower(Range.clip(pwr + x + z, -MAX_SPEED, MAX_SPEED));
        //backRight.setPower(Range.clip(pwr + x - z, -MAX_SPEED, MAX_SPEED));
        //backLeft.setPower(Range.clip(pwr - x + z, -MAX_SPEED, MAX_SPEED));
        */
    }

    private static void normalizeCombinedPowers(double[] motorPowers) {
        double maxAbsPower = 0.0d;

        for (double motorPower : motorPowers) {
            double tmpAbsPower = Math.abs(motorPower);
            if (tmpAbsPower > maxAbsPower) {
                maxAbsPower = tmpAbsPower;
            }
        }

        if (maxAbsPower > 1.0d) {
            for (int i = 0; i < motorPowers.length; ++i) {
                motorPowers[i] = motorPowers[i] / maxAbsPower;
            }
        }
    }

    /**
     * Returns a power that is reduced proportionally to the maximum speed, which would be
     * no reduction if max speed is 1.0 (or basically 100%).
     *
     * @param input power to potentially be reduced.
     * @return a power that is reduced proportionally to the maximum speed, which would be
     * no reduction if max speed is 1.0 (or basically 100%).
     */
    private static double reducePower(double input) {
        // return input;   // If no reduction desired
        return input * MAX_SPEED;
    }

    /**
     * Re-reads joystick values and assigns the state variables used for other computations.
     */
    private void updateJoyStickValues() {
        y = gamepad1.left_stick_y;
        x = gamepad1.left_stick_x;
        z = gamepad1.right_stick_x;

        // NOTE: Check for deadzone before any shaping
        y = adjustForDeadZone(y);
        x = adjustForDeadZone(x);
        z = adjustForDeadZone(z);

        y = shapeInput(y);
        x = shapeInput(x);
        z = shapeInput(z);
    }

    /**
     * Returns 0.0 if within the deadzone range, otherwise the original value.
     *
     * @param input value to check.
     * @return 0.0 if within the deadzone range, otherwise the original value.
     */
    private static float adjustForDeadZone(float input) {
        float adjustedValue = input;

        if (Math.abs(input) < DEAD_ZONE) {
            adjustedValue = 0.0f;
        }

        return adjustedValue;
    }

    /**
     * Returns the input value shaped by squaring and preserving sign.
     *
     * REVISIT: This is where using a more custom curve could be done instead.
     *
     * @param input value to shape.
     * @return the input value shaped by squaring and preserving sign.
     */
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

        // return input * (input < 0.0f ? -input : input);
    }

    @Override
    public void stop() {
    }

}

