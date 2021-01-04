package de.thu.currencyconverter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExchangeRateDatabase {
    // Exchange rates to EURO - price for 1 Euro
    private final static ExchangeRate[] RATES = {
           /* new ExchangeRate("EUR", 1.0, R.drawable.flag_eur),
            new ExchangeRate("USD", 1.0845, R.drawable.flag_usd),
            new ExchangeRate("JPY", 130.02, R.drawable.flag_jpy),
            new ExchangeRate("BGN", 1.9558, R.drawable.flag_bgn),
            new ExchangeRate("CZK", 27.473, R.drawable.flag_czk),
            new ExchangeRate("DKK", 7.4690, R.drawable.flag_dkk),
            new ExchangeRate("GBP", 0.73280, R.drawable.flag_gbp),
            new ExchangeRate("HUF", 299.83, R.drawable.flag_huf),
            new ExchangeRate("PLN", 4.0938, R.drawable.flag_pln),
            new ExchangeRate("RON", 4.4050, R.drawable.flag_ron),
            new ExchangeRate("SEK", 9.3207, R.drawable.flag_sek),
            new ExchangeRate("CHF", 1.0439, R.drawable.flag_chf),
            new ExchangeRate("NOK", 8.6545, R.drawable.flag_nok),
            new ExchangeRate("HRK", 7.6448, R.drawable.flag_hrk),
            new ExchangeRate("RUB", 62.5595, R.drawable.flag_rub),
            new ExchangeRate("TRY", 2.8265, R.drawable.flag_try),
            new ExchangeRate("AUD", 1.4158, R.drawable.flag_aud),
            new ExchangeRate("BRL", 3.5616, R.drawable.flag_brl),
            new ExchangeRate("CAD", 1.3709, R.drawable.flag_cad),
            new ExchangeRate("CNY", 6.7324, R.drawable.flag_cny),
            new ExchangeRate("HKD", 8.4100, R.drawable.flag_hkd),
            new ExchangeRate("IDR", 14172.71, R.drawable.flag_idr),
            new ExchangeRate("ILS", 4.3019, R.drawable.flag_ils),
            new ExchangeRate("INR", 67.9180, R.drawable.flag_inr),
            new ExchangeRate("KRW", 1201.04, R.drawable.flag_krw),
            new ExchangeRate("MXN", 16.5321, R.drawable.flag_mxn),
            new ExchangeRate("MYR", 4.0246, R.drawable.flag_myr),
            new ExchangeRate("NZD", 1.4417, R.drawable.flag_nzd),
            new ExchangeRate("PHP", 48.527, R.drawable.flag_php),
            new ExchangeRate("SGD", 1.4898, R.drawable.flag_sgd),
            new ExchangeRate("THB", 35.328, R.drawable.flag_thb),
            new ExchangeRate("ZAR", 13.1446, R.drawable.flag_zar),*/
            new ExchangeRate("EUR", 1.0, R.drawable.flag_eur,"Bruxelles"),
            new ExchangeRate("USD", 1.0845, R.drawable.flag_usd,"Washington"),
            new ExchangeRate("JPY", 130.02, R.drawable.flag_jpy,"Tokyo"),
            new ExchangeRate("BGN", 1.9558, R.drawable.flag_bgn,"Sofia"),
            new ExchangeRate("CZK", 27.473, R.drawable.flag_czk,"Prague"),
            new ExchangeRate("DKK", 7.4690, R.drawable.flag_dkk,"Copenhagen"),
            new ExchangeRate("GBP", 0.73280, R.drawable.flag_gbp,"London"),
            new ExchangeRate("HUF", 299.83, R.drawable.flag_huf,"Budapest"),
            new ExchangeRate("PLN", 4.0938, R.drawable.flag_pln,"Warsaw"),
            new ExchangeRate("RON", 4.4050, R.drawable.flag_ron,"Bucharest"),
            new ExchangeRate("SEK", 9.3207, R.drawable.flag_sek,"Stockholm"),
            new ExchangeRate("CHF", 1.0439, R.drawable.flag_chf,"Bern"),
            new ExchangeRate("NOK", 8.6545, R.drawable.flag_nok,"Oslo"),
            new ExchangeRate("HRK", 7.6448, R.drawable.flag_hrk,"Zagreb"),
            new ExchangeRate("RUB", 62.5595, R.drawable.flag_rub,"Moscow"),
            new ExchangeRate("TRY", 2.8265, R.drawable.flag_try,"Ankara"),
            new ExchangeRate("AUD", 1.4158, R.drawable.flag_aud,"Canberra"),
            new ExchangeRate("BRL", 3.5616, R.drawable.flag_brl,"Brasilia"),
            new ExchangeRate("CAD", 1.3709, R.drawable.flag_cad,"Ottawa"),
            new ExchangeRate("CNY", 6.7324, R.drawable.flag_cny,"Beijing"),
            new ExchangeRate("HKD", 8.4100, R.drawable.flag_hkd,"Hong Kong"),
            new ExchangeRate("IDR", 14172.71, R.drawable.flag_idr,"Jakarta"),
            new ExchangeRate("ILS", 4.3019, R.drawable.flag_ils,"Jerusalem"),
            new ExchangeRate("INR", 67.9180, R.drawable.flag_inr,"New Delhi"),
            new ExchangeRate("KRW", 1201.04, R.drawable.flag_krw,"Seoul"),
            new ExchangeRate("MXN", 16.5321, R.drawable.flag_mxn,"Mexico City"),
            new ExchangeRate("MYR", 4.0246, R.drawable.flag_myr,"Kuala Lumpur"),
            new ExchangeRate("NZD", 1.4417, R.drawable.flag_nzd,"Wellington"),
            new ExchangeRate("PHP", 48.527, R.drawable.flag_php,"Manila"),
            new ExchangeRate("SGD", 1.4898, R.drawable.flag_sgd,"Singapore"),
            new ExchangeRate("THB", 35.328, R.drawable.flag_thb,"Bangkok"),
            new ExchangeRate("ZAR", 13.1446, R.drawable.flag_zar,"Cape Town"),
    };

    //private final static Map<String,Map<Double, Integer>> CURRENCIES_MAP = new HashMap<String,Map<Double, Integer>>();
    private final static Map<String,Double> CURRENCIES_MAP = new HashMap<>();
    private final static Map<String, Integer> CURRENCIES_MAP_FLAGS = new HashMap<>();
    private final static Map<String, String> CURRENCIES_MAP_CAPITAL = new HashMap<>();
    private final static String[] CURRENCIES_LIST;
    private final static String[] CURRENCIES_LIST_FLAGS;
    private final static String[] CURRENCIES_LIST_CAPITAL;

    static {
        for (ExchangeRate r : RATES) {
            CURRENCIES_MAP.put(r.getCurrencyName(), r.getRateForOneEuro());
            CURRENCIES_MAP_FLAGS.put(r.getCurrencyName(), r.getFlagImage());
            CURRENCIES_MAP_CAPITAL.put(r.getCurrencyName(), r.getCapital());
        }
        CURRENCIES_LIST = new String[CURRENCIES_MAP.size()];
        CURRENCIES_LIST_FLAGS = new String[CURRENCIES_MAP_FLAGS.size()];
        CURRENCIES_LIST_CAPITAL = new String[CURRENCIES_MAP_CAPITAL.size()];

        CURRENCIES_MAP.keySet().toArray(CURRENCIES_LIST);
        CURRENCIES_MAP_FLAGS.keySet().toArray(CURRENCIES_LIST_FLAGS);
        CURRENCIES_MAP_CAPITAL.keySet().toArray(CURRENCIES_LIST_CAPITAL);

        Arrays.sort(CURRENCIES_LIST);
        Arrays.sort(CURRENCIES_LIST_FLAGS);
        Arrays.sort(CURRENCIES_LIST_CAPITAL);

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

    public int getFlagImage(String currency){
        return CURRENCIES_MAP_FLAGS.get(currency);
    }

    /**
     * Gets exchange rate for currency (equivalent for one Euro)
     */

    public double getExchangeRate(String currency) {
        return CURRENCIES_MAP.get(currency);
    }

    /**
     * Gets capital given the flag
     */
    public String getCapital(String currency) {
        return CURRENCIES_MAP_CAPITAL.get(currency);
    }


    /**
     * Converts a value from a currency to another one
     * @return converted value
     */
    public double convert(double value, String currencyFrom, String currencyTo) {
        return value / CURRENCIES_MAP.get(currencyFrom) * CURRENCIES_MAP.get(currencyTo);
    }

    /**
     * Fill the list directly from the European Central Bank (ECB)
     */

    public void setExchangeRate(String currency, Double rate){
        CURRENCIES_MAP.put(currency, rate);
    }

    public void updateCurrencyList(String currency, Double rate){
        CURRENCIES_MAP.put(currency, rate);
        CURRENCIES_MAP.keySet().toArray(CURRENCIES_LIST);
        Arrays.sort(CURRENCIES_LIST);
    }

    public boolean isCurrencyExist(String currency){
        // Get the iterator over the HashMap
        Iterator<Map.Entry<String, Double> >
                iterator = CURRENCIES_MAP.entrySet().iterator();

        // flag to store result
        boolean isKeyPresent = false;

        // Iterate over the HashMap
        while (iterator.hasNext()) {

            // Get the entry at this iteration
            Map.Entry<String, Double>
                    entry
                    = iterator.next();

            // Check if this key is the required key
            if (currency.equals(entry.getKey())) {
                isKeyPresent = true;
            }
        }
        return isKeyPresent;
    }

    /* set exchange rate */




}
