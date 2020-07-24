package com.icatchtek.basecomponent.prompt;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.icatchtek.basecomponent.R;


/**
 * Created by zhang yanhu C001012 on 2015/12/9 09:26.
 */
public class MyToast {
    private static  Toast toast;

    public static void  show(Context context,String message)
    {

        Context mcontext = context.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate( R.layout.my_toast, null);
        TextView textView = (TextView) view.findViewById( R.id.message_text);
        if(toast != null){
            toast.cancel();
        }
        toast = new Toast(mcontext);
        textView.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void show(Context context,int stringId)
    {
        Context mcontext = context.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate( R.layout.my_toast, null);
        TextView textView = (TextView) view.findViewById( R.id.message_text);
        if(toast != null){
            toast.cancel();
        }
        toast = new Toast(mcontext);
        textView.setText(stringId);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void show(Context context,int stringId, int duration)
    {
        Context mcontext = context.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate( R.layout.my_toast, null);
        TextView textView = (TextView) view.findViewById( R.id.message_text);
        if(toast != null){
            toast.cancel();
        }
        toast = new Toast(mcontext);
        textView.setText(stringId);
        toast.setDuration(duration);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}