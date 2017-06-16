package arprog.inc.ar.arprog;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelpActivity extends AppCompatActivity {

    private String readHelp() {
        InputStream inputStream = MainActivity.current.getResources().openRawResource(R.raw.help);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String result = "";
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                result += line + '\n';
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ((TextView) findViewById(R.id.help_text)).setText(readHelp());
    }
}
