package de.thu.currencyconverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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



    // Overriden methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onResume(){
        super.onResume();
        Spinner spinnerFrom = findViewById(R.id.sp_fromval);
        Spinner spinnerTo = findViewById(R.id.sp_toval);

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        EditText txt_amtToConvert = findViewById(R.id.txt_amount);
        TextView txt_convertedAmt = findViewById(R.id.txt_result);
        //Retrieve value from preference. Empty string by default
        String amountConverted = prefs.getString("amountConverted","");
        String amountResult = prefs.getString("amountResult","");

        int spinnerSourcePos = prefs.getInt("currencySpinnerSource",0);
        int spinnerTargetPos = prefs.getInt("currencySpinnerTarget",0);

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
                new Thread((Runnable) new ExchangeRateUpdateRunnable(this)).start();
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

        cExchangeAmount = cAmount * cCurrTo;
        res.setText(cExchangeAmount.toString());

        setShareText("Currency Conversion: From " + clickedFromCurrency + " to " + clickedToCurrency + " = " + cExchangeAmount.toString() + ". Thank you! - [MORALES]");

    }

    public ExchangeRateAdapter getAdapter() {
        return exchangeRateAdapter;
    }


    public ExchangeRateDatabase getDatabase(){
        return exchangeRateDatabase;
    }

}

