package de.thu.currencyconverter;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateRatesSchedulerService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;

    private    int c = 0;

    TimerAsyncTask timerAsyncTask = new TimerAsyncTask(this);



    @Override
    public boolean onStartJob(JobParameters params) {
        //Log.d(TAG, "Job started");
        //we are responsible to make own background thread
        //  doBackgroundWork(params);
        timerAsyncTask.execute(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
             /*  for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);
                    if (jobCancelled) {
                        return;
                    } */

                  /*  try {
                        Thread.sleep(60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } */
                //  }

                //  while(true) {

                try {
                    c++;
                    Thread.sleep(60*1000);
                    Log.d("Execute:",Integer.toString(c));
                    jobFinished(params, false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //  }
                //  Log.d(TAG, "execute something in the background");

            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // this is called when job is cancelled
        //e.g you only want to run the process when wife is connected
        //suddenly disconnected, process will be cancelled
        Log.d(TAG, "Job cancelled before completion");

        return true;


    }


    private static class TimerAsyncTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private Context mContext;

        //tell when finish executing
        private final JobService jobService;
        private int c = 0;

        //from runnable class
        private boolean updating = false;

        private ExchangeRateAdapter exchangeRateAdapter;
        private MainActivity mainActivity;

        private Spinner spinnerFrom;
        private Spinner spinnerTo;
        ExchangeRateDatabase erd = new ExchangeRateDatabase();




        private Notifier notifier;

        private int noOfItemsUpdated;

        //((MainActivity) getActivity()).startChronometer();





        public TimerAsyncTask(JobService jobService) {
            this.jobService = jobService;

        }

        @Override
        protected JobParameters doInBackground(JobParameters... jobParameters) {
            mainActivity = MainActivity.getInstance();
            notifier = new Notifier(mainActivity);
            //---------------------------------------------------
           /* for (int i = 0; i < 10; i++) {
                Log.d(TAG, "run: " + i);

                try {
                    c++;
                    Thread.sleep(1000);
                    Log.d("Execute:",Integer.toString(c));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } */

            //---------------------------------------------------
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
                noOfItemsUpdated = 0;
                while(eventType != XmlPullParser.END_DOCUMENT){

                    // Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
                    if(eventType == XmlPullParser.START_TAG) {

                        if("cube".equalsIgnoreCase(parser.getName()) == true && parser.getAttributeCount() == 2){
                            if(parser.getAttributeValue(null, "currency") != null){
                                String currencyName = parser.getAttributeValue(null, "currency");
                                String rateForOneEuro = parser.getAttributeValue(null,"rate");

                                mainActivity.getDatabase().setExchangeRate(currencyName, Double.parseDouble(rateForOneEuro));
                                //erd.setExchangeRate(currencyName, Double.parseDouble(rateForOneEuro));
                                noOfItemsUpdated++;
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

               /*  this.mainActivity = activity;
        notifier = new Notifier(activity); */

            notifier.removeNotification();
            //a toast can only be started from the UI thread. This means that new Runnable needs to be created
            //new Runnable is passed to the UI thread for execution
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifier.showOrUpdateNotification(noOfItemsUpdated);
                    Toast.makeText(mainActivity.getApplicationContext(),
                            "Rates successfully updated!", Toast.LENGTH_LONG).show();
                    mainActivity.getAdapter().notifyDataSetChanged();
                    // notifier.showOrUpdateNotification();
                }
            });

           /* Log.d("CountDownIntentService","Start Operation");

            for(int i=0; i<= 100; i+=10){
                try{
                    Log.d("CountDownIntentService", i + " % completed");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } */
            Log.d("CountdownIntentService","Ended Operation");
            return jobParameters[0];
        }

        //this method runs on ui thread after finishing background operations (doInBackground)
        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters,false);
            super.onPostExecute(jobParameters);
        }
    }
}
