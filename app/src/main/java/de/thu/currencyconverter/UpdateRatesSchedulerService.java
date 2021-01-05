package de.thu.currencyconverter;

import android.app.job.JobParameters;
import android.app.job.JobService;
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
    MainActivity mainActivity;

    private    int c = 0;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        //we are responsible to make own background thread
        doBackgroundWork(params);
        return true;
    }


    private void doBackgroundWork(final JobParameters params) {
       // new Thread((Runnable) new ExchangeRateUpdateRunnable(mainActivity.getActivity())).start();
        //jobFinished(params, false);

     //test
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c++;
                    Thread.sleep(1000);
                    Log.d("Execute:",Integer.toString(c));
                    jobFinished(params, false);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // this is called when job is cancelled
        //e.g you only want to run the process when wife is connected
        //suddenly disconnected, process will be cancelled
        Log.d(TAG, "Job cancelled before completion");

        //boolean indicates if we want to reschedule our job or not
        jobCancelled = true;
        return true;


    }


}
