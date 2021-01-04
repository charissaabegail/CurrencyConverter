package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String cCurrencyFr = "";
    private String cCurrencyTo = "";
    private Double cExchangeRate = 0.0;
    private Double cAmount = 0.0;
    private Double cExchangeAmount = 0.0;

    private Double cCurrFrom = 0.0;
    private Double cCurrTo = 0.0;

    private String clickedFromCurrency = "";
    private String clickedToCurrency = "";


    //For custom spinner adapter
    private ArrayList<ExchangeRate> mExchangeRates;
    private ArrayList<ExchangeRate> mExchangeRatesNew;
    private ExchangeRateAdapter exchangeRateAdapter;
    ExchangeRateDatabase erd = new ExchangeRateDatabase();
    ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    ShareActionProvider shareActionProvider = null;
    ExchangeRateUpdateRunnable exchangeRateUpdateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Quick fix for Network Error

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); */


        //create a runnable object
      //  exchangeRateUpdateRunnable = new ExchangeRateUpdateRunnable(MainActivity.this,(Spinner) findViewById(R.id.sp_fromval),(Spinner) findViewById(R.id.sp_toval));

        //create a new thread and pass runnable - passing this runnable process to a new thread
        //Thread t = new Thread(exchangeRateUpdateRunnable);

        //t.start(); //let the thread run in the background

       /* //create a runnable object
        exchangeRateUpdateRunnable = new ExchangeRateUpdateRunnable(MainActivity.this,(Spinner) findViewById(R.id.sp_fromval),(Spinner) findViewById(R.id.sp_toval));

        //create a new thread and pass runnable - passing this runnable process to a new thread
        Thread t = new Thread(exchangeRateUpdateRunnable);

        t.start(); //let the thread run in the background */

        //initialize mExchangeRates in ArrayList


        initList();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinnerFrom = findViewById(R.id.sp_fromval);
        exchangeRateAdapter = new ExchangeRateAdapter(exchangeRateDatabase);
        spinnerFrom.setAdapter(exchangeRateAdapter);


        Spinner spinnerTo = findViewById(R.id.sp_toval);
        exchangeRateAdapter = new ExchangeRateAdapter(exchangeRateDatabase);
        spinnerTo.setAdapter(exchangeRateAdapter);


        //SpinnerFrom Selected listener
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedFromCurrency = parent.getItemAtPosition(position).toString();
                cCurrFrom = exchangeRateDatabase.getExchangeRate(clickedFromCurrency);
                Toast.makeText(MainActivity.this,clickedFromCurrency + " selected. " + cCurrFrom + " exchange rate.", Toast.LENGTH_SHORT).show();
             //   ExchangeRate fromCurrency =
             /*   ExchangeRate fromCurrency = (ExchangeRate) parent.getItemAtPosition(position);
                clickedFromCurrency = fromCurrency.getCurrencyName();

                */
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedToCurrency = parent.getItemAtPosition(position).toString();
                cCurrTo = exchangeRateDatabase.getExchangeRate(clickedToCurrency);
                Toast.makeText(MainActivity.this,clickedToCurrency + " selected. " + cCurrTo + " exchange rate.", Toast.LENGTH_SHORT).show();
             /*   ExchangeRate toCurrency = (ExchangeRate) parent.getItemAtPosition(position);

                clickedToCurrency = toCurrency.getCurrencyName();
                cCurrTo = exchangeRateDatabase.getExchangeRate(clickedToCurrency);
                Toast.makeText(MainActivity.this,clickedToCurrency + " selected. " + cCurrTo + " exchange rate.", Toast.LENGTH_SHORT).show();
    */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void initList(){
        mExchangeRates = null;
        mExchangeRates = new ArrayList<>();
        ExchangeRateDatabase erd = new ExchangeRateDatabase();
        String keySet;
        for(int i = 0; i < erd.getCurrencies().length; i++ ){
            keySet = exchangeRateDatabase.getCurrencies()[i];
            mExchangeRates.add(new ExchangeRate(keySet,erd.getExchangeRate(keySet), erd.getCapital(keySet)));
        }
    }

    public void initListCurrencies(String[] currencies){
        mExchangeRates = new ArrayList<>();
        ExchangeRateDatabase erd = new ExchangeRateDatabase();
        String keySet;
        for(int i = 0; i < currencies.length; i++ ){
            keySet = erd.getCurrencies()[i];
            mExchangeRates.add(new ExchangeRate(keySet,erd.getExchangeRate(keySet), erd.getCapital(keySet)));
        }
    }
    public void calculate(View view){
        TextView amt = (TextView)findViewById(R.id.txt_amount); //amount to exchange
        TextView res = (TextView)findViewById(R.id.txt_result);

        cAmount = Double.parseDouble(amt.getText().toString());

        cExchangeAmount = cAmount * cCurrTo;
        res.setText(cExchangeAmount.toString());

        setShareText("Currency Conversion: From " + clickedFromCurrency + " to " + clickedToCurrency + " = " + cExchangeAmount.toString() + ". Thank you! - [MORALES]");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String currency = (String)parent.getItemAtPosition(position).toString();
        ExchangeRateDatabase erd = new ExchangeRateDatabase();
        cExchangeRate = erd.getExchangeRate(currency);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
       MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        setShareText(null);
        return true;
    }
    private void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.toolbar_menu_item_currency_list:

                startActivity(new Intent(MainActivity.this, CurrencyListActivity.class));
                return true;
            case R.id.toolbar_menu_item_refresh:
                //new Thread((Runnable) new ExchangeRateUpdateRunnable(this,(Spinner) findViewById(R.id.sp_fromval),(Spinner) findViewById(R.id.sp_toval))).start();

                new Thread((Runnable) new ExchangeRateUpdateRunnable(this)).start();
                return true;

                /*exchangeRateUpdateRunnable.updateCurrencies();
                return true; */
                //exchangeRateUpdateRunnable.updateCurrencies();
                // 1. update objects (sorted) from web api
                   // updateCurrencies();

                // 2. refreshing the DOM spinners
                  /*  Spinner spinnerFrom = findViewById(R.id.sp_fromval);
                    exchangeRateAdapter = new ExchangeRateAdapter(this,mExchangeRatesNew);
                    spinnerFrom.setAdapter(exchangeRateAdapter);

                    Spinner spinnerTo = findViewById(R.id.sp_toval);
                    exchangeRateAdapter = new ExchangeRateAdapter(this,mExchangeRatesNew);
                    spinnerTo.setAdapter(exchangeRateAdapter); */


            default:
                startActivity(new Intent(MainActivity.this, CurrencyListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    public ExchangeRateAdapter getAdapter() {
        return exchangeRateAdapter;
    }


/*
    public void updateCurrencies() {

        // 2. Send request to OmdbAPI
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
            ExchangeRateDatabase erd = new ExchangeRateDatabase();

            mExchangeRatesNew.add(new ExchangeRate("EUR",erd.getExchangeRate("EUR"), erd.getFlagImage("EUR"), erd.getCapital("EUR")));
            while(eventType != XmlPullParser.END_DOCUMENT){
                Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
                if(eventType == XmlPullParser.START_TAG) {

                    if("cube".equalsIgnoreCase(parser.getName())){
                        if(parser.getAttributeValue(null, "currency") != null){
                            String currencyName = parser.getAttributeValue(null, "currency");
                            String rateForOneEuro = parser.getAttributeValue(null,"rate");

                            if(erd.isCurrencyExist(currencyName) == true) {
                                mExchangeRatesNew.add(new ExchangeRate(currencyName,Double.parseDouble(rateForOneEuro), erd.getFlagImage(currencyName), erd.getCapital(currencyName)));
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

            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            initListCurrencies(erd.getCurrencies());


        } catch (Exception e) {
            Log.e("ExchangeRateList", "Can't query database! "+ e.toString());
            e.printStackTrace();
        }

    }
*/

    public ExchangeRateDatabase getDatabase(){
        return exchangeRateDatabase;
    }

}

