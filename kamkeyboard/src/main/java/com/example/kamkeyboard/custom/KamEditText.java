package com.example.kamkeyboard.custom;

import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.kamkeyboard.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;

import static android.R.attr.paddingLeft;


/**
 * Created by Kam on 17/1/16.
 */
public class KamEditText extends EditText {

    public KamEditText(Context context) {
        this(context, null);
    }

    public KamEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KamEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SystemUtil.closeKeyboard(this);
        this.setSelection(this.getText().length());
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {//获取焦点时传递给键盘
            EventBus.getDefault().post(this);
        }
        setSelected(focused);
    }

    //
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestFocus();
        requestFocusFromTouch();
        float textSize = getTextSize() * getTextScaleX() / 2;
        int selection = (int) ((event.getX() - getPaddingLeft()) / (textSize));
        if (getText().length() > selection) {
            setSelection(selection);
        } else {
            setSelection(getText().length());
        }
        return true;
    }

}
