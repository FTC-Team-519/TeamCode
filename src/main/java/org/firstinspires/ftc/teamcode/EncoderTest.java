package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Encoder Test", group = "Test OpModes" )

public class EncoderTest extends OpMode{

    private Motor slider;
    // -1325 max (actual value -1329)
    @Override
    public void init() {
        slider = new Motor(hardwareMap, "slider");

        slider.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void start() {
        slider.getMotor().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        telemetry.addData("Encoder Value", slider.getMotor().getCurrentPosition());
    }
}
