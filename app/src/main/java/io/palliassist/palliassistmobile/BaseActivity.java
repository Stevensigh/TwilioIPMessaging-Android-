package io.palliassist.palliassistmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.palliassist.palliassistmobile.twilio.ui.MessageActivity;

/**
 * Created by Steven on 12/4/2016.
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Chat option(located in res/menu/main_menu.xml)

            case R.id.context_return:
                final Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.context_message:
                final Intent j = new Intent(this, MessageActivity.class);
                startActivity(j);
                finish();
                break;

                //Other 3 context menus need to be completed
            case R.id.context_notify:
                final Intent k = new Intent(this, EsasActivity.class);
                startActivity(k);
                finish();
                break;

            case R.id.context_setting:
                final Intent m = new Intent(this, SettingActivity.class);
                startActivity(m);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
