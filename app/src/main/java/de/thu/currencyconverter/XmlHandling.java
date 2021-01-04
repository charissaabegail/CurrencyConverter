package de.thu.currencyconverter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class XmlHandling {
    private String currency;
    private Double rate;
    private String urlString = null;
    private XmlPullParserFactory xmlFactorObject;
    public volatile boolean parsingComplete = true;

    public XmlHandling(String urlString){
        this.urlString = urlString;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getRate() {
        return rate;
    }

    public String getUrlString() {
        return urlString;
    }

    public void parseXMLAndStore(XmlPullParser parser){
        int event;
        String text = null;
        try {
            event = parser.getEventType();
            while(event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                    case XmlPullParser.END_TAG:
                        if(name.equals("cube")){
                            currency = parser.getAttributeValue(0);
                            rate = Double.parseDouble(parser.getAttributeValue(1));
                        }
                        break;
                }
                event = parser.next();
            }
            parsingComplete=false;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setReadTimeout(10000);
                    connect.setConnectTimeout(15000);
                    connect.setRequestMethod("GET");
                    connect.setDoInput(true);
                    connect.connect();

                    InputStream stream = connect.getInputStream();
                    xmlFactorObject = XmlPullParserFactory.newInstance();
                    XmlPullParser lParser = xmlFactorObject.newPullParser();
                    lParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                    lParser.setInput(stream, null);
                    parseXMLAndStore(lParser);
                    stream.close();

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
