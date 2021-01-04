package de.thu.currencyconverter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CurrencyItemAdapter extends BaseAdapter {

    public List<ExchangeRate> data;
    final ExchangeRateDatabase exchangeRateDatabase = new ExchangeRateDatabase();
    //final CurrencyItemAdapter adapter = new CurrencyItemAdapter(erd);

    //Constructor
    public CurrencyItemAdapter(List<ExchangeRate> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        ExchangeRate entry = data.get(position);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.currency_list_item, null, false);
        }

        String currentItem = exchangeRateDatabase.getCurrencies()[position];

        ImageView image_view_flag = convertView.findViewById(R.id.image_view_flag);
        int imageId = context.getResources().getIdentifier("flag_"+currentItem.toLowerCase(),"drawable",context.getPackageName());

        TextView textView = (TextView)convertView.findViewById(R.id.txt_currency_item);



        if(currentItem != null){
            image_view_flag.setImageResource(imageId);
            textView.setText(currentItem);
        }


        return convertView;


    }


}
