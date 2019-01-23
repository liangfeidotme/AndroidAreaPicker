package me.liangfei.areapicker.old;


import androidx.annotation.Keep;
import android.text.TextUtils;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof AreaModel) {
            AreaModel other = (AreaModel) obj;
            return id == other.id || TextUtils.equals(name, other.name);
        }
        return false;
    }
}
