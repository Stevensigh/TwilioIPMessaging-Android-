package io.palliassist.palliassistmobile.twilio.chat.token;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.palliassist.palliassistmobile.twilio.application.TwilioApplication;

public class TokenRequest {
  private static TokenRequest mInstance;
  private RequestQueue mRequestQueue;

  private TokenRequest() {
    mRequestQueue = Volley.newRequestQueue(TwilioApplication.get().getApplicationContext());
  }

  public static synchronized TokenRequest getInstance() {
    if (mInstance == null) {
      mInstance = new TokenRequest();
    }
    return mInstance;
  }

  public <T> void addToRequestQueue(Request<T> req) {
    mRequestQueue.add(req);
  }
}
