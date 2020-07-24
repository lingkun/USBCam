package com.icatch.usbcam.engine.setting;

/**
 * @author b.jiang
 * @date 2018/10/26
 * @description
 */
public class SettingItem {
    private int propertyId;
    private String[] valueListString;
    private String curValue;

    public void setIndex(int index) {
        this.index = index;
        curValue = valueListString[index];
    }

    private int index;

    public SettingItem(int titleResId,int propertyId) {
        this.titleResId = titleResId;
        this.propertyId = propertyId;
        this.type = SettingType.SETTING_ACTION;
    }

    private int titleResId;

    public SettingItem(int titleResId,int propertyId, String curValue) {
        this.titleResId = titleResId;
        this.propertyId = propertyId;
        this.curValue = curValue;
        this.type = SettingType.SETTING_INFO;
    }

    private SettingType type;

    public SettingType getType() {
        return type;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String[] getValueListString() {
        return valueListString;
    }

    public String getCurValue() {
        return curValue;
    }

    public int getIndex() {
        return index;
    }

    public int getTitleResId() {
        return titleResId;
    }



    public SettingItem(int titleResId,int propertyId, String[] valueListString, int index ) {
        if(index < 0){
            index = 0;
        }
        this.propertyId = propertyId;
        this.valueListString = valueListString;
        this.index = index;
        this.titleResId = titleResId;
        if(valueListString.length >0 ){
            this.curValue = valueListString[index];
        }
        this.type = SettingType.SETTING_COMMON;
    }

    public enum SettingType{
        SETTING_INFO,
        SETTING_ACTION,
        SETTING_COMMON
    }

    @Override
    public String toString() {
        return "propertyId:" + propertyId + " curValue:" + curValue+ " index:" + index;
    }
}
