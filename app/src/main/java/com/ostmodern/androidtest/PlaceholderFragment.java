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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment is responsible for loading
 * and displaying the root set list i.e. episodes.
 *
 * @author michaelakakpo
 * @version 18/10/15.
 */
public class PlaceholderFragment extends Fragment {

    private final static String TAG = PlaceholderFragment.class.getSimpleName();

    // Current activity
    MainActivity mCurrentActivity;

    // AsyncTask for downloading sets
    DownloadItemsTask mDownloadItemsTask;

    // Needs to update the adapter to display sets
    ListItemAdapter mListItemAdapter;

    // Network connectivity message
    private TextView mTextViewNetworkMessage;

    private TextView mTextViewEmptyListView;

    ListView mListView;

    private final List<Item> listOfItems = new ArrayList<>();

    public PlaceholderFragment() {
    /* No args constructor */
    }

    public static PlaceholderFragment newInstance() {
        return new PlaceholderFragment();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
        this.mCurrentActivity = (MainActivity) context;
        // Check if the AsyncTask has an attached activity, if not then attach an activity instance
        if (mDownloadItemsTask != null) {
            mDownloadItemsTask.onAttach(mCurrentActivity);
        }
    }

    /**
     * Initiates a request for the list of items
     */
    public void beginTask() {
        mDownloadItemsTask = new DownloadItemsTask(mCurrentActivity);
        mDownloadItemsTask.execute();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Avoid creating and destroying Fragment every time configuration changes
        setRetainInstance(true);
    }

    /**
     * @inheritDoc
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTextViewNetworkMessage = (TextView) rootView.findViewById(R.id.txt_network_connection_status);
        mTextViewEmptyListView = (TextView) rootView.findViewById(R.id.txt_exmpty_list_status);

        mListView = (ListView) rootView.findViewById(R.id.list);

        // Initialise and set the adapter
        mListItemAdapter = new ListItemAdapter(getContext(), listOfItems);
        mListView.setAdapter(mListItemAdapter);

        return rootView;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        mListView.setEmptyView(mTextViewEmptyListView);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored");
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Checks if there is network connection present and then attempt to load items if there is
     */
    private void loadData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Asynchronously load the sets (not blocking the main thread)
            beginTask();
        } else {
            // let user know the connection is not available
            mTextViewNetworkMessage.setVisibility(View.VISIBLE);
            mTextViewNetworkMessage.setText(R.string.network_no_connection_message);
            Log.d(TAG, "No network connection available()");
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
        if (mDownloadItemsTask != null) {
            // Notify the AsyncTask the calling activity is now not available
            mDownloadItemsTask.onDetach();
        }
    }
}



