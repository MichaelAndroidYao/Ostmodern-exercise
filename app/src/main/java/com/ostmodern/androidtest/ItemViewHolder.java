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

import android.view.View;
import android.widget.TextView;

/**
 * ViewHolder to represent row of articles being recycled
 */
public class ItemViewHolder {

    private final TextView txtTitle;

    ItemViewHolder(View view) {
        // Use the View holder so we only call findViewById once.
        txtTitle = (TextView) view.findViewById(R.id.txt_item_title);
    }

    public TextView getTitle() {
        return txtTitle;
    }

}