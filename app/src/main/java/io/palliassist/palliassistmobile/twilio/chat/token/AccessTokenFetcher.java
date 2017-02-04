package io.palliassist.palliassistmobile.twilio.chat.token;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import io.palliassist.palliassistmobile.R;

import io.palliassist.palliassistmobile.twilio.application.SessionManager;
import io.palliassist.palliassistmobile.twilio.application.TwilioApplication;
import io.palliassist.palliassistmobile.twilio.chat.listener.TaskCompletionListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenFetcher {

    private Context context;

    public AccessTokenFetcher(Context context) {
        this.context = context;
    }

    public void fetch(final TaskCompletionListener<String, String> listener) {
        JSONObject obj = new JSONObject(getTokenRequestParams(context));
        //String requestUrl = getStringResource(R.string.token_url);
        String requestUrl = "https://palliassist-dev-us.azurewebsites.net/token?identity=steven";
        JsonObjectRequest jsonObjReq =
                new JsonObjectRequest(Request.Method.POST, requestUrl, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String token = null;
                        try {
                            token = response.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError("Failed to parse token JSON response");
                        }
                        listener.onSuccess(token);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        listener.onError("Failed to fetch token");
                    }
                });
        jsonObjReq.setShouldCache(false);
        TokenRequest.getInstance().addToRequestQueue(jsonObjReq);
    }

    private Map<String, String> getTokenRequestParams(Context context) {
        String androidId =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, String> params = new HashMap<>();
        params.put("deviceId", androidId);
        params.put("identity", SessionManager.getInstance().getUsername());
        return params;
    }

    private String getStringResource(int id) {
        Resources resources = TwilioApplication.get().getResources();
        return resources.getString(id);
    }

}
