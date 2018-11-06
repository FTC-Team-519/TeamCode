package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.io.UnsupportedEncodingException;

public class Vuforia {

    private VuforiaLocalizer vuforia;

    private VuforiaLocalizer.Parameters parameters;
    private static String convert(byte[] thing) {
        try {
            return new String(thing, "US-ASCII");
        } catch (UnsupportedEncodingException uee) {
            return "Could not convert!!!";
        }
    }

    private static final String A = "QWJaajY5di8vLy8vQUFBQUdTWHZyMEc2TDBrTXJ3TUQwT";
    private static final String B = "zdZRWdNc0lmOEI2WFZ1eEV1UTNCci8wV1d0Tk13dHBSMm";
    private static final String C = "5sZHA5cmsxTk1PbHhWc0VBcXIwbmFac1o0dmlvTlZ0R0Q";
    private static final String D = "2K2tMUXVZd0lrSWlKMXlsZGl5Wjd4U055Qkt0Yk0zeVoy";
    private static final String E = "dFBYMFRFZnYrY1o0L3d5TmhXRVoxMU0wdk9GenVsSUFlc";
    private static final String F = "mRwUTBhNzRzeTZHWWc5UzFtSzRGUDJnbFh0b2tuVy94Um";
    private static final String G = "FBaG1zWnkzaXRjZWhYZCtLYVNRS2JBNzZMOGdGcytFY2x";
    private static final String H = "RQW1XQlFwdVRIRHlLcmVMbzEvaHlxUjRzb1BaUXIxemgy";
    private static final String I = "RXg1RWZ0K01TdzBmaUh3NjhtWG1lWDh1L1ZrMk00QWVsR";
    private static final String J = "ERlSE5UdndoMHJ1dnVYczdGVmRaZDBMWm4yMnR4RStWbm";
    private static final String K = "h6cGt5OUFFbEI5eHZVYkFoZTIwN2lWSU5wSndpZDR3d0l";
    private static final String L = "LZ3VFTEdRMTQK";

    private static final String VALUE = A + B + C + D + E + F + G + H + I + J + K + L;

    private static final byte[] MY_VALUE = Base64.decode(VALUE, Base64.NO_WRAP);
    private static final String KEY = convert(MY_VALUE);


    public Vuforia(HardwareMap map) {
        int cameraMonitorViewId = map.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", map.appContext.getPackageName());
        parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = KEY;

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        vuforia.setFrameQueueCapacity(1);
        com.vuforia.Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

    }

    public void sampleGoldBlockPosition(Telemetry telemetry) {
        try {
            VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();
            Thread t = new GoldSample(frame, telemetry);
            t.start();
        } catch (Exception e) {
            // guess
        }
    }

}

class GoldSample extends Thread {

    private VuforiaLocalizer.CloseableFrame frame;

    private Image rgb;

    private Telemetry telemetry;
    public GoldSample(VuforiaLocalizer.CloseableFrame frame, Telemetry telemetry) {
        this.telemetry = telemetry;
        long numImages = frame.getNumImages();
        this.frame = frame;

        for (int i = 0; i < numImages; i++) {
            if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                rgb = frame.getImage(i);
                break;
            }
        }

        assert(rgb != null);
    }

    private double getAmountOfYellow(Bitmap bm) {
        double amt = 0;

        for(int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                int pixel = bm.getPixel(x,y);

                double red, green, blue;
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);


                double yellow = ((red + green)) / 500;
               // telemetry.addData("Yellow Value", yellow + "");
                if (yellow > .6 && blue < 150) {
                    amt += yellow;
                }

            }
        }

        return amt;
    }
    public void run() {

        Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);

        bm.copyPixelsFromBuffer(rgb.getPixels());


        int boxWidth = bm.getWidth()/2;                      //    ||                   ||
        int boxHeight = bm.getHeight()/2;

        Bitmap halfOneTop = Bitmap.createBitmap(bm, 0, 0, boxWidth, boxHeight);

        //top right part coordinate is (w/2, 0).
        Bitmap halfTwoTop = Bitmap.createBitmap(bm, boxWidth, 0, boxWidth, boxHeight);

        //bottom left part coordinate is (0, h/2).
        Bitmap halfOneBottom = Bitmap.createBitmap(bm, 0, boxHeight, boxWidth, boxHeight);

        //bottom right part coordinate is (w/2, h/2).
        Bitmap halfTwoBottom = Bitmap.createBitmap(bm, boxWidth, boxHeight, boxWidth, boxHeight);
        //Bitmap firstHalf = Bitmap.createBitmap(bm, 0, 0, bm.getWidth()/2, bm.getHeight());
        //Bitmap secondHalf = Bitmap.createBitmap(bm, bm.getWidth()/2, 0, bm.getWidth()/2, )
        //Bitmap topHalf2 = Bitmap.createBitmap(bm, bm.getWidth())


        double yellowHalfOne = 0;
        double yellowHalfTwo = 0;

        yellowHalfTwo = getAmountOfYellow(halfOneTop) + getAmountOfYellow(halfTwoTop);
        yellowHalfOne = getAmountOfYellow(halfTwoBottom) + getAmountOfYellow(halfOneBottom);

        if (Math.abs(yellowHalfOne - yellowHalfTwo) < 2000) {
            telemetry.addData("Block Found", "Off Screen - negligible difference of " + Math.abs(yellowHalfOne-yellowHalfTwo));
        } else if (yellowHalfOne > yellowHalfTwo) {
            telemetry.addData("Block Found", "Left");
        } else {
            telemetry.addData("Block Found", "Right");
        }
       // int[] pix = new int[rgb.getWidth() * rgb.getHeight()];

        //bm.getPixels(pix, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

       /* int halfOneY = bm.getHeight()/2;
        int halfOneX = bm.getWidth()/2;

        int numHalfOne = 0;
        int numHalfTwo = 0;

        int yellowHalfOne = 0;
        int yellowHalfTwo = 0;

        double threshold = .95;

        /*
        for (int y = 0; y < halfOneY; y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                int pixel = bm.getPixel(x, y);

                int red, green, yellow;

                red = Color.red(pixel);
                green = Color.green(pixel);

                yellow = (red + green) / 500;

                if (yellow > threshold) {
                    yellowHalfOne += yellow;
                }

                numHalfOne += 1;
            }
        }

        for (int y = halfOneY; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                int pixel = bm.getPixel(x, y);

                int red, green, yellow;

                red = Color.red(pixel);
                green = Color.green(pixel);

                yellow = (red + green)/500;

                if (yellow > threshold) {
                    yellowHalfTwo += yellow;
                }

                numHalfTwo += 1;
            }
        }

        //yellowHalfOne /= numHalfOne;
        //yellowHalfTwo /= numHalfTwo;

        telemetry.addData("Yellow H1", yellowHalfOne + "");
        telemetry.addData("Yellow H2", yellowHalfTwo + "");
        if (Math.abs(yellowHalfOne - yellowHalfTwo) < 10000) {
            telemetry.addData("Gold Block Found", "Off Screen");
        }
        else if (yellowHalfOne > yellowHalfTwo) {
            telemetry.addData("Gold Block Found", "Left");
        } else {
            telemetry.addData("Gold Block Found", "Right");
        }
        /*
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
              int pixel = bm.getPixel(x, y);

              int red, green, blue;
              red = Color.red(pixel);
              green = Color.green(pixel);
              blue = Color.blue(pixel);


             // telemetry.addData("Pixel (" + x + "," + y + ")", pixel + " (" + red + ", " + green + ", " + blue + ")");
            }
        }*/

        telemetry.addData("Pixel Analysis", "Done");
    }
}
