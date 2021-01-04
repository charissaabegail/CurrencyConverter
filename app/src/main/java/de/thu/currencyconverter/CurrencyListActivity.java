package de.thu.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class CurrencyListActivity extends AppCompatActivity {

    private ArrayList<ExchangeRate> mCurrencyItem;
    private CurrencyItemAdapter currencyItemAdapter;
    ExchangeRateDatabase erd = new ExchangeRateDatabase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        initList();

        CurrencyItemAdapter adapter = new CurrencyItemAdapter(mCurrencyItem);
        Log.i("hello",Integer.toString(adapter.getCount()));
        ListView listView = (ListView)findViewById(R.id.currency_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ExchangeRate exRate = (ExchangeRate) parent.getItemAtPosition(position);
                String capitalName = exRate.getCapital();

                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+Uri.encode(capitalName));
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

    }

    public void initList(){
        mCurrencyItem = new ArrayList<>();
        String keySet;
        for(int i = 0; i < erd.getCurrencies().length; i++ ){
            keySet = erd.getCurrencies()[i];
            mCurrencyItem.add(new ExchangeRate(keySet, erd.getExchangeRate(keySet),erd.getCapital(keySet)));
        }
    }
}