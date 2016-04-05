package com.example.yuriy.simpleclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by yuriy on 05.04.16.
 */
public class MyPreferenceActivity extends PreferenceActivity {

    public static final String IPCONFIG = "ipconfig";
    public static final String TCPCONFIG = "tcpconfig";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userprefs);
    }

}
