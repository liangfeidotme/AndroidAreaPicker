package com.liangfeizc.areapicker.sample;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.liangfeizc.areapicker.sample.pojo.Response;
import com.liangfeizc.areapicker.sample.pojo.ResponseData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String readAssetsFile(Context context, String fileName) {
        BufferedReader in = null;
        try {
            StringBuilder sb = new StringBuilder();
            InputStream is = context.getAssets().open(fileName);
            in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static List<Area> getMainAreas(Context context) {
        String provinces = readAssetsFile(context, "provinces.json");
        Response response = JSON.parseObject(provinces, Response.class);
        return convert(response.data.branches);
    }

    public static List<Area> getSubAreasById(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            return getMainAreas(context);
        }
        String cities = readAssetsFile(context, "cities.json");
        Response response = JSON.parseObject(cities, Response.class);
        return convert(response.data.branches);
    }

    private static List<Area> convert(List<ResponseData.Branch> branches) {
        List<Area> areas = new ArrayList<>();
        for (ResponseData.Branch branch : branches) {
            Area area = new Area();
            area.id = branch.query;
            area.name = branch.text;
            areas.add(area);
        }
        return areas;
    }

    public static String[] extractNames(List<Area> areas) {
        String[] names = new String[areas.size()];
        for (int i = 0, size = areas.size(); i < size; i++) {
            names[i] = areas.get(i).name;
        }
        return names;
    }
}
