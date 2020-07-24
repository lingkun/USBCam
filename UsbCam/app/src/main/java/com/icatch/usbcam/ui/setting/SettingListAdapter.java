package com.icatch.usbcam.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icatch.usbcam.R;
import com.icatch.usbcam.engine.setting.SettingItem;
import com.icatchtek.basecomponent.widget.HorizSelectView;
import com.icatchtek.baseutil.log.AppLog;

import java.util.LinkedList;

/**
 * @author b.jiang
 * @date 2018/10/29
 * @description
 */
public class SettingListAdapter extends BaseAdapter {
    private static final String TAG=SettingListAdapter.class.getSimpleName();
    private Context context;
    private LinkedList<SettingItem> menuList;
    private LinkedList<SettingItem> changedList;
    private LinkedList<SettingItem> beforeList;

    public SettingListAdapter(Context context, LinkedList<SettingItem> menuList) {
        this.context = context;
        this.menuList = menuList;
        beforeList = new LinkedList<>();
        deepCopy();
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    private void deepCopy(){
        if(menuList == null || menuList.size() == 0){
            return;
        }
        beforeList = new LinkedList<>();
        for (SettingItem temp: menuList){
            SettingItem item = new SettingItem(temp.getTitleResId(),temp.getPropertyId(),temp.getCurValue());
            beforeList.add(item);
        }
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final SettingItem settingItem = menuList.get(position);
        SettingItem.SettingType settingType = settingItem.getType();
        if(settingType == SettingItem.SettingType.SETTING_COMMON){
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_menu_item_common, parent,false);
            holder = new ViewHolder();
            holder.title =  convertView.findViewById(R.id.item_title);
            holder.selectView = convertView.findViewById(R.id.item_select_view);
            holder.title.setText(settingItem.getTitleResId());
            holder.selectView.setValueListString(settingItem.getValueListString());
            holder.selectView.setIndex(settingItem.getIndex());
            holder.selectView.setOnIndexChangeListener(new HorizSelectView.OnIndexChangeListener() {
                @Override
                public void onIndexChange(int index) {
                    AppLog.d(TAG,"onIndexChange index:" + index);
                    settingItem.setIndex(index);
                    addChangedItem(settingItem);
                }
            });

        }else if (settingType == SettingItem.SettingType.SETTING_ACTION){
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_menu_item_action, parent,false);
            holder = new ViewHolder();
            holder.title =  convertView.findViewById(R.id.item_title);
            holder.title.setText(settingItem.getTitleResId());
        }
        else if (settingType == SettingItem.SettingType.SETTING_INFO){
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_menu_item_info, parent,false);
            holder = new ViewHolder();
            holder.title =  convertView.findViewById(R.id.item_title);
            holder.text = convertView.findViewById(R.id.item_value);
            holder.title.setText(settingItem.getTitleResId());
            holder.text.setText(settingItem.getCurValue());
        }

        convertView.setTag(holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView title;
        public TextView text;
        public HorizSelectView selectView;
    }

    private void addChangedItem(SettingItem settingItem){
        if(changedList == null){
            changedList = new LinkedList<>();
        }
        int index = isExist(settingItem);
        if(index < 0){
            changedList.add(settingItem);
        }else {
            changedList.set(index,settingItem);
        }
        AppLog.d(TAG,"addChangedItem changedList:" + changedList);
    }

    private int isExist(SettingItem settingItem){
        int index = -1;
        if(changedList == null || changedList.size() <= 0){
            return index;
        }
        for(int ii = 0 ;ii < changedList.size() ;ii++){
            if(changedList.get(ii).getPropertyId() == settingItem.getPropertyId()){
                index = ii;
                break;
            }
        }
        return index;
    }

    private boolean isChanged(SettingItem settingItem){
        if(beforeList == null || beforeList.size() <=0){
            return true;
        }
        for(SettingItem temp : beforeList){
            if(temp.getPropertyId() == settingItem.getPropertyId() && temp.getCurValue() != settingItem.getCurValue()){
                AppLog.d(TAG,"PropertyId:" + settingItem.getPropertyId() + " isChanged:true");
                return  true;
            }
        }
        return false;
    }

    public LinkedList<SettingItem> getChangedList() {
        AppLog.d(TAG,"getChangedList changedList:" + changedList);
        if(changedList == null || changedList.size() == 0){
            return changedList;
        }
        LinkedList<SettingItem> tempList = new LinkedList<>();
        for(SettingItem temp :changedList ){
            if(isChanged(temp)){
                AppLog.d(TAG,"temp:" + temp);
                tempList.add(temp);
            }
        }
        AppLog.d(TAG,"getChangedList tempList:" + tempList);
        return tempList;
    }
}

