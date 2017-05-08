package com.liangfeizc.areapicker.zui;


import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class AreaModel {
    @SerializedName("id")
    public long id;

    @SerializedName("name")
    public String name;

    @SerializedName("subList")
    public List<AreaModel> subAreas;

    public transient boolean isSelected;
}
