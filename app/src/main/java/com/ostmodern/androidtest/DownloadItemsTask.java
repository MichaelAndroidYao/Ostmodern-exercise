/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ostmodern.androidtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronously loading set information from the Skylark API
 * <p/>
 * Class Description
 *
 * @author michaelakakpo
 * @version 18/10/15.
 */
class DownloadItemsTask extends AsyncTask<Void, Void, List<Item>> {

    private final String TAG = DownloadItemsTask.class.getSimpleName();
    private Activity mActivity;
    private ProgressDialog dialog;

    // Constructor to allow task to be called with fresh activity instance
    public DownloadItemsTask(Activity activity) {
        onAttach(activity);
    }

    // Attaching and the task to the parent activity
    public void onAttach(Activity activity) {
        this.mActivity = activity;
        this.dialog = new ProgressDialog(mActivity);
    }

    // Detaching the parent activity
    public void onDetach() {
        this.mActivity = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected List<Item> doInBackground(Void... urls) {
        Log.d(TAG, "doInBackground");

        /* These two need to be declared outside the try/catch
        so that they can be closed in the finally block. */

        // Http Connection
        HttpURLConnection connection = null;
        // Buffer for reading InputStream from response
        BufferedReader bufferedReader = null;
        // InputStream
        InputStream inputStream = null;
        // The JSON response from the server is stored as a raw JSON string.
        String jsonResponseString = null;

        // List of Artists after parsing the JSON response
        List<Item> listOfItems = new ArrayList<>();

        try {

            // BASE URL to append any further query params etc onto
            final String CAKES_BASE_URL = "http://feature-code-test.skylark-cms.qa.aws.ostmodern.co.uk:8000/api/episodes/";

            // Main Url the connection will be opened on
            URL cakesURL = new URL(CAKES_BASE_URL);

            Log.d("Cakes URL: ", cakesURL.toString());

            // Create the request and open the connection on the url.
            connection = (HttpURLConnection) cakesURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Check the stream of bytes from the JSON response is not empty
            inputStream = connection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream == null) {
                // No bytes to stream from the response
                jsonResponseString = null;
            }

            // Convert the stream of bytes to a stream of characters {@link InputStreamReader}
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }

            // Read the characters from the response and build a string out of it
            String line;
            if (bufferedReader != null) {
                while ((line = bufferedReader.readLine()) != null) {
                    // Build a string out of the contents of the BufferedReader
                    stringBuilder.append(line).append("\n");
                }
            }

            // Check the String that is being built from reading the string
            if (stringBuilder.length() == 0) {
                // If the stream was empty then no need to parse the JSON response.
                jsonResponseString = null;
            }
            // Store the response after successfully reading it
            jsonResponseString = stringBuilder.toString();

            Log.d(TAG, "Json response: " + jsonResponseString);
        } catch (IOException e) {
            Log.e(TAG, "Error closing stream: " + e.getMessage());
                /* If the connection didn't successfully retrieve the set data,
                there's no point in attempting to parse it. */
            jsonResponseString = null;
        } finally {
            // Ensure that regardless of outcome, the connection is disconnected
            if (connection != null) {
                connection.disconnect();
            }

            // Ensure that regardless of outcome, the InputStream is closed
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Ensure that regardless of outcome, the buffer is closed
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream {}" + e.getMessage());
                }
            }
        }
        // Attempt to parse Json response and extract required fields
        try {
            listOfItems = parseResponseData(jsonResponseString);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return listOfItems;
    }

    // Parsing the Json String and extracting the required item fields
    public List<Item> parseResponseData(String response) throws JSONException {

        // JSON objects that need to be extracted form the JSON response
        final String ITEM_UID = "uid";
        final String ITEM_TITLE = "title";
        final String ITEM_OBJECT = "objects";

        // List of items
        List<Item> listOfItems = new ArrayList<>();

        // response string is converted into an object so it can be traversed to extract individual objects
        JSONObject items = new JSONObject(response);

        JSONArray itemArray = items.getJSONArray(ITEM_OBJECT);

        // Iterate through array and pull relevant fields from each JSONObject
        for (int currentItem = 0; currentItem < itemArray.length(); currentItem++) {

            Item item = new Item();

            // retrieve relevant item fields
            JSONObject currentCakeItem = itemArray.getJSONObject(currentItem);

            String itemUID = currentCakeItem.getString(ITEM_UID);
            String itemTitle = currentCakeItem.getString(ITEM_TITLE);

            // store item info
            item.setUid(itemUID);
            item.setTitle(itemTitle);

            Log.i("Item UID: ", itemUID);
            Log.i("Item Title: ", itemTitle);

            // create list of items
            listOfItems.add(item);

        }
        Log.d("Items: ", "# of items " + listOfItems.size());
        return listOfItems;
    }

    // onPostExecute displays the results of the AsyncTask loading the items.
    @Override
    protected void onPostExecute(List<Item> result) {
        Log.d(TAG, "onPostUpdate()");
        if (mActivity != null) {
            // If the activity is null don't bother updating, else update UI
            ((MainActivity) mActivity).updateItemsList(result);
        }
        // If the dialog is still showing after loading then dismiss
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


}
