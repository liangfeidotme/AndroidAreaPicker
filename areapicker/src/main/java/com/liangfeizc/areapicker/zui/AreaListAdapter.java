package com.liangfeizc.areapicker.zui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangfeizc.areapicker.R;

import java.util.List;

public class AreaListAdapter extends ArrayAdapter<AreaModel> {
    private LayoutInflater inflater;

    public AreaListAdapter(@NonNull Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    public void setSelected(int position) {
        for (int i = 0, size = getCount(); i < size; i++) {
            getItem(i).isSelected = false;
        }

        getItem(position).isSelected = true;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_area, null, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        AreaModel areaModel = getItem(position);
        vh.name.setText(areaModel.name);
        vh.name.setTextColor(ContextCompat.getColor(getContext(),
                areaModel.isSelected ? R.color.tab_theme_color : R.color.tab_selected_text_color));
        vh.checked.setVisibility(areaModel.isSelected ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private static final class ViewHolder {
        private TextView name;
        private ImageView checked;

        ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.area_name);
            checked = (ImageView) itemView.findViewById(R.id.check_image);
        }
    }
}
