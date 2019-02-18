package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.FileInputStream;

@Autonomous(name="TestAutonomousPlayback", group="Z_PlaybackAuto")
public class AutonomousPlayback extends OpMode {
    // The input file stream.
    private FileInputStream inputStream;
    // The hardware player.
    private BlackBox.Player player;
    
    private String replayFile;
    private static final String DEFAULT_REPLAY_FILE = "recordedTeleop";

    private ElapsedTime elapsedTime = new ElapsedTime();
    private StepCounter stepCounter = new StepCounter(0, elapsedTime);
    private Vuforia vuforia;
    private Motor climber;
    private MotorUtil motorUtil;
    private Motor frontLeft;
    private Motor frontRight;
    private Motor backLeft;
    private Motor backRight;
    private LimitSwitch limitSwitch;
    private Servo marker;
    private Servo parker;
    private Servo righty;
    private Servo lefty;

    private boolean descend;
    public AutonomousPlayback() {
        this(DEFAULT_REPLAY_FILE, false);
    }
    
    public AutonomousPlayback(String replayFile, boolean doDescend) {
        this.replayFile = replayFile;
        this.descend = doDescend;
    }

    @Override
    public void init() {
        /*try {
            // Open previously written file full of hardware data.
            inputStream = hardwareMap.appContext.openFileInput(replayFile);
            // Create a player to playback the hardware log.
            player = new BlackBox.Player(inputStream, hardwareMap);
        } catch (Exception e) {
            e.printStackTrace();
            requestOpModeStop();
        }*/
        vuforia = new Vuforia(hardwareMap);
        climber = new Motor(hardwareMap, "climber");
        frontLeft = new Motor(hardwareMap, "motor1");
        frontRight = new Motor(hardwareMap, "motor2");
        backLeft = new Motor(hardwareMap, "motor3");
        backRight = new Motor(hardwareMap, "motor4");
        frontRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
        limitSwitch = new LimitSwitch(hardwareMap);

        marker = new Servo(hardwareMap, "marker");
        parker = new Servo(hardwareMap, "parker");
        righty = new Servo(hardwareMap, "righty");
        lefty = new Servo(hardwareMap, "lefty");

        motorUtil = new MotorUtil(frontRight, frontLeft, backRight, backLeft);
    }

    @Override
    public void start() {
        marker.setPosition(1);
        parker.setPosition(0.7);
        righty.setPosition(.5); // 0
        lefty.setPosition(.5); // 1
    }

