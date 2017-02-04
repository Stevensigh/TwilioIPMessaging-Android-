package io.palliassist.palliassistmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.palliassist.palliassistmobile.twilio.MainChatActivity;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button button_message;
    private Button button_notify;
    private Button button_esas;
    private Button button_pain;
    private Button button_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");


        button_message = (Button) findViewById(R.id.button_message);
        button_message.setOnClickListener(this);

        button_notify = (Button) findViewById(R.id.button_notify);
        button_notify.setOnClickListener(this);

        button_esas = (Button) findViewById(R.id.button_esas);
        button_esas.setOnClickListener(this);

        button_pain=(Button) findViewById(R.id.button_pain);
        button_pain.setOnClickListener(this);

        button_settings = (Button) findViewById(R.id.button_setting);
        button_settings.setOnClickListener(this);
    }


    public boolean onContextItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
        return true;
    }

    /**
     * Switch case determining the different options on the menu
     * @param item
     * @return
     */


    /**
     * button listener that will do something if specific button is clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_message:
                final Intent i = new Intent(this, MainChatActivity.class);
                startActivity(i);
                finish();

            case R.id.button_notify:
            /* TODO*/
                return;

            case R.id.button_esas:
                final Intent k = new Intent(this, EsasActivity.class);
                startActivity(k);
                finish();
                return;
            case R.id.button_pain:
                final Intent m = new Intent(this, PainLocator.class);
                startActivity(m);
                finish();
                return;


            case R.id.button_setting:
            /* TODO*/
                final Intent n = new Intent(this, SettingActivity.class);
                startActivity(n);
                finish();
                return;
        }
    }


}
