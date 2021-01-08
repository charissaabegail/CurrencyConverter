package de.thu.currencyconverter;

import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExchangeRateDatabase {
    // Exchange rates to EURO - price for 1 Euro
    private final static ExchangeRate[] RATES = {

            new ExchangeRate("EUR", 1.0,"Bruxelles"),
            new ExchangeRate("USD", 1.0845,"Washington"),
            new ExchangeRate("JPY", 130.02,"Tokyo"),
            new ExchangeRate("BGN", 1.9558,"Sofia"),
            new ExchangeRate("CZK", 27.473,"Prague"),
            new ExchangeRate("DKK", 7.4690,"Copenhagen"),
            new ExchangeRate("GBP", 0.73280,"London"),
            new ExchangeRate("HUF", 299.83,"Budapest"),
            new ExchangeRate("PLN", 4.0938,"Warsaw"),
            new ExchangeRate("RON", 4.4050,"Bucharest"),
            new ExchangeRate("SEK", 9.3207,"Stockholm"),
            new ExchangeRate("CHF", 1.0439,"Bern"),
            new ExchangeRate("NOK", 8.6545,"Oslo"),
            new ExchangeRate("HRK", 7.6448,"Zagreb"),
            new ExchangeRate("RUB", 62.5595,"Moscow"),
            new ExchangeRate("TRY", 2.8265,"Ankara"),
            new ExchangeRate("AUD", 1.4158,"Canberra"),
            new ExchangeRate("BRL", 3.5616,"Brasilia"),
            new ExchangeRate("CAD", 1.3709,"Ottawa"),
            new ExchangeRate("CNY", 6.7324,"Beijing"),
            new ExchangeRate("HKD", 8.4100,"Hong Kong"),
            new ExchangeRate("IDR", 14172.71,"Jakarta"),
            new ExchangeRate("ILS", 4.3019,"Jerusalem"),
            new ExchangeRate("INR", 67.9180,"New Delhi"),
            new ExchangeRate("KRW", 1201.04,"Seoul"),
            new ExchangeRate("MXN", 16.5321,"Mexico City"),
            new ExchangeRate("MYR", 4.0246,"Kuala Lumpur"),
            new ExchangeRate("NZD", 1.4417,"Wellington"),
            new ExchangeRate("PHP", 48.527,"Manila"),
            new ExchangeRate("SGD", 1.4898,"Singapore"),
            new ExchangeRate("THB", 35.328,"Bangkok"),
            new ExchangeRate("ZAR", 13.1446,"Cape Town"),
    };


    private final static Map<String, ExchangeRate> CURRENCIES_MAP = new HashMap<>();

    private final static String[] CURRENCIES_LIST;

    static {
        for (ExchangeRate r : RATES) {
            CURRENCIES_MAP.put(r.getCurrencyName(), r);
        }
        CURRENCIES_LIST = new String[CURRENCIES_MAP.size()];

        CURRENCIES_MAP.keySet().toArray(CURRENCIES_LIST);
        Arrays.sort(CURRENCIES_LIST);

    }

    /**
     * Returns list of currency names
     */

    public String[] getCurrencies() {
        return CURRENCIES_LIST;
    }

    public List<ExchangeRate> getCurrenciesList(){
        return Arrays.asList(RATES);
    }

    /**
     * Gets flag image given the currency as keyset
     */

   /* public int getFlagImage(String currency){
        return CURRENCIES_MAP_FLAGS.get(currency);
    } */

    /**
     * Gets exchange rate for currency (equivalent for one Euro)
     */

    public double getExchangeRate(String currency) {
        return CURRENCIES_MAP.get(currency).getRateForOneEuro();
    }

    public String getCapital(String currency) {
        return CURRENCIES_MAP.get(currency).getCapital();
    }

    /**
     * Converts a value from a currency to another one
     * @return converted value
     */
    public double convert(double value, String currencyFrom, String currencyTo) {
        return value / getExchangeRate(currencyFrom) * getExchangeRate(currencyTo);
    }

    /**
     * Update ratePerOneEuro from API
     */
    public void setExchangeRate(String currency, Double rate){

        for (ExchangeRate r : RATES) {
            if(r.getCurrencyName().equalsIgnoreCase(currency)) {
                r.setRateForOneEuro(rate);
                CURRENCIES_MAP.put(currency, r);
            }
        }
        CURRENCIES_MAP.keySet().toArray(CURRENCIES_LIST);
        Arrays.sort(CURRENCIES_LIST);
    }


}