    @Override
    public void loop() {

        switch (this.stepCounter.getStep()) {
            case 0:
                vuforia.sampleGoldBlockPosition(telemetry);
                if (!this.descend) {
                    stepCounter.set(6);
                } else {
                    stepCounter.increment();
                }
                break;
            case 1:
                climber.getMotor().setPower(1);
                stepCounter.increment();
                break;
            case 2:
                if (limitSwitch.hasHitLimit() || elapsedTime.time() > 12) {
                    climber.getMotor().setPower(0);
                    stepCounter.increment();
                }
                break;
            case 3:
                telemetry.addData("Stopped Climber", "Due to " + (limitSwitch.hasHitLimit() ? "limit switch" : "timeout"));
                stepCounter.increment();
                break;
            case 4:
                if (vuforia.getGoldBlockPosition() != null) {
                    stepCounter.increment();
                }
                break;
            case 5:

                if (elapsedTime.time() < 0.5) { //.75 usually  // was 0.7
                    motorUtil.strafeLeft(.6); //.6
                } else {
                    stepCounter.increment();
                    motorUtil.stopMoving();
                }
                break;
            case 6:
                try {
                    elapsedTime.reset();
                    if (this.descend) {
                        telemetry.addData("Wants to descend", replayFile.replace("%s", vuforia.getGoldBlockPosition().toString()));
                        inputStream = hardwareMap.appContext.openFileInput(replayFile.replace("%s", vuforia.getGoldBlockPosition().toString()));
                    } else {
                        telemetry.addData("DOES NOT WANT TO DESCEND", replayFile);
                        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                        //inputStream = new FileInputStream(new File(path, recordingName));
                        inputStream = hardwareMap.appContext.openFileInput(replayFile);
                    }
                    // Create a player to playback the hardware log.
                    player = new BlackBox.Player(inputStream, hardwareMap);
                    stepCounter.increment();
                } catch (Exception e) {
                    e.printStackTrace();
                    //requestOpModeStop();
                    telemetry.addData("Failed to run from input stream", ".");
                }
                break;
            case 7:
                try {
                    player.playback(elapsedTime.time());
                } catch (Exception e) {
                  //  requestOpModeStop();
                    telemetry.addData("Failed to run from playback", ".");
                }
                break;


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

    /* Actual Autonomous OpModes */

    @Autonomous(name = "PlaybackCrater", group = "Z_Recordings")
    public static class PlaybackCrater extends AutonomousPlayback {
        public PlaybackCrater() {
            super("craterBlock%s", true);
        }
    }

    @Autonomous(name = "PlaybackDepot", group = "Z_Recordings")
    public static class PlaybackDepot extends AutonomousPlayback {
        public PlaybackDepot() {
            super("depotBlock%s", true);
        }
    }

    @Autonomous(name = "PlaybackCraterThenDepot", group = "Z_Recordings")
    public static class PlaybackCraterThenDepot extends AutonomousPlayback {
        public PlaybackCraterThenDepot() {
            super("craterBlock%sThenDepot", true);
        }
    }

    ///////////////////////////////
    @Autonomous(name = "Test_PlaybackCraterBlockLeft", group = "Z_Recordings")
    public static class ReplayCraterBlockLeft extends AutonomousPlayback {
        public ReplayCraterBlockLeft() {
            super("craterBlockLeft", false);
        }
    }

    @Autonomous(name = "Test_PlaybackCraterBlockCenter", group = "Z_Recordings")
    public static class ReplayCraterBlockCenter extends AutonomousPlayback {
        public ReplayCraterBlockCenter() {
            super("craterBlockCenter", false);
        }
    }

    @Autonomous(name = "Test_PlaybackCraterBlockRight", group = "Z_Recordings")
    public static class ReplayCraterBlockRight extends AutonomousPlayback {
        public ReplayCraterBlockRight() {
            super("craterBlockRight", false);
        }
    }

    @Autonomous(name = "Test_PlaybackDepotBlockLeft", group = "Z_Recordings")
    public static class ReplayDepotBlockLeft extends AutonomousPlayback {
        public ReplayDepotBlockLeft() {
            super("depotBlockLeft", false);
        }
    }

    @Autonomous(name = "Test_PlaybackDepotBlockCenter", group = "Z_Recordings")
    public static class ReplayDepotBlockCenter extends AutonomousPlayback {
        public ReplayDepotBlockCenter() {
            super("depotBlockCenter", false);
        }
    }

    @Autonomous(name = "Test_PlaybackDepotBlockRight", group = "Z_Recordings")
    public static class ReplayDepotBlockRight extends AutonomousPlayback {
        public ReplayDepotBlockRight() {
            super("depotBlockRight", false);
        }
    }

    @Autonomous(name = "Test_PlaybackCraterBlockLeftThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockLeftThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockLeftThenDepot() {
            super("craterBlockLeftThenDepot", false);
        }
    }

    @Autonomous(name = "Test_PlaybackCraterBlockCenterThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockCenterThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockCenterThenDepot() {
            super("craterBlockCenterThenDepot", false);
        }
    }

    @Autonomous(name = "Test_PlaybackCraterBlockRightThenDepot", group = "Z_Recordings")
    public static class ReplayCraterBlockRightThenDepot extends AutonomousPlayback {
        public ReplayCraterBlockRightThenDepot() {
            super("craterBlockRightThenDepot", false);
        }
    }
}