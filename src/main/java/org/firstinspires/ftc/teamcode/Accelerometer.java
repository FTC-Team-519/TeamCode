package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class Accelerometer extends IMU {
    // v = dist / time
    // d = vi*t + 1/2*a*t^2

    private double previousTime = 0;
    private double previousVelocity = 0;

    private double totalDistance = 0;
    public Accelerometer(HardwareMap map) {
        super(map);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 100);

        // Y axis is for strafing
    }

    public void startRecordingDistance() {
        previousTime = 0;
        previousVelocity = 0;
        totalDistance = 0;
    }

    public void sampleDistance(double time) {
        double yAccel = getAccelerometer().getAcceleration().yAccel;
        double currVelo =  yAccel * (time - previousTime) + previousVelocity;

        double dist = (previousVelocity * (time - previousTime)) + .5 * (yAccel) * Math.pow((time-previousTime), 2);
        totalDistance += dist;

        previousVelocity = currVelo;
        previousTime = time;
    }

    public double getDistance() {
        return totalDistance;
    }

    public BNO055IMU getAccelerometer() {
        return imu;
    }
}
