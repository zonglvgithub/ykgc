package shoplistdownload.example.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    TimeChooseView timeChooseView;
    My_ScrollView scrollView;
    List<TimeMode> timeList = new ArrayList<>();
    int textSpacing = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeChooseView = (TimeChooseView) findViewById(R.id.time_choose);
        scrollView = (My_ScrollView) findViewById(R.id.scrollView);

        for (int i = 2; i < 23; i++) {
            TimeMode timeMode = new TimeMode();
            timeMode.time = i + "时";
            timeMode.isSelected = false;
            TimeMode timeMode1 = new TimeMode();
            timeMode1.time = "";
            timeMode1.isSelected = false;

            timeList.add(timeMode);
            timeList.add(timeMode1);
        }
        timeChooseView.setTime(timeList);
        timeChooseView.setTextSpacing(textSpacing);
        timeChooseView.setPosition(3);
        List<Integer> list = new ArrayList<>();

        list.add(5);
        list.add(7);
        list.add(8);
        list.add(10);


        timeChooseView.setPositionList(list);
        timeChooseView.setSelectedTime(new TimeChooseView.SelectedTime() {
            @Override
            public void getSelectedTime(String startTimeStr, String endTimeStr) {
                Log.e("TimeChooseView", startTimeStr + "    ===   " + endTimeStr);
            }
        });

        scrollView.setScrollViewListener(new My_ScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
                timeChooseView.setParamInt1(paramInt1);
            }
        });

        timeChooseView.setWidthHeight(ScreenUtil.dip2px(this, timeList.size() * textSpacing + 20), ScreenUtil.dip2px(this,100));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示

    }
}
