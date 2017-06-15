package arprog.inc.ar.arprog;

import android.content.Intent;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import arprog.inc.ar.opengl.RenderData;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        handleLoading();
    }

    private View mContentView;
    private float currentAlpha = 0;
    private void handleLoading() {
        MainActivity.current = this;
        mContentView = findViewById(R.id.loading_view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                currentAlpha = 0;
                Process.setThreadPriority(-19);

                long startTime = System.currentTimeMillis();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContentView.setAlpha(currentAlpha);
                    }
                });

                System.out.println("loading");
                while (currentAlpha < 1) {
                    float deltaTime = (float) (System.currentTimeMillis() - startTime);
                    currentAlpha = deltaTime / 2000f;
                    System.out.print(".");
                    Thread.yield();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mContentView.setAlpha(currentAlpha);
                        }
                    });
                }


                while (!isLoaded) {
                    Thread.yield();
                }

                startTime = System.currentTimeMillis();
                while (currentAlpha > 0f) {
                    float deltaTime = (float) (System.currentTimeMillis() - startTime);
                    currentAlpha = 1 - deltaTime / 500f;
                    System.out.print(".");
                    Thread.yield();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mContentView.setAlpha(currentAlpha);
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.current, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        }).start();

        startLoadingThread();
    }

    public boolean isLoaded = false;

    private void startLoadingThread() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                MainActivity.prepareOpenCV();
                RenderData.prepareAllModels();
                isLoaded = true;
            }
        });

        if (!isLoaded)
            thread.start();
    }
}
