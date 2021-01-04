package de.thu.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ExchangeRateAdapter extends ArrayAdapter<ExchangeRate> {

    public ExchangeRateAdapter(Context context, ArrayList<ExchangeRate> arrayList){
        super(context,0,arrayList);
    }

    /*public ExchangeRateAdapter(Context context, ArrayList<ExchangeRate> arrayList){
        super(context,0,arrayList);
    } */

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

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_view_item, parent, false
            );
        }

        ImageView image_view_flag = convertView.findViewById(R.id.image_view_flag);
        TextView txt_curr_name = convertView.findViewById(R.id.txt_currency_name);
        TextView txt_rate_for_euro = convertView.findViewById(R.id.txt_rate_for_euro);

        ExchangeRate currentItem = getItem(position);

        if(currentItem != null){
            image_view_flag.setImageResource(currentItem.getFlagImage());
            txt_curr_name.setText(currentItem.getCurrencyName());
            txt_rate_for_euro.setText(Double.toString(currentItem.getRateForOneEuro()));
        }

        return convertView;
    }
}
