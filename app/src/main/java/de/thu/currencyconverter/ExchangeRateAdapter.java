package de.thu.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ExchangeRateAdapter extends BaseAdapter {
    private ExchangeRateDatabase exchangeRateDatabase;

    public ExchangeRateAdapter(ExchangeRateDatabase exchangeRateDatabase){
        //super(context,0,arrayList);
        this.exchangeRateDatabase = exchangeRateDatabase;
    }

    /*public ExchangeRateAdapter(Context context, ArrayList<ExchangeRate> arrayList){
        super(context,0,arrayList);
    } */

    @Override
    public int getCount() {
        return exchangeRateDatabase.getCurrencies().length;
    }

    @Override
    public Object getItem(int position) {
        return exchangeRateDatabase.getCurrencies()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }



    private View initView(int position, View convertView, ViewGroup parent){
        Context context = parent.getContext();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.spinner_view_item, parent, false
            );
        }

        String currentItem = exchangeRateDatabase.getCurrencies()[position];

        ImageView image_view_flag = convertView.findViewById(R.id.image_view_flag);
        int imageId = context.getResources().getIdentifier("flag_" + currentItem.toLowerCase(),"drawable",context.getPackageName());


        TextView txt_curr_name = convertView.findViewById(R.id.txt_currency_name);
        TextView txt_rate_for_euro = convertView.findViewById(R.id.txt_rate_for_euro);

        if(currentItem != null){
            image_view_flag.setImageResource(imageId);
            txt_curr_name.setText(currentItem);
            txt_rate_for_euro.setText(Double.toString(exchangeRateDatabase.getExchangeRate(currentItem)));
        }

        return convertView;
    }
}
