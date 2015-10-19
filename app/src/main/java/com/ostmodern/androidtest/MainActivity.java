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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;


/**
 * Main activity to host the fragment containing the list of items
 *
 * @author michaelakakpo
 * @version 18/10/15.
 */
public class MainActivity extends AppCompatActivity {

    private PlaceholderFragment mPlaceholderFragment;
    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * @inheritDoc
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        if (savedInstanceState == null) {
            if (mPlaceholderFragment == null) {
                // Activity starting first time
                mPlaceholderFragment = PlaceholderFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, mPlaceholderFragment, "placeholder_fragment")
                        .commit();
            } else {
                Log.d(TAG, "Fragment retained");
                mPlaceholderFragment = (PlaceholderFragment) getSupportFragmentManager()
                        .findFragmentByTag("placeholder_fragment");
            }
        }

        // TODO: TwoPane mode could be used to display 2 fragments.
        // Fragment A (on the left) showing episode episode (Set) list and Fragment B on the right showing the item details
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlaceholderFragment != null) {
            // Remove reference to fragment upon activity being destroyed
            mPlaceholderFragment = null;
        }
    }

    // Update the list of the items from the activity

    public void updateItemsList(List<Item> result) {
        // Check to make sure the components are not null (set by onDetach)
        if (mPlaceholderFragment != null) {
            if (mPlaceholderFragment.mDownloadItemsTask != null) {
                if (mPlaceholderFragment.mListItemAdapter != null) {
                    // update the adapter
                    Log.d("updating from activity", "# adapter items" + result.size());
                    mPlaceholderFragment.mListItemAdapter.addItemsToList(result);
                }
            }
        }
    }
}
