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

package com.titan.gyslfh.alarminfo;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link com.titan.gyslfh.alarminfo.AlarmInfoModel.AlarmInfo} list.
 */
public class AlarmInfosListBindings {


    @SuppressWarnings("unchecked")
    @BindingAdapter({"items"})
    public static void setItems(RecyclerView recyclerView, List<AlarmInfoModel.AlarmInfo> items) {
        AlarmInfoFragment.RVAdapter_listobject adapter = (AlarmInfoFragment.RVAdapter_listobject) recyclerView.getAdapter();
        if (adapter != null)
        {

            adapter.replaceData(items);
        }
    }
}
