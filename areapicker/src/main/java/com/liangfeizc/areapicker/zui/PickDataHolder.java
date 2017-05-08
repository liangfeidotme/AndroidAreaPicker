package com.liangfeizc.areapicker.zui;


public class PickDataHolder {
    public AreaModel initialArea;
    public AreaModel[] selectedAreas;
    private String[] tabTitles;

    private static PickDataHolder instance;

    public PickDataHolder instance() {
        return instance;
    }

    public PickDataHolder(int tabCount, AreaModel initialArea, String[] tabTitles) {
        selectedAreas = new AreaModel[tabCount];
        this.tabTitles = tabTitles;
        instance = this;
    }

    public void select(int position, AreaModel areaModel) {
        selectedAreas[position] = areaModel;
    }

    public AreaModel[] selectedAreas() {
        return selectedAreas;
    }

    public static void release() {
        instance = null;
    }
}
