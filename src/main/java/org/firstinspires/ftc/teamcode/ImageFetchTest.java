package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "ImageFetchTest", group = "Test")
public class ImageFetchTest extends OpMode {

    private Vuforia vuforia;
    @Override
    public void init() {
        vuforia = new Vuforia(hardwareMap);
    }

    @Override
    public void start() {
        vuforia.sampleGoldBlockPosition(telemetry);
    }

    @Override
    public void loop() {

    }
}
