package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class IMU {

    private int POLL_SPEED_MS = 1000;
    protected BNO055IMU imu;

    public IMU(HardwareMap map) { // accelerationToDistanceUnit

        imu = map.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        parameters.loggingEnabled = false;
        parameters.calibrationDataFile = "IMUCalibration.json";

        imu.initialize(parameters);

        //imu.getPosition().
    }

    public String getDump() {
        return this.imu.getAcceleration().toUnit(DistanceUnit.MM).toString() + " | Calibrated: " + this.imu.isAccelerometerCalibrated();
    }
}
