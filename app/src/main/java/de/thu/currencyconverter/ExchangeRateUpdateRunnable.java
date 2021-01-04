package de.thu.currencyconverter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class ExchangeRateUpdateRunnable implements Runnable {
    private boolean updating = false;

    private ExchangeRateAdapter exchangeRateAdapter;
    private MainActivity mainActivity;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    ExchangeRateDatabase erd = new ExchangeRateDatabase();

    public ExchangeRateUpdateRunnable(MainActivity activity){
        this.mainActivity = activity;
        //this.spinnerFrom = spinnerFrom;
        //this.spinnerTo = spinnerTo;
    }

    @Override
    public void run() {
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


            Log.i("You can do it.", Double.toString(erd.getExchangeRate("PHP")));
            // mExchangeRatesNew.add(new ExchangeRate("EUR",erd.getExchangeRate("EUR"), erd.getFlagImage("EUR"), erd.getCapital("EUR")));
            while(eventType != XmlPullParser.END_DOCUMENT){
                // Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
                if(eventType == XmlPullParser.START_TAG) {

                    if("cube".equalsIgnoreCase(parser.getName()) == true && parser.getAttributeCount() == 2){
                        if(parser.getAttributeValue(null, "currency") != null){
                            String currencyName = parser.getAttributeValue(null, "currency");
                            String rateForOneEuro = parser.getAttributeValue(null,"rate");

                            mainActivity.getDatabase().setExchangeRate(currencyName, Double.parseDouble(rateForOneEuro));
                        }
                    }
                }
                eventType = parser.next();
            }


        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", "MalformedURLException");
        } catch (IOException e) {
            Log.e("IOException", "IOException");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", "XmlPullParserException");
        }

        //a toast can only be started from the UI thread. This means that new Runnable needs to be created
        //new Runnable is passed to the UI thread for execution
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity.getApplicationContext(),
                        "The rates have been successfully updated", Toast.LENGTH_LONG).show();
                mainActivity.getAdapter().notifyDataSetChanged();
                // notifier.showOrUpdateNotification();
            }
        });
    }

}
