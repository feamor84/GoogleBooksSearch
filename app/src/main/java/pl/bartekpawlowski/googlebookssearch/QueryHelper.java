package pl.bartekpawlowski.googlebookssearch;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper class to handle http requests and json result parse
 */

public final class QueryHelper {

    private final static String CLASS_IDENTIFIER = "QueryHelper";

    /**
     * private constructor to avoid call a new QueryHelper object
     */
    private QueryHelper() {
    }

    private static String urlBuilder(String userInput) {
        StringBuffer url = new StringBuffer();
        String userInputEncoded = "";

        try {
            userInputEncoded = URLEncoder.encode(userInput.trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(CLASS_IDENTIFIER, "Unsupported encoding exception", e);
        }

        url.append("https://www.googleapis.com/books/v1/volumes?q=");
        url.append(userInputEncoded);
        url.append("&maxResults=20");

        return url.toString();
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(CLASS_IDENTIFIER, "Error preparing URL", e);
        }

        return url;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuffer output = new StringBuffer();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    private static String getAuthors(JSONArray authorsArray, Context context) {
        StringBuffer authors = new StringBuffer();

        if(authorsArray == null) {
            return context.getResources().getString(R.string.no_author_provided);
        }

        try {
            if(authorsArray.length() == 1) {
                authors.append(authorsArray.getString(0));
            } else {
                for (int i = 0; i < authorsArray.length(); i++) {
                    authors.append(authorsArray.getString(i));
                    authors.append(", ");
                }
                authors.deleteCharAt(authors.lastIndexOf(","));
            }
        } catch (JSONException e) {
            Log.e(CLASS_IDENTIFIER, "Error parsing authors array", e);
        }

        return authors.toString().trim();
    }

    private static String makeHttpRequest(String urlString) throws IOException {
        URL url = createUrl(urlBuilder(urlString));
        String jsonResponse = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        if (url == null) {
            return jsonResponse;
        }

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.setReadTimeout(1500);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else {
                Log.e(CLASS_IDENTIFIER, "Error making HTTP request with code: " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(CLASS_IDENTIFIER, "Error during HTTP connection", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        Log.i(CLASS_IDENTIFIER, jsonResponse);
        return jsonResponse;
    }

    public static ArrayList<GoogleBook> extractGoogleBookString(String urlString, Context context) {
        ArrayList<GoogleBook> googleBookArrayList = new ArrayList<GoogleBook>();

        if (urlString.isEmpty()) {
            return googleBookArrayList;
        }

        try {
            String json = makeHttpRequest(urlString);

            JSONObject root = new JSONObject(json);
            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.optJSONArray("authors");

                GoogleBook tmp = new GoogleBook(
                        volumeInfo.getString("title"),
                        getAuthors(authors, context)
                );

                googleBookArrayList.add(tmp);
            }

        } catch (IOException e) {
            Log.e(CLASS_IDENTIFIER, "Error during JSON parsing", e);
        } catch (JSONException e) {
            Log.e(CLASS_IDENTIFIER, "Error during JSON parsing", e);
        }

        return googleBookArrayList;
    }
}
