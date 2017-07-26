package com.example.kamkeyboard.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.kamkeyboard.R;
import com.example.kamkeyboard.entity.KeyBoardItem;
import com.example.kamkeyboard.util.StringUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Kam on 17/1/18.
 */

public class KeyBoardAdapter extends BaseMultiItemQuickAdapter<KeyBoardItem, BaseViewHolder> {
    public boolean mIsUpper = false;// 是否大写
    public boolean mIsNum = false;// 是否数字
    public boolean mIsABC = false;// 是否字母键盘
    public boolean mIsChar = false;// 是否字符键盘
    public onItemClick mOnItemClick;
    public onItemLongClick mOnItemLongClick;
    public onKeyClick mOnKeyClick;
    private ScheduledExecutorService scheduledExecutor;
    private long lastDownTime;
    private long thisEventTime;
    private long longPressTime = 500;
    boolean isLongClickModule = false;
    boolean isLongClicking = false;

    public void setUpper(boolean upper) {
        mIsUpper = upper;
    }

    public void setNumSelected() {
        mIsNum = true;
        mIsABC = false;
        mIsChar = false;
    }

    public void setABCSelected() {
        mIsABC = true;
        mIsNum = false;
        mIsChar = false;
    }

    public void setCharSelected() {
        mIsChar = true;
        mIsABC = false;
        mIsNum = false;
    }

    public KeyBoardAdapter(Context context, List data, onItemClick onItemClick, onItemLongClick onItemLongClick, onKeyClick onKeyClick) {
        super(data);
        this.mOnItemClick = onItemClick;
        this.mOnItemLongClick = onItemLongClick;
        this.mOnKeyClick = onKeyClick;
        addItemType(KeyBoardItem.NUMBER, R.layout.item_keyboard_num);
        addItemType(KeyBoardItem.CAHR, R.layout.item_keyboard_alphabet_a);
        addItemType(KeyBoardItem.CAHR_BIG, R.layout.item_keyboard_char_big);
        addItemType(KeyBoardItem.CAHR_NORMAL, R.layout.item_keyboard_alphabet_normal);
        addItemType(KeyBoardItem.ALPHABET_NORMAL, R.layout.item_keyboard_alphabet_normal);
        addItemType(KeyBoardItem.ALPHABET_A, R.layout.item_keyboard_alphabet_a);
        addItemType(KeyBoardItem.IMG_CAPITAL, R.layout.item_keyboard_img_alphabet);
        addItemType(KeyBoardItem.NUM_IMG_BACK, R.layout.item_keyboard_img_num);
        addItemType(KeyBoardItem.ALPHABET_IMG_BACK, R.layout.item_keyboard_img_alphabet);
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, KeyBoardItem item) {
        switch (baseViewHolder.getItemViewType()) {
            case KeyBoardItem.NUM_IMG_BACK:
                baseViewHolder.setImageResource(R.id.tv_kb, R.drawable.iv_common_kb_delete_big);
                onDeleteClick(baseViewHolder, baseViewHolder.getView(R.id.tv_kb), baseViewHolder.getLayoutPosition() - getHeaderLayoutCount());

                break;
            case KeyBoardItem.ALPHABET_IMG_BACK:
                baseViewHolder.setImageResource(R.id.tv_kb, R.drawable.iv_common_kb_delete_small);
                onDeleteClick(baseViewHolder, baseViewHolder.getView(R.id.tv_kb), baseViewHolder.getLayoutPosition() - getHeaderLayoutCount());

                break;
            case KeyBoardItem.IMG_CAPITAL:
                if (!mIsUpper) {// 小写
                    baseViewHolder.setImageResource(R.id.tv_kb, R.drawable.iv_commom_kb_lowercase);
                    baseViewHolder.setBackgroundRes(R.id.tv_kb, R.drawable.selector_keyboard_key_alphabet);
                } else {
                    baseViewHolder.setImageResource(R.id.tv_kb, R.drawable.iv_commom_kb_capital);
                    baseViewHolder.setBackgroundRes(R.id.tv_kb, R.drawable.shape_keyboard_alphabet_selected);
                }
                break;
            case KeyBoardItem.NUMBER:
                baseViewHolder.setText(R.id.tv_kb, item.getContent());
                break;
            case KeyBoardItem.CAHR_NORMAL:
                baseViewHolder.setText(R.id.tv_kb, item.getContent());
                break;
            case KeyBoardItem.CAHR:
                baseViewHolder.setText(R.id.tv_kb, item.getContent());
                break;
            case KeyBoardItem.CAHR_BIG:
                baseViewHolder.setText(R.id.tv_kb, item.getContent());
                break;
            default:
                if (!mIsUpper) {// 小写
                    if (!StringUtils.isEmpty(item.getContent()) && isWord(item.getContent())) {
                        baseViewHolder.setText(R.id.tv_kb, item.getContent().toLowerCase());
                    }
                } else {// 大写
                    if (!StringUtils.isEmpty(item.getContent()) && isWord(item.getContent())) {
                        baseViewHolder.setText(R.id.tv_kb, item.getContent().toUpperCase());
                    }
                }
                break;
        }
        onClick(baseViewHolder);

    }

    private boolean isWord(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str.toLowerCase());
    }

    private void onClick(final BaseViewHolder baseViewHolder) {
        baseViewHolder.getView(R.id.tv_kb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onClick(v, baseViewHolder.getLayoutPosition() - getHeaderLayoutCount());
                }
                if (mOnKeyClick != null) {
                    mOnKeyClick.onKeyClick(v);
                }
            }
        });
    }

    private Handler handler = new Handler();

    private void onDeleteClick(final BaseViewHolder baseViewHolder, View view, final int position) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                thisEventTime = event.getEventTime();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按下时触发发送消息
                        lastDownTime = event.getDownTime();
                        if (mOnItemClick != null) {
                            mOnItemClick.onClick(view, baseViewHolder.getLayoutPosition() - getHeaderLayoutCount());
                        }
                        baseViewHolder.setBackgroundRes(view.getId(), R.color.greySelectedText);
                        // 长按操作，静止不动时的计时
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isLongClicking = true;
                                if (mOnItemLongClick != null) {
                                    mOnItemLongClick.onLongClick(view, position, true);
                                }
                            }
                        }, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //手指抬起时停止发送
                        handler.removeCallbacksAndMessages(null);
                        if (isLongClickModule) {
                            isLongClickModule = false;
                            isLongClicking = false;
                        }
                        baseViewHolder.setBackgroundRes(view.getId(), R.color.white);
                        if (mOnItemLongClick != null) {
                            mOnItemLongClick.onLongClick(view, position, false);
                        }
                        break;
                    default:
                        if (!isLongClickModule) {
                            isLongClickModule = isLongClick();
                        }
                        if (isLongClickModule && !isLongClicking) {
                            //长按事件,不停发送消息
                            handler.removeCallbacksAndMessages(null);//取消静止不动时的计时
                            isLongClicking = true;
                            if (mOnItemLongClick != null) {
                                mOnItemLongClick.onLongClick(view, position, true);
                            }
                        }

                        break;
                }
                return true;
            }
        });
    }

    private boolean isLongClick() {
        long intervalTime = thisEventTime - lastDownTime;
        if (intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

    public interface onItemClick {
        void onClick(View v, int position);
    }

    public interface onItemLongClick {
        void onLongClick(View v, int position, boolean isTouch);
    }

    public interface onKeyClick {
        void onKeyClick(View v);
    }
}
