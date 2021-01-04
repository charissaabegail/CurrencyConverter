package de.thu.currencyconverter;

import java.util.Comparator;

/* Sort */
public class CustomComparator implements Comparator<ExchangeRate> {
    @Override
    public int compare(ExchangeRate o1, ExchangeRate o2) {
        return o1.getCurrencyName().compareTo(o2.getCurrencyName());
    }
}