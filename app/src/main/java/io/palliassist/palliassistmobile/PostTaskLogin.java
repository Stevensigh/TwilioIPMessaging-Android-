package io.palliassist.palliassistmobile;


import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aakashshah on 12/5/16.
 */
public class PostTaskLogin extends AsyncTask<String, String, Boolean> {

    Context context;
    String url_string;
    String token;

    public PostTaskLogin(Context c) {
        this.context = c;
        this.url_string = "https://hcbredcap.com.br/api/";
        this.token = "F2C5AEE8A2594B0A9E442EE91C56CC7A";
    }

    @Override
    /**
     * args are strings of username and password
     */
    protected Boolean doInBackground(String... args) {


    return true;

    }
}
