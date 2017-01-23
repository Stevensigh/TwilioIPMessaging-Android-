package io.palliassist.palliassistmobile.twilio.util;

/**
 * Created by User on 11/13/2016.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static String stringFromInputStream(InputStream is) throws IOException
    {
        char[] buf = new char[1024];
        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader(is, "UTF-8");

        int bin;
        while ((bin = in.read(buf, 0, buf.length)) >= 0) {
            out.append(buf, 0, bin);
        }

        return out.toString();
    }

    public static String httpGet(String url) throws Exception {

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

        conn.setConnectTimeout(45000);
        conn.setReadTimeout(30000);
        conn.setDoInput(true);

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            String capabilityToken = stringFromInputStream(is);
            is.close();
            conn.disconnect();
            return capabilityToken;
        } else {
            conn.disconnect();
            throw new Exception("Error code from server: " + responseCode);
        }
    }
}
