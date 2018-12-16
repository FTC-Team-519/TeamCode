package org.firstinspires.ftc.teamcode;

public class MotorUtil {

    public static final int FRONT_LEFT  = 0;
    public static final int FRONT_RIGHT = 1;
    public static final int BACK_LEFT   = 2;
    public static final int BACK_RIGHT  = 3;

    private static double[] motorPowers = new double[4];

    private static void normalizeCombinedPowers() {
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

    public static double[] UpdateMotorPowers(double pwr, double x, double z) {
        motorPowers[FRONT_RIGHT] = pwr - x - z;
        motorPowers[FRONT_LEFT] = 1.25*(pwr + x + z);
        motorPowers[BACK_RIGHT] = 1.25*(pwr + x - z);
        motorPowers[BACK_LEFT] = pwr - x + z;
        normalizeCombinedPowers();

        return motorPowers;
    }

    private Motor frontRight;
    private Motor frontLeft;
    private Motor backRight;
    private Motor backLeft;

    public MotorUtil(Motor frontRight, Motor frontLeft, Motor backRight, Motor backLeft) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backRight = backRight;
        this.backLeft = backLeft;
    }

    public void forward(double amount) {
        frontLeft.getMotor().setPower(amount);
        frontRight.getMotor().setPower(amount);
        backRight.getMotor().setPower(amount);
        backLeft.getMotor().setPower(amount);
    }

    public void reverse(double amount) {
        forward(-amount);
    }

    public void turnLeft(double power, boolean turnOnSpot) {
        frontRight.getMotor().setPower(power);
        backRight.getMotor().setPower(power);
        if (turnOnSpot) {
            backLeft.getMotor().setPower(-power);
            frontLeft.getMotor().setPower(-power);
        }
    }

    public void turnRight(double power, boolean turnOnSpot) {
        frontLeft.getMotor().setPower(power);
        backLeft.getMotor().setPower(power);
        if (turnOnSpot) {
            backRight.getMotor().setPower(-power);
            frontRight.getMotor().setPower(-power);
        }
    }

    public void strafeLeft(double power) {
        frontLeft.getMotor().setPower(-1*power); //-1
        frontRight.getMotor().setPower(.6*power); // .85
        backRight.getMotor().setPower(-.71*power); //-.975
        backLeft.getMotor().setPower(0.75*power); // .725
    }

    public void strafeRight(double power) {
        frontLeft.getMotor().setPower(.9*power);//.9
        frontRight.getMotor().setPower(-.7*power);
        backRight.getMotor().setPower(.6*power);
        backLeft.getMotor().setPower(-1*power);
    }

    public void stopMoving() {
        frontRight.getMotor().setPower(0);
        frontLeft.getMotor().setPower(0);
        backLeft.getMotor().setPower(0);
        backRight.getMotor().setPower(0);
    }
}
