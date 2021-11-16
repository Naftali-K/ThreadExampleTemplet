package com.nk.threadexampletemplet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyTest";

    Handler handler = new Handler(); //android.os.Handler
    TextView counter;
    private volatile boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = findViewById(R.id.counter);

        findViewById(R.id.start_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = false;
//                exampleRunWithoutThread(10); //will crash app

//                ExampleThread exampleThread = new ExampleThread(10);
//                exampleThread.start(); // will work normally. using Thread

                ExampleRunnable exampleRunnable = new ExampleRunnable(10);
//                exampleRunnable.run(); // run in main thread
                new Thread(exampleRunnable).start(); // will work normally. still must put in the Thread.

                // anonymous thread run - not worked
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 10; i++){
//                            try {
//                                Thread.sleep(1000);
//                                Log.d(TAG, "Second:" + i);
//                                counter.setText("Sec:" + i);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });

        findViewById(R.id.stop_thread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = true;
            }
        });
    }




    //option 1 without Thread. Will crash app, because UI frozen for more then 10sec
    private void exampleRunWithoutThread(int seconds){
        for (int i = 0; i < seconds; i++){
            try {
                Thread.sleep(1000);
                Log.d(TAG, "Second:" + i);
                counter.setText("Sec: " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //option 2, where class is extend from Thread. Working well, no problem
    class ExampleThread extends Thread {
        int seconds;
        int counterInt;

        public ExampleThread(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++){
                counterInt = i;
                try {
                    Thread.sleep(1000);
                    Log.d(TAG, "Second:" + i);
//                    counter.setText("Sec: " + i); // gonna crash app and give error: Only the original thread that created a view hierarchy can touch its views.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            counter.setText("Sec: " + counterInt);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //option 3, where class have implements from Runnable, is interface of Thread. will work well, no problem
    class ExampleRunnable implements Runnable {

        int seconds;
        int counterInt;

        public ExampleRunnable(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++){
                if (stopThread) {
                    return;
                }
                counterInt = i;
                try {
                    Thread.sleep(1000);
                    Log.d(TAG, "Second:" + i);

//                    counter.setText("Sec: " + i); // gonna crash app and give error: Only the original thread that created a view hierarchy can touch its views.

                    // taking handle from outside
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            counter.setText("Sec: " + counterInt);
//                        }
//                    });

                    // this option, for get inside of loop/thread and handler
//                    Handler threadHandler = new Handler(Looper.getMainLooper());
//                    threadHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            counter.setText("Sec: " + counterInt);
//                        }
//                    });

                    counter.post(new Runnable() {
                        @Override
                        public void run() {
                            counter.setText("Sec: " + counterInt);
                        }
                    });

                    // working on activity. this option must have activity.
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            counter.setText("Sec: " + counterInt);
//                        }
//                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}