package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.FileInputStream;

@Autonomous(name="TestAutonomousPlayback", group="PlaybackAuto")
public class AutonomousPlayback extends OpMode {
    // The input file stream.
    private FileInputStream inputStream;
    // The hardware player.
    private BlackBox.Player player;

    @Override
    public void init() {
        try {
            // Open previously written file full of hardware data.
            inputStream = hardwareMap.appContext.openFileInput("recordedTeleop");
            // Create a player to playback the hardware log.
            player = new BlackBox.Player(inputStream, hardwareMap);
        } catch (Exception e) {
            e.printStackTrace();
            requestOpModeStop();
        }
    }

    @Override
    public void loop() {
        try {
            // Update the hardware to mimic human during recorded Teleop.
            player.playback(time);
            telemetry.addData("Playing", time + "");
        } catch (Exception e) {
            e.printStackTrace();
            requestOpModeStop();
        }
    }

    @Override
    public void stop() {
        try {
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}