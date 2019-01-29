package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.DcMotor;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Timeseries streaming.
 * Writes and reads timeseries streams.
 */
public class PropertyStream {
    /**
     * Data point in a time series.
     * A variable has a Datapoint's value until the next instance in the stream.
     */
    public static class DataPoint implements java.io.Serializable {
        // Name of the variable.
        public final String varname;
        // Time of the data point (seconds, inclusive).
        public final DcMotor.Direction motorDirection;

        public final DcMotor.ZeroPowerBehavior motorBehavior;

        public DataPoint(String varname, DcMotor.Direction dir, DcMotor.ZeroPowerBehavior behavior) {
            this.varname = varname;
            this.motorDirection = dir;
            this.motorBehavior = behavior;
        }
    }

    /**
     * Timeseries writer.
     */
    public static class Writer {
        /**
         * Creates the Writer.
         *
         * @param outputStream The output stream to write to.
         */
        public Writer(OutputStream outputStream) throws Exception {
            this.outputStream = new ObjectOutputStream(outputStream);
        }

        /**
         * Writes a DataPoint to the output stream.
         * Calls to this function must be done with non-decreasing timestamps.
         */
        public void write(DataPoint point) throws Exception {
            outputStream.writeObject(point);
        }

        // The object output stream.
        private ObjectOutputStream outputStream;
    }

    /**
     * Timeseries reader.
     */
    public static class Reader {
        /**
         * Creates the Reader.
         *
         * @param inputStream The input stream to read from.
         */
        public Reader(InputStream inputStream) throws Exception {
            this.inputStream = new ObjectInputStream(inputStream);
            nextPoint = null;
            endOfStream = false;
        }

        /**
         * Reads a DataPoint from the input stream.
         *
         * @return DataPoint if available, null otherwise.
         */
        public DataPoint read() throws Exception {
            // Return next data point if stored.
            if (nextPoint != null) {
                DataPoint ret = nextPoint;
                nextPoint = null;
                return ret;
            }
            try {
                return (DataPoint) inputStream.readObject();
            } catch (EOFException e) {
                endOfStream = true;
                return null;
            }
        }

        // The object input stream.
        private ObjectInputStream inputStream;
        // The next point to be returned.
        private PropertyStream.DataPoint nextPoint;
        // Whether the read is done.
        private boolean endOfStream;
    }
}