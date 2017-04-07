package com.example.kamkeyboard.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.kamkeyboard.R;
import com.example.kamkeyboard.adapter.KeyBoardAdapter;
import com.example.kamkeyboard.entity.KeyBoardItem;
import com.example.kamkeyboard.util.StringUtils;
import com.example.kamkeyboard.util.SystemUtil;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Kam on 17/1/18.
 */

public class MyKeyBoardView extends LinearLayout implements View.OnClickListener {
    RecyclerView numKeyboardRecycleView;
    EditText editText;

    public boolean isNum = true;// 是否数字键盘
    public boolean isUpper = false;// 是否大写

    private KeyBoardAdapter numKeyboardAdapter;
    private KeyBoardAdapter abcKeyboardAdapter;
    ArrayList<KeyBoardItem> numList = new ArrayList<>();
    ArrayList<KeyBoardItem> abcList = new ArrayList<>();

    Context mContext;
    View view;
    Pattern mPattern;

    //小数点后的位数
    private static final int POINTER_LENGTH = 2;

    private static final String POINTER = ".";

    GridLayoutManager layoutManagerNum;
    GridLayoutManager layoutManagerABC;

    TextView mTvKbAbc;
    TextView mTvKb123;
    TextView mTvKbChina;

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_keyboard, this);
        initView();
    }

    public MyKeyBoardView(Context context) {
        super(context);
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    private void initView() {
        numKeyboardRecycleView = (RecyclerView) view.findViewById(R.id.rv_kb_num);
        mTvKbAbc = (TextView) view.findViewById(R.id.tv_kb_abc);
        mTvKb123 = (TextView) view.findViewById(R.id.tv_kb_123);
        mTvKbChina = (TextView) view.findViewById(R.id.tv_kb_china);
        mTvKbAbc.setOnClickListener(this);
        mTvKb123.setOnClickListener(this);
        mTvKbChina.setOnClickListener(this);
        initDatas();
        // 实例化一个GridLayoutManager，列数为3
        layoutManagerNum = new GridLayoutManager(mContext, 3);
        numKeyboardAdapter = new KeyBoardAdapter(mContext, numList, mOnItemClick, mOnItemLongClick);
        numKeyboardAdapter.setIsNum(true);

        layoutManagerABC = new GridLayoutManager(mContext, 366);
        abcKeyboardAdapter = new KeyBoardAdapter(mContext, abcList, mOnItemClick, mOnItemLongClick);
        abcKeyboardAdapter.setIsNum(false);
        abcKeyboardAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return abcList.get(position).getSpanSize();
            }
        });
        changeKeyboard();

    }

    private void changeKeyboard() {
        if (isNum) {
            numKeyboardRecycleView.setLayoutManager(layoutManagerNum);
            numKeyboardRecycleView.setAdapter(numKeyboardAdapter);
            mTvKbAbc.setSelected(false);
            mTvKb123.setSelected(true);
            mTvKbChina.setSelected(false);
        } else {
            numKeyboardRecycleView.setLayoutManager(layoutManagerABC);
            numKeyboardRecycleView.setAdapter(abcKeyboardAdapter);
            mTvKbAbc.setSelected(true);
            mTvKb123.setSelected(false);
            mTvKbChina.setSelected(false);
        }
    }

    private void initDatas() {
        mPattern = Pattern.compile("([0-9]|\\.)*");
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "7"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "8"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "9"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "4"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "5"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "6"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "1"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "2"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "3"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "."));
        numList.add(new KeyBoardItem(KeyBoardItem.NUMBER, KeyBoardItem.NUM_SPAN_SIZE, "0"));
        numList.add(new KeyBoardItem(KeyBoardItem.NUM_IMG_BACK, KeyBoardItem.NUM_SPAN_SIZE));

        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "q"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "w"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "e"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "r"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "t"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "y"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "u"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "i"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "o"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "p"));

        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_A, KeyBoardItem.IMG_SPAN_SIZE, "a"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "s"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "d"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "f"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "g"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "h"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "j"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "k"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "l"));

        abcList.add(new KeyBoardItem(KeyBoardItem.IMG_CAPITAL, KeyBoardItem.IMG_SPAN_SIZE));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "z"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "x"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "c"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "v"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "b"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "n"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "m"));
        abcList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_IMG_BACK, KeyBoardItem.IMG_SPAN_SIZE));
    }

    /**
     * This method is to add code to EditText
     */
    private void onClickNumber(View view, int position) {
        Editable editable;
        int start;
        if (editText == null) {
            if (!isNum) {// if it is a //alphabet keyboard
                if (abcList.get(position).getItemType() == KeyBoardItem.IMG_CAPITAL) {//change capital
                    inputUpperKeyBoard();
                }
            }
            return;
        }
        String destText = editText.getText().toString();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setSelection(editText.getText().toString().length());
        editable = editText.getText();
        start = editText.getSelectionStart();
//        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);//Testing for only number decimal allow

        if (view instanceof TextView) {
            TextView textView = ((TextView) view);
            String strChar = textView.getText().toString();
            switch (editText.getInputType()) {

                case InputType.TYPE_CLASS_NUMBER:
                    //the number is int
                    if (!StringUtils.isEmpty(editText.getText().toString()) || !strChar.equals("0")) {
                        //ignore number 0 when EditText is empty
                        editable.insert(start, strChar);
                    }
                    break;
                case InputType.TYPE_NUMBER_FLAG_DECIMAL:
                    //the number is decimal
                    if (isNum) {// if it is a num keyboard
                        if (numList.get(position).getItemType() == KeyBoardItem.NUM_IMG_BACK) {//back
                            inputDeleteKeyBoard(editable, start);
                        }
                    }
                    inputNumberDecimal(editable, editText, destText, strChar, start);
                    break;
                case InputType.TYPE_CLASS_TEXT:
                    editable.insert(start, strChar);
                    break;
                case InputType.TYPE_CLASS_PHONE:
                    editable.insert(start, strChar);
                    break;
                default:
                    editable.insert(start, strChar);
                    break;
            }

        } else if (view instanceof ImageView) {
            if (isNum) {// if it is a num keyboard
                if (numList.get(position).getItemType() == KeyBoardItem.NUM_IMG_BACK) {//back
                    inputDeleteKeyBoard(editable, start);
                } else {
                }
            } else {//alphabet keyboard
                if (abcList.get(position).getItemType() == KeyBoardItem.ALPHABET_IMG_BACK) {//back
                    inputDeleteKeyBoard(editable, start);
                } else if (abcList.get(position).getItemType() == KeyBoardItem.IMG_CAPITAL) {//change capital
                    inputUpperKeyBoard();
                }
            }
        }
    }

    private void inputDeleteKeyBoard(Editable editable, int start) {
        if (editable != null && editable.length() > 0 && start > 0) {
            editable.delete(start - 1, start);
        }
    }

    private void inputUpperKeyBoard() {
        isUpper = !isUpper;
        abcKeyboardAdapter.setUpper(isUpper);
        abcKeyboardAdapter.notifyDataSetChanged();
    }

    private void inputNumberDecimal(Editable editable, EditText editText, String destText, String strChar, int start) {
        Matcher matcher = mPattern.matcher(strChar);
        //已经输入小数点的情况下，只能输入数字
        if (destText.contains(POINTER)) {
            if (!matcher.matches()) {
                return;
            } else {
                if (POINTER.equals(strChar)) {  //只能输入一个小数点
                    return;
                }
            }
            if (editText.getText().toString().trim().substring(0).equals(".") && editText.getText().length() == 1) {
                //输入第一个数字为小数点的时候
                strChar = "0." + strChar;
                editText.setText(strChar);
                editText.setSelection(3);
                return;
            }
            //验证小数点精度，保证小数点后只能输入两位
            int index = destText.indexOf(POINTER);
            int length = destText.length() - index;

            if (length > POINTER_LENGTH) {
                return;
            } else {
                editable.insert(start, strChar);
            }
        } else {
            //没有输入小数点的情况下，只能输入小数点和数字，但首位不能输入小数点和0
            if (!matcher.matches()) {
                return;
            } else {
                editable.insert(start, strChar);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_kb_abc) {
            if (isNum) {
                isNum = false;
                changeKeyboard();
            }

        } else if (i == R.id.tv_kb_123) {
            if (!isNum) {
                isNum = true;
                changeKeyboard();
            }

        } else if (i == R.id.tv_kb_china) {
            if (editText != null) {
                SystemUtil.openKeyboard(editText, mContext);
            } else {
                SystemUtil.openKeyboard(null, mContext);
            }
//                mTvKbAbc.setSelected(false);
//                mTvKb123.setSelected(false);
//                mTvKbChina.setSelected(true);

        }
    }

    private KeyBoardAdapter.OnItemClick mOnItemClick = new KeyBoardAdapter.OnItemClick() {
        @Override
        public void onClick(View v, int position) {
            onClickNumber(v, position);
        }
    };
    private ScheduledExecutorService scheduledExecutor;

    private KeyBoardAdapter.OnItemLongClick mOnItemLongClick = new KeyBoardAdapter.OnItemLongClick() {
        @Override
        public void onLongClick(final View v, final int position, final boolean isTouch) {
            if (isTouch) {
                scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = position;
                        msg.obj = v;
                        handler.sendMessage(msg);
                    }
                }, 0, 50, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
            } else {
                if (scheduledExecutor != null) {
                    scheduledExecutor.shutdownNow();
                    scheduledExecutor = null;
                }
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            View view = (View) msg.obj;
            int position = msg.what;
            onClickNumber(view, position);
        }
    };
}
