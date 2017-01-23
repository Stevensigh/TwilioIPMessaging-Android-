package io.palliassist.palliassistmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.palliassist.palliassistmobile.twilio.ui.ChannelActivity;
import io.palliassist.palliassistmobile.twilio.ui.Logger;
import io.palliassist.palliassistmobile.twilio.util.BasicIPMessagingClient;
import io.palliassist.palliassistmobile.twilio.util.HttpHelper;
import io.palliassist.palliassistmobile.twilio.util.ILoginListener;

import static android.widget.Toast.LENGTH_LONG;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ILoginListener {
    private static final Logger logger = Logger.getLogger(LoginActivity.class);
    private static String DEFAULT_CLIENT_NAME = "test";
    public static String local_user = DEFAULT_CLIENT_NAME;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    //Twilio related
    private static final String auth_script = "https://aa32f544.ngrok.io/token";
    private BasicIPMessagingClient chatClient;
    public String capabilityToken = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        //Set up Twilio access
        }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        getLoginData();
    }

    private void getLoginData() {

        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://hcbredcap.com.br/api/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                validateLogin(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", "F2C5AEE8A2594B0A9E442EE91C56CC7A");
                params.put("content", "record");
                params.put("format", "json");
                params.put("type", "flat");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        rq.add(sr);
    }

    private void validateLogin(String response) {

        Log.wtf("LOGIN", response);
        Boolean success = false;
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                String user = jsonobject.getString("username");
                DEFAULT_CLIENT_NAME= user;
                String pswd = jsonobject.getString("password");
                if ((user.equals(mEmailView.getText().toString())) && (pswd.equals(mPasswordView.getText().toString()))) {
                    success = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (success) {
            StringBuilder url = new StringBuilder();
            url.append(auth_script);
            url.append(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
            new GetCapabilityTokenAsyncTask().execute(url.toString());
            Log.d("Twilio","Completed Twilio Access");
            final Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText((Context) this, "Incorrect login credentials. Please try again.", LENGTH_LONG).show();
            mEmailView.requestFocus();
            mEmailView.setText("");
            mPasswordView.setText("");
        }
    }

    private class GetCapabilityTokenAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            LoginActivity.this.chatClient.doLogin(LoginActivity.this, capabilityToken);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginActivity.this.progressDialog = ProgressDialog.show(LoginActivity.this, "",
                    "Connecting to #general channel", true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                capabilityToken = HttpHelper.httpGet(params[0]);
                logger.e("capabilityToken string : " + capabilityToken);
                chatClient.setCapabilityToken(capabilityToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return capabilityToken;
        }
    }
    @Override
    public void onLoginStarted() {
        logger.d("Log in started");
    }

    @Override
    public void onLoginFinished() {
        //LoginActivity.this.progressDialog.dismiss();
        logger.d("Log in completed");
    }

    @Override
    public void onLoginError(String errorMessage) {
        LoginActivity.this.progressDialog.dismiss();
        logger.e("Error logging in : " + errorMessage);
        Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutFinished() {
        // TODO Auto-generated method stub

    }
}

