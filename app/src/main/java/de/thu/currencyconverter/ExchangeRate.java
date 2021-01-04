package de.thu.currencyconverter;

public class ExchangeRate {
    private String currencyName;
    private double rateForOneEuro;
    //private int flagImage;
    private String capital;

    public ExchangeRate() {
        this.currencyName = "";
        this.rateForOneEuro = 0.0;
        //this.flagImage = flagImage;
        this.capital = "";

    }
    public ExchangeRate(String currencyName, double rateForOneEuro, String capital) {
        this.currencyName = currencyName;
        this.rateForOneEuro = rateForOneEuro;
        //this.flagImage = flagImage;
        this.capital = capital;

    }

    public String getCurrencyName() {
        return currencyName;
    }

    public double getRateForOneEuro() {
        return rateForOneEuro;
    }

    //public int getFlagImage() { return flagImage; }

    public String getCapital() { return capital; }

    public void setRateForOneEuro(double rateForOneEuro) {
        this.rateForOneEuro = rateForOneEuro;
    }
}
