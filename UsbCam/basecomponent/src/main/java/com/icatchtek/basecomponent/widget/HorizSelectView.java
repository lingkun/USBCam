package com.icatchtek.basecomponent.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icatchtek.basecomponent.R;

/**
 * @author b.jiang
 * @date 2018/10/29
 * @description
 */
public class HorizSelectView extends LinearLayout implements View.OnClickListener{
    private TextView vuleTxv;
    private ImageView icPlus;
    private ImageView icMinus;
    private String[] valueListString;
    private int index;
    private OnIndexChangeListener listener;
    public HorizSelectView(Context context) {
        super(context);
    }

    public HorizSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // 把布局和当前类形成整体
        LayoutInflater.from(context).inflate(R.layout.horiz_select_layout, this);
        icPlus =  findViewById(R.id.ic_plus);
        icPlus.setOnClickListener(this);
        icMinus = findViewById(R.id.ic_minus);
        icMinus.setOnClickListener(this);
        vuleTxv = findViewById(R.id.value_txv);
    }

    public void setValueListString(String[] valueListString) {
        this.valueListString = valueListString;
    }

    public void setIndex(int index) {
        this.index = index;
        if(vuleTxv != null) {
            vuleTxv.setText(valueListString[index]);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ic_plus) {
            if (index + 1 == valueListString.length) {
                index = 0;
            } else {
                index ++;
            }

        }else if(id== R.id.ic_minus){
            if (index == 0) {
                index = valueListString.length- 1;
            } else {
                index --;
            }
        }
        vuleTxv.setText(valueListString[index]);
        if(listener != null){
            listener.onIndexChange(index);
        }
    }


    public int getIndex() {
        return index;
    }

    public interface OnIndexChangeListener{
        void onIndexChange(int index);
    }

    public void setOnIndexChangeListener(@Nullable OnIndexChangeListener listener) {
        this.listener  = listener;
    }
}
