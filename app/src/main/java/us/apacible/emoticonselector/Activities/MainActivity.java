package us.apacible.emoticonselector.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import us.apacible.emoticonselector.Floater.Floater;
import us.apacible.emoticonselector.R;

/**
 * Main activity for the Emoticon Selector app.
 *
 * @author japacible
 */
public class MainActivity extends Activity {
    // Whether or not Floater is currently running.
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = false;
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.startBtn);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!isRunning) {
                startService(new Intent(MainActivity.this, Floater.class));
                isRunning = true;
            }
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopBtn);
        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isRunning) {
                stopService(new Intent(MainActivity.this, Floater.class));
                isRunning = false;
            }
            }
        });
    }
}
