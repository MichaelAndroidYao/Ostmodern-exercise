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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the row for each item in the list
 *
 * @author michaelakakpo
 * @version 1/10/15.
 */
class ListItemAdapter extends ArrayAdapter {

    private final Context mContext;
    // Initial lit of Items
    private List<Item> listOfItems = new ArrayList<>();

    public ListItemAdapter(Context context, List objects) {
        super(context, R.layout.set_list_item, objects);
        this.mContext = context;
        this.listOfItems = objects;
    }

    /**
     * @inheritDoc
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item currentItem = getItem(position);
        ItemViewHolder itemViewHolder;

        // represents the row being inflated and recycled
        View rowItem = convertView;

        // First time instantiating rows of a article so needs to be inflated
        if (rowItem == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowItem = inflater.inflate(R.layout.set_list_item, parent, false);
            itemViewHolder = new ItemViewHolder(rowItem);

            rowItem.setTag(itemViewHolder);
        } else {
            //recycling the viewholder once the convertview is no longer null
            itemViewHolder = (ItemViewHolder) rowItem.getTag();
        }
        itemViewHolder.getTitle().setText(currentItem.getTitle());

        return rowItem;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getCount() {
        return listOfItems.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Item getItem(int position) {
        return listOfItems.get(position);
    }

    /**
     * Add the items from the updated items to the list and notify any observers and refresh the changes
     *
     * @param restoredItems - The list of items the adapter is to be updated with
     */
    public void addItemsToList(List<Item> restoredItems) {
        if (restoredItems != null) {
            listOfItems.clear();
            listOfItems.addAll(restoredItems);
            notifyDataSetChanged();
        }
    }
}
