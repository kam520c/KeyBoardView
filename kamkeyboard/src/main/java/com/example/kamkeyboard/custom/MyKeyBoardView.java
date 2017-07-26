package com.example.kamkeyboard.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
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

import static com.example.kamkeyboard.R.id.tv_kb_abc;


/**
 * Created by Kam on 17/1/18.
 */

public class MyKeyBoardView extends LinearLayout implements View.OnClickListener {
    RecyclerView keyboardRecycleView;
    EditText editText;

    public boolean isNum = true;// 是否数字键盘,show numKeyboard in the begining
    public boolean isABC = false;// 是否字母键盘
    public boolean isChar = false;// 是否字符键盘
    public boolean isUpper = false;// 是否大写
    public boolean isShowNumKeyboard = false;
    public boolean isShowAbcKeyboard = false;
    public boolean isShowCharKeyboard = false;
    public boolean isShowSystemKeyboard = false;
    public boolean isKeyboardTitleGone = false;

    private KeyBoardAdapter numKeyboardAdapter;
    private KeyBoardAdapter abcKeyboardAdapter;
    private KeyBoardAdapter charKeyboardAdapter;
    ArrayList<KeyBoardItem> numList = new ArrayList<>();
    ArrayList<KeyBoardItem> abcList = new ArrayList<>();
    ArrayList<KeyBoardItem> charList = new ArrayList<>();

    Context mContext;
    View view;
    Pattern mPattern;
    public onKeyClickListener mOnKeyListener;

    //小数点后的位数
    private static final int POINTER_LENGTH = 2;

    private static final String POINTER = ".";

    GridLayoutManager layoutManagerNum;
    GridLayoutManager layoutManagerABC;
    GridLayoutManager layoutManagerChar;

    TextView mTvKbAbc;
    TextView mTvKb123;
    TextView mTvKbChar;
    TextView mTvKbChina;

