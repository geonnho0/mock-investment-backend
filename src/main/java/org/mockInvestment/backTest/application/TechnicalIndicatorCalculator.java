package org.mockInvestment.backTest.application;

import org.mockInvestment.stockPrice.domain.StockPriceCandle;

import java.util.LinkedList;
import java.util.List;

public class TechnicalIndicatorCalculator {

    private static final int PERIOD_FOR_RSI = 14;

    private static final int PERIOD_FOR_MACD_SHORT = 12;

    private static final int PERIOD_FOR_MACD_LONG = 26;

    private static final int PERIOD_FOR_MACD_SIGNAL = 9;


    public static double calculateRSI(List<StockPriceCandle> stockPriceCandles) {
        List<Double> gains = new LinkedList<>();
        List<Double> losses = new LinkedList<>();

        for (int i = 1; i < stockPriceCandles.size(); i++) {
            StockPriceCandle current = stockPriceCandles.get(i);
            StockPriceCandle previous = stockPriceCandles.get(i - 1);

            double priceDiff = current.getClose() - previous.getClose();

            if (priceDiff > 0) {
                gains.add(priceDiff);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(-priceDiff);
            }

            if (i >= PERIOD_FOR_RSI) {
                gains.remove(0);
                losses.remove(0);
            }
        }

        double avgGain = gains.stream().mapToDouble(Double::doubleValue).sum() / PERIOD_FOR_RSI;
        double avgLoss = losses.stream().mapToDouble(Double::doubleValue).sum() / PERIOD_FOR_RSI;

        double relativeStrength = avgGain / avgLoss;

        return 100 - (100 / (1 + relativeStrength));
    }

    public static double[] calculateMovingAverage(List<StockPriceCandle> stockPriceCandles, int period, String type) {
        double[] data = stockPriceCandles.stream()
                .mapToDouble(stockPriceCandle -> {
                    if (type.equals("price"))
                        return stockPriceCandle.getClose();
                    return stockPriceCandle.getVolume();
                })
                .toArray();

        return calculateMovingAverage(data, period);
    }

    public static double[] calculateMACD(List<StockPriceCandle> stockPriceCandles) {
        double[] data = stockPriceCandles.stream()
                .mapToDouble(stockPriceCandle -> stockPriceCandle.getClose())
                .toArray();
        double[] shortMA = calculateMovingAverage(data, PERIOD_FOR_MACD_SHORT);
        double[] longMA = calculateMovingAverage(data, PERIOD_FOR_MACD_LONG);
        double[] macd = new double[longMA.length];

        for (int i = 0; i < longMA.length; i++) {
            macd[i] = shortMA[i + shortMA.length - longMA.length] - longMA[i];
        }

        return macd;
    }

    public static double[] calculateSignalLine(double[] macd) {
        double[] signalLine = new double[macd.length - PERIOD_FOR_MACD_SIGNAL + 1];
        double[] signalLineMA = calculateMovingAverage(macd, PERIOD_FOR_MACD_SIGNAL);

        for (int i = 0; i <= macd.length - PERIOD_FOR_MACD_SIGNAL; i++) {
            signalLine[i] = signalLineMA[i];
        }

        return signalLine;
    }

    private static double[] calculateMovingAverage(double[] data, int period) {
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
