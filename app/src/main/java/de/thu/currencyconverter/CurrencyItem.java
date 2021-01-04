package de.thu.currencyconverter;

public class CurrencyItem {
    private String currencyName;
    private int imageFlag;

    public CurrencyItem(String currencyName, int imageFlag) {
        this.currencyName = currencyName;
        this.imageFlag = imageFlag;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public int getImageFlag() { return imageFlag; }
}
