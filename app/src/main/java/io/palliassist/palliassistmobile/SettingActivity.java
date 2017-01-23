package io.palliassist.palliassistmobile;

import android.os.Bundle;

import io.palliassist.palliassistmobile.BaseActivity;
import io.palliassist.palliassistmobile.R;

/**
 * Created by Steven on 12/5/2016.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        setTitle("Settings");
    }

}
