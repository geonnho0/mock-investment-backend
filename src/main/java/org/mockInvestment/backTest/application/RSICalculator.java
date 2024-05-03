package org.mockInvestment.backTest.application;

import java.util.ArrayList;
import java.util.List;

public class RSICalculator {

    private static final int PERIOD_FOR_RSI = 14;

    private final List<Double> data;

    private final List<Double> gains = new ArrayList<>();

    private final List<Double> losses = new ArrayList<>();


    public RSICalculator(List<Double> data) {
        this.data = new ArrayList<>();
        for (Double d : data) {
            add(d);
        }
    }

    public void add(Double newData) {
        data.add(newData);
        if (data.size() > PERIOD_FOR_RSI) {
            removeFirst();
        }
        if (data.size() > 1) {
            updateGainsAndLosses();
        }
    }

    public void removeFirst() {
        data.remove(0);
        gains.remove(0);
        losses.remove(0);
    }

    public double calculate() {
        double avgGain = gains.stream().mapToDouble(Double::doubleValue).sum() / PERIOD_FOR_RSI;
        double avgLoss = losses.stream().mapToDouble(Double::doubleValue).sum() / PERIOD_FOR_RSI;
        double relativeStrength = avgGain / avgLoss;
        return 100 - (100 / (1 + relativeStrength));
    }

    private void updateGainsAndLosses() {
        Double current = data.get(data.size() - 1);
        Double previous = data.get(data.size() - 2);
        double diff = current - previous;

        if (diff > 0) {
            gains.add(diff);
            losses.add(0.0);
        } else {
            gains.add(0.0);
            losses.add(-diff);
        }
    }

}
