/*
 * Copyright 2016, The Android Open Source Project
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

package com.titan.gyslfh.layercontrol;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import com.esri.arcgisruntime.mapping.LayerList;

/**
 * Contains {@link BindingAdapter}s for the {@link com.esri.arcgisruntime.layers.Layer} list.
 */
public class LayerListBindings {


    @SuppressWarnings("unchecked")
    @BindingAdapter({"items"})
    public static void setItems(RecyclerView recyclerView, LayerList items) {
        LayersAdapter adapter = (LayersAdapter) recyclerView.getAdapter();
        if (adapter != null)
        {

            adapter.replaceData(items);
        }
    }
}