    LinearLayout linTitle;

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_keyboard, this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyKeyBoardView);
        if (array.getBoolean(R.styleable.MyKeyBoardView_isNumKeyboardShow, false)) {
            isShowNumKeyboard = true;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_isAbcKeyboardShow, false)) {
            isShowAbcKeyboard = true;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_isCharKeyboardShow, false)) {
            isShowCharKeyboard = true;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_isSystemKeyboardShow, false)) {
            isShowSystemKeyboard = true;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_isKeyboardTitleGone, false)) {
            isKeyboardTitleGone = true;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_initAbcKeyboard, false)) {
            isNum = false;
            isABC = true;
            isChar = false;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_initNumKeyboard, false)) {
            isNum = true;
            isABC = false;
            isChar = false;
        }
        if (array.getBoolean(R.styleable.MyKeyBoardView_initCharKeyboard, false)) {
            isNum = false;
            isABC = false;
            isChar = true;
        }
        initView();
    }

    public MyKeyBoardView(Context context) {
        super(context);
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public EditText getEditText() {
        return editText;
    }

    private void initView() {
        keyboardRecycleView = (RecyclerView) view.findViewById(R.id.rv_kb_num);
        linTitle = (LinearLayout) view.findViewById(R.id.linTitle);
        mTvKbAbc = (TextView) view.findViewById(tv_kb_abc);
        mTvKb123 = (TextView) view.findViewById(R.id.tv_kb_123);
        mTvKbChar = (TextView) view.findViewById(R.id.tv_kb_char);
        mTvKbChina = (TextView) view.findViewById(R.id.tv_kb_china);
        mTvKbAbc.setOnClickListener(this);
        mTvKb123.setOnClickListener(this);
        mTvKbChar.setOnClickListener(this);
        mTvKbChina.setOnClickListener(this);
        initDatas();
        // 实例化一个GridLayoutManager，列数为3
        layoutManagerNum = new GridLayoutManager(mContext, 3);
        numKeyboardAdapter = new KeyBoardAdapter(mContext, numList, mOnItemClick, mOnItemLongClick, mOnKeyClick);
        numKeyboardAdapter.setNumSelected();
        layoutManagerABC = new GridLayoutManager(mContext, 366);
        abcKeyboardAdapter = new KeyBoardAdapter(mContext, abcList, mOnItemClick, mOnItemLongClick, mOnKeyClick);
        abcKeyboardAdapter.setABCSelected();
        abcKeyboardAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return abcList.get(position).getSpanSize();
            }
        });
        layoutManagerChar = new GridLayoutManager(mContext, 366);
        charKeyboardAdapter = new KeyBoardAdapter(mContext, charList, mOnItemClick, mOnItemLongClick, mOnKeyClick);
        charKeyboardAdapter.setCharSelected();
        charKeyboardAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return charList.get(position).getSpanSize();
            }
        });
        if (isShowAbcKeyboard) {
            mTvKbAbc.setVisibility(View.VISIBLE);
        } else {
            mTvKbAbc.setVisibility(View.GONE);
        }
        if (isShowNumKeyboard) {
            mTvKb123.setVisibility(View.VISIBLE);
        } else {
            mTvKb123.setVisibility(View.GONE);
        }
        if (isShowCharKeyboard) {
            mTvKbChar.setVisibility(View.VISIBLE);
        } else {
            mTvKbChar.setVisibility(View.GONE);
        }
        if (isShowSystemKeyboard) {
            mTvKbChina.setVisibility(View.VISIBLE);
        } else {
            mTvKbChina.setVisibility(View.GONE);
        }
        if ((!isShowAbcKeyboard && !isShowNumKeyboard && !isShowCharKeyboard && !isShowSystemKeyboard) || isKeyboardTitleGone) {
            linTitle.setVisibility(View.GONE);
        }
        changeKeyboard();
    }

    private void changeKeyboard() {
        if (isNum) {
            keyboardRecycleView.setLayoutManager(layoutManagerNum);
            keyboardRecycleView.setAdapter(numKeyboardAdapter);
            mTvKbAbc.setSelected(false);
            mTvKb123.setSelected(true);
            mTvKbChar.setSelected(false);
            mTvKbChina.setSelected(false);
        } else if (isABC) {
            keyboardRecycleView.setLayoutManager(layoutManagerABC);
            keyboardRecycleView.setAdapter(abcKeyboardAdapter);
            mTvKbAbc.setSelected(true);
            mTvKb123.setSelected(false);
            mTvKbChar.setSelected(false);
            mTvKbChina.setSelected(false);
        } else if (isChar) {
            keyboardRecycleView.setLayoutManager(layoutManagerChar);
            keyboardRecycleView.setAdapter(charKeyboardAdapter);
            mTvKbAbc.setSelected(false);
            mTvKb123.setSelected(false);
            mTvKbChar.setSelected(true);
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

        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "!"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "@"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "#"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "$"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "%"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "^"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "&"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "*"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "("));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ")"));

        charList.add(new KeyBoardItem(KeyBoardItem.CAHR, KeyBoardItem.IMG_SPAN_SIZE, "["));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "]"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "{"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "}"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "+"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "-"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "="));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "/"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "\\"));

        charList.add(new KeyBoardItem(KeyBoardItem.CAHR, KeyBoardItem.IMG_SPAN_SIZE, "<"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ">"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "<<"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ">>"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "+"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "~"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "`"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "_"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "¥"));

        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_BIG, KeyBoardItem.IMG_SPAN_SIZE, "."));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ","));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "、"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "?"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "'"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, "\""));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ":"));
        charList.add(new KeyBoardItem(KeyBoardItem.CAHR_NORMAL, KeyBoardItem.ALPHABET_NORMAL_SPAN_SIZE, ";"));
        charList.add(new KeyBoardItem(KeyBoardItem.ALPHABET_IMG_BACK, KeyBoardItem.IMG_SPAN_SIZE));
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
        destText = destText.substring(0, editText.getSelectionStart());
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
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
                case (InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER):
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
            } else if (isABC) {//alphabet keyboard
                if (abcList.get(position).getItemType() == KeyBoardItem.ALPHABET_IMG_BACK) {//back
                    inputDeleteKeyBoard(editable, start);
                } else if (abcList.get(position).getItemType() == KeyBoardItem.IMG_CAPITAL) {//change capital
                    inputUpperKeyBoard();
                }
            } else if (isChar) {//char keyboard
                if (charList.get(position).getItemType() == KeyBoardItem.ALPHABET_IMG_BACK) {//back
                    inputDeleteKeyBoard(editable, start);
                }
            }
        }
    }

    private String getItemClickKey(View view) {
        if (view instanceof TextView) {
            return ((TextView) view).getText().toString();
        } else {
            return null;
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
        if (i == tv_kb_abc) {
            if (!isABC) {
                isNum = false;
                isABC = true;
                isChar = false;
                changeKeyboard();
            }

        } else if (i == R.id.tv_kb_123) {
            if (!isNum) {
                isNum = true;
                isABC = false;
                isChar = false;
                changeKeyboard();
            }
        } else if (i == R.id.tv_kb_char) {
            if (!isChar) {
                isNum = false;
                isABC = false;
                isChar = true;
                changeKeyboard();
            }

        } else if (i == R.id.tv_kb_china) {
            if (editText != null) {
                SystemUtil.openKeyboard(editText, mContext);
            } else {
                SystemUtil.openKeyboard(null, mContext);
            }
        }
    }

    private KeyBoardAdapter.onItemClick mOnItemClick = new KeyBoardAdapter.onItemClick() {
        @Override
        public void onClick(View v, int position) {
            onClickNumber(v, position);
        }
    };

    public KeyBoardAdapter.onKeyClick mOnKeyClick = new KeyBoardAdapter.onKeyClick() {
        @Override
        public void onKeyClick(View v) {
            if (mOnKeyListener != null) {
                mOnKeyListener.onKeyClick(getItemClickKey(v));
            }
        }
    };

    //获取key内容接口
    public interface onKeyClickListener {
        void onKeyClick(String str);
    }

    public void setOnKeyClickListener(onKeyClickListener listener) {
        mOnKeyListener = listener;
    }

    private ScheduledExecutorService scheduledExecutor;

    private KeyBoardAdapter.onItemLongClick mOnItemLongClick = new KeyBoardAdapter.onItemLongClick() {
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
