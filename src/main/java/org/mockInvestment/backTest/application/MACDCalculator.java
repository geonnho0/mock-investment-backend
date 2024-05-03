package org.mockInvestment.backTest.application;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MACDCalculator {

    private static final int PERIOD_FOR_MACD_SHORT = 12;

    private static final int PERIOD_FOR_MACD_LONG = 26;

    private static final int PERIOD_FOR_MACD_SIGNAL = 9;

    private final List<Double> data;

    private final double[] macd;


    public MACDCalculator(List<Double> data) {
        this.data = new ArrayList<>(data);
        macd = new double[this.data.size() - PERIOD_FOR_MACD_LONG + 1];
    }

    public void add(Double newData) {
        data.add(newData);
        data.remove(0);
    }

    public double[] calculateMACD() {
        double[] data = this.data.stream().mapToDouble(Double::doubleValue).toArray();
        double[] shortMA = calculateMovingAverage(data, PERIOD_FOR_MACD_SHORT);
        double[] longMA = calculateMovingAverage(data, PERIOD_FOR_MACD_LONG);
        Arrays.fill(macd, 0);

        for (int i = 0; i < longMA.length; i++) {
            macd[i] = shortMA[i + shortMA.length - longMA.length] - longMA[i];
        }

        return macd;
    }

    public double[] calculateSignalLine() {
        double[] signalLine = new double[macd.length - PERIOD_FOR_MACD_SIGNAL + 1];
        double[] signalLineMA = calculateMovingAverage(macd, PERIOD_FOR_MACD_SIGNAL);

        for (int i = 0; i <= macd.length - PERIOD_FOR_MACD_SIGNAL; i++) {
            signalLine[i] = signalLineMA[i];
        }

        return signalLine;
    }

    private double[] calculateMovingAverage(double[] data, int period) {
        double[] movingAverage = new double[data.length - period + 1];

        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += data[i];
        }
        movingAverage[0] = sum / period;
        for (int i = period; i < data.length; i++) {
            sum -= data[i - period];
            sum += data[i];
            movingAverage[i - period] = sum / period;
        }

        return movingAverage;
    }

}
