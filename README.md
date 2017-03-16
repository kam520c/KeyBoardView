KeyBoardView
==================
自定义键盘
 ![image](https://github.com/kam520c/KeyBoardView/raw/master/screenshots/num.png)

 ![image](https://github.com/kam520c/KeyBoardView/raw/master/screenshots/abc.png)

Gradle example
=======

```java
allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
   }
   dependencies {
        compile 'com.github.kam520c:KeyBoardView:1.0'
   }
```

Sample （更多用法详见示例项目）
=======

```java
public class MainActivity extends AppCompatActivity {
    MyKeyBoardView keyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyboardView = (MyKeyBoardView) findViewById(R.id.keyboardView);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEditText(final KamEditText myEditText) {
        keyboardView.setEditText(myEditText);
    }

}
```

xml中：
```java
        <com.example.kamkeyboard.custom.MyKeyBoardView
            android:id="@+id/keyboardView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
```
edittext:
```java
        <com.example.kamkeyboard.custom.KamEditText
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@null"/>
```