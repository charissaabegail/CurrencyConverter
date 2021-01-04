package de.thu.currencyconverter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class ExchangeRateUpdateRunnable implements Runnable {
    private boolean updating = false;

    private ArrayList<ExchangeRate> mExchangeRatesNew;
    private ExchangeRateAdapter exchangeRateAdapter;
    private MainActivity mainActivity;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    ExchangeRateDatabase erd = new ExchangeRateDatabase();



    public ExchangeRateUpdateRunnable(MainActivity activity, Spinner spinnerFrom, Spinner spinnerTo){
        this.mainActivity = activity;
        this.spinnerFrom = spinnerFrom;
        this.spinnerTo = spinnerTo;
    }

    @Override
    public void run() {
        while(true){
            synchronized (ExchangeRateUpdateRunnable.this){
                if(updating){
                    updateCurrencies();
                }
            }

            try {
                Thread.sleep(1000); //This thread is running for 1s
            } catch (InterruptedException e) {

            }

        }
    }

    public void updateCurrencies() {


        Log.i("updateCurrencies","HELLO");
        Runnable r = new Runnable() {
            @Override
            public void run() {
               // textView.setText(value + " Seconds");

                // 2. Send request to www.ecb.europa.eu api
                String queryString = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"; //android does not allow unencrypted traffic anymore so we change http - https (cant query database)

                try {
                    URL url = new URL(queryString);
                    URLConnection connection = url.openConnection();

                    InputStream stream = connection.getInputStream();
                    String encoding = connection.getContentEncoding();

                    // 3. Getting and Analyzing XML response
                    XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                    parser.setInput(stream, encoding);

                    // 4. Building a List<Movie>
                    int eventType = parser.getEventType(); //Where are we in the document


                    mExchangeRatesNew = new ArrayList<>();

                    Log.i("You can do it.", Double.toString(erd.getExchangeRate("PHP")));
                    mExchangeRatesNew.add(new ExchangeRate("EUR",erd.getExchangeRate("EUR"), erd.getFlagImage("EUR"), erd.getCapital("EUR")));
                    while(eventType != XmlPullParser.END_DOCUMENT){
                        // Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
                        if(eventType == XmlPullParser.START_TAG) {

                            if("cube".equalsIgnoreCase(parser.getName())){
                                if(parser.getAttributeValue(null, "currency") != null){
                                    String currencyName = parser.getAttributeValue(null, "currency");
                                    String rateForOneEuro = parser.getAttributeValue(null,"rate");

                                    if(erd.isCurrencyExist(currencyName) == true) {
                                        mExchangeRatesNew.add(new ExchangeRate(currencyName,Double.parseDouble(rateForOneEuro), erd.getFlagImage(currencyName), erd.getCapital(currencyName)));

                                        Log.i("You can do it.", rateForOneEuro);
                                    } else {
                                        //Feature to be added later, because web api for currencies does not contain
                                        //flag_image and capital
                                        // ExchangeRate m = new ExchangeRate(currencyName,rateForOneEuro,flagImage,capital );
                                        // exchangeRateList.add(m);
                                    }



                                }
                            }
                        }
                        eventType = parser.next();
                    }

                    //Custom Comparator for Sorting of updated spinner values
                    Collections.sort(mExchangeRatesNew, new CustomComparator());

                    // Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();

                    // 2. refreshing the DOM spinners
                   /* exchangeRateAdapter = new ExchangeRateAdapter(activity,mExchangeRatesNew);
                    spinnerFrom.setAdapter(exchangeRateAdapter);


                    exchangeRateAdapter = new ExchangeRateAdapter(activity,mExchangeRatesNew);
                    spinnerTo.setAdapter(exchangeRateAdapter); */


                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // mainActivity.getAdapter().notifyDataSetChanged();
                            exchangeRateAdapter = new ExchangeRateAdapter(mainActivity,mExchangeRatesNew);
                            spinnerFrom.setAdapter(exchangeRateAdapter);


                            exchangeRateAdapter = new ExchangeRateAdapter(mainActivity,mExchangeRatesNew);
                            spinnerTo.setAdapter(exchangeRateAdapter);
                        }
                    });



                } catch (Exception e) {
                    Log.e("ExchangeRateList", "Can't query database! "+ e.toString());
                    e.printStackTrace();
                }
            }
        };
        // Send runnable r to UI thread (and execute run() method whenever it make sense)
        //textView.post(r); //this posting some code in the UI (see illustration - Chapter 8)
        spinnerFrom.post(r);
        spinnerTo.post(r);

    }

    public void setUpdating(boolean isUpdating){
         this.updating = isUpdating;
    }



}
