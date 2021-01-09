package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
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

    private final int UPDATE_INTERVAL_HRS = 24 * 60 * 60 * 1000; //24 hours in milliseconds
    private String cCurrencyFr = "";
    private String cCurrencyTo = "";
    private Double cExchangeRate = 0.0;
    private Double cAmount = 0.0;
    private Double cExchangeAmount = 0.0;

    private Double cCurrFrom = 0.0;
    private Double cCurrTo = 0.0;


    private String clickedFromCurrency = "";
    private String clickedToCurrency = "";

    private static DecimalFormat df2 = new DecimalFormat("#.##");


    //For custom spinner adapter
    private ArrayList<ExchangeRate> mExchangeRates;
    private ArrayList<ExchangeRate> mExchangeRatesNew;
    private ExchangeRateAdapter exchangeRateAdapter;
    ExchangeRateDatabase erd = new ExchangeRateDatabase();
    ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    ShareActionProvider shareActionProvider = null;
    ExchangeRateUpdateRunnable exchangeRateUpdateRunnable;

    private static final String TAG = "MainActivity";

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }
    // Overriden methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        //Quick fix for Network Error
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); */

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
                Log.i("New spinner from:",Double.toString(cCurrFrom));
                Toast.makeText(MainActivity.this,clickedFromCurrency + " selected. " + cCurrFrom + " exchange rate.", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        scheduleJobRateUpdater();
    }
    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        //get amount to be converted and result
        EditText txt_amtToConvert = findViewById(R.id.txt_amount);
        TextView txt_convertedAmt = findViewById(R.id.txt_result);

        //Retrieve value from preference. Empty string by default
        String amountConverted = prefs.getString("amountConverted","");
        String amountResult = prefs.getString("amountResult","");

        // Retrieve spinner values
        Spinner spinnerFrom = findViewById(R.id.sp_fromval);
        Spinner spinnerTo = findViewById(R.id.sp_toval);
        int spinnerSourcePos = prefs.getInt("currencySpinnerSource",0);
        int spinnerTargetPos = prefs.getInt("currencySpinnerTarget",0);

        //Exercise 8.2:
        //Extend your app so it persists all rates retrieved from the ECB and restores the most current
        //local data when the app is started next time
        for (String currencyItem : exchangeRateDatabase.getCurrencies()) {
            String currencyRate = prefs.getString(currencyItem, "0.00");

            if ("EUR".equalsIgnoreCase(currencyItem)) {
                currencyRate = "1.00";
            }

            if (!("0.00".equals(currencyRate))) {
                exchangeRateDatabase.setExchangeRate(currencyItem, Double.parseDouble(currencyRate));
            }
        }

        spinnerFrom.setSelection(spinnerSourcePos);
        spinnerTo.setSelection(spinnerTargetPos);
        txt_amtToConvert.setText(amountConverted);
        txt_convertedAmt.setText(amountResult);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        EditText txt_amtToConvert = findViewById(R.id.txt_amount);
        TextView txt_convertedAmt = findViewById(R.id.txt_result);

        //Retrieve value from preference. Empty string by default
        String amountConverted = txt_amtToConvert.getText().toString();
        String amountResult = txt_convertedAmt.getText().toString();

        Spinner spinnerFrom = findViewById(R.id.sp_fromval);
        int spinnerSourcePos = spinnerFrom.getSelectedItemPosition();

        Spinner spinnerTo = findViewById(R.id.sp_toval);
        int spinnerTargetPos = spinnerTo.getSelectedItemPosition();

        //Exercise 8.2:
        //Extend your app so it persists all rates retrieved from the ECB and restores the most current
        //local data when the app is started next time
        for (String currencyItem : exchangeRateDatabase.getCurrencies()) {
            String currencyRate = Double.toString(exchangeRateDatabase.getExchangeRate(currencyItem));
            editor.putString(currencyItem, currencyRate);
        }


        editor.putString("amountConverted",amountConverted);
        editor.putString("amountResult",amountResult);
        editor.putInt("currencySpinnerSource",spinnerSourcePos);
        editor.putInt("currencySpinnerTarget",spinnerTargetPos);

        editor.apply();
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
                scheduleJobRateUpdater();
                return true;

            default:
                startActivity(new Intent(MainActivity.this, CurrencyListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    // User-defined methods
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

    public void calculate(View view){
        TextView amt = (TextView)findViewById(R.id.txt_amount); //amount to exchange
        TextView res = (TextView)findViewById(R.id.txt_result);

        cAmount = Double.parseDouble(amt.getText().toString());
        cExchangeRate =  cCurrTo / cCurrFrom;
        cExchangeAmount = cAmount * cExchangeRate;

        String sValue = (String) String.format("%.2f", cExchangeAmount);
        Double newValue = Double.parseDouble(sValue);
        res.setText(newValue.toString());

        setShareText("Currency Conversion: From " + clickedFromCurrency + " to " + clickedToCurrency + " = " + cExchangeAmount.toString() + ". Thank you!");

    }

    public ExchangeRateAdapter getAdapter() {
        return exchangeRateAdapter;
    }


    public ExchangeRateDatabase getDatabase(){
        return exchangeRateDatabase;
    }

    //JobScheduler for Rates Update
    public void scheduleJobRateUpdater() {
        ComponentName componentName = new ComponentName(this, UpdateRatesSchedulerService.class);

        JobInfo info = new JobInfo.Builder(123, componentName)
                // .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(UPDATE_INTERVAL_HRS)
                .build();


        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }


}

