package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.FileInputStream;

@Autonomous(name="TestAutonomousPlayback", group="Z_PlaybackAuto")
public class AutonomousPlayback extends OpMode {
    // The input file stream.
    private FileInputStream inputStream;
    // The hardware player.
    private BlackBox.Player player;
    
    private String replayFile;
    private static final String DEFAULT_REPLAY_FILE = "recordedTeleop";
    
    public AutonomousPlayback() {
        this(DEFAULT_REPLAY_FILE);
    }
    
    public AutonomousPlayback(String replayFile) {
        this.replayFile = replayFile;
    }

    @Override
    public void init() {
        try {
            // Open previously written file full of hardware data.
            inputStream = hardwareMap.appContext.openFileInput(replayFile);
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
            // Update the hardware to mimic human during recorded Autonomous.
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

    @Autonomous(name = "PlaybackCraterBlockLeft", group = "Z_Recordings")
    public static class ReplayCraterBlockLeft extends AutonomousPlayback {
        public ReplayCraterBlockLeft() {
            super("craterBlockLeft");
        }
    }

    @Autonomous(name = "PlaybackCraterBlockCenter", group = "Z_Recordings")
    public static class ReplayCraterBlockCenter extends AutonomousPlayback {
        public ReplayCraterBlockCenter() {
            super("craterBlockCenter");
        }
    }

    @Autonomous(name = "PlaybackCraterBlockRight", group = "Z_Recordings")
    public static class ReplayCraterBlockRight extends AutonomousPlayback {
        public ReplayCraterBlockRight() {
            super("craterBlockRight");
        }
    }

    @Autonomous(name = "PlaybackDepotBlockLeft", group = "Z_Recordings")
    public static class ReplayDepotBlockLeft extends AutonomousPlayback {
        public ReplayDepotBlockLeft() {
            super("depotBlockLeft");
        }
    }

    @Autonomous(name = "PlaybackDepotBlockCenter", group = "Z_Recordings")
    public static class ReplayDepotBlockCenter extends AutonomousPlayback {
        public ReplayDepotBlockCenter() {
            super("depotBlockCenter");
        }
    }

    @Autonomous(name = "PlaybackDepotBlockRight", group = "Z_Recordings")
    public static class ReplayDepotBlockRight extends AutonomousPlayback {
        public ReplayDepotBlockRight() {
            super("depotBlockRight");
        }
    }

    @Autonomous(name = "PlaybackCraterBlockLeftThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockLeftThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockLeftThenDepot() {
            super("craterBlockLeftThenDepot");
        }
    }

    @Autonomous(name = "PlaybackCraterBlockCenterThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockCenterThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockCenterThenDepot() {
            super("craterBlockCenterThenDepot");
        }
    }

    @Autonomous(name = "PlaybackCraterBlockRightThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockRightThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockRightThenDepot() {
            super("craterBlockRightThenDepot");
        }
    }
}