package com.test.courier;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 27775 on 2019/12/13.
 */

public class ClearEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    //  删除按钮
    private Drawable mClearDrawable;
    //  是否获得焦点
    private boolean mHasFoucus = false;
    public ClearEditText(Context context) {
        super(context,null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs,android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //  获取drawableRight,如果没有就使用默认的
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.mipmap.cuo);
            //  ????? 注释了也没有影响
//      mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
            //  默认设置不可见
            setClearIconVisiable(false);
            //  设置焦点监听
            setOnFocusChangeListener(this);
            //  设置文字监听
            addTextChangedListener(this);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            boolean flag = event.getX() > (getWidth() - getTotalPaddingRight()) && event.getX() < (getWidth() - getPaddingRight())
                    ? true : false;
            if (flag) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    public void setClearIconVisiable(boolean b) {
        Drawable right = b ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mHasFoucus = hasFocus;
        if (hasFocus) {
            setClearIconVisiable(getText().length() > 0);
        } else {
            setClearIconVisiable(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mHasFoucus) {
            setClearIconVisiable(getText().length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
