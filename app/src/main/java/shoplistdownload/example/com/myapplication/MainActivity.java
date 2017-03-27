package shoplistdownload.example.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener,TimeChooseView.TimeChooseMoveIntreface {
    TimeChooseView timeChooseView;
    My_ScrollView scrollView;
    List<TimeMode> timeList = new ArrayList<>();
    int textSpacing = 60;


    private TextView tv_add_btn;
    private TextView tv_remove_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeChooseView = (TimeChooseView) findViewById(R.id.time_choose);
        scrollView = (My_ScrollView) findViewById(R.id.scrollView);
        tv_add_btn = (TextView) findViewById(R.id.tv_add_btn);
        tv_remove_btn = (TextView) findViewById(R.id.tv_remove_btn);


        tv_add_btn.setOnClickListener(this);
        tv_remove_btn.setOnClickListener(this);

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
        timeChooseView.setTimeChooseMoveIntreface( this );
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

        timeChooseView.setWidthHeight(ScreenUtil.dip2px(this, timeList.size() * textSpacing + 20), ScreenUtil.dip2px(this, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示

    }

    private Toast toast;

    @Override
    public void onClick(View view) {

        if(toast !=null) toast.cancel();

        switch (view.getId()) {
            case R.id.tv_add_btn:
                boolean addSuccess = timeChooseView.addPick();

                if(addSuccess){
                    toast = Toast.makeText( MainActivity.this, "添加成功",Toast.LENGTH_SHORT);
                }else{

                    toast = Toast.makeText( MainActivity.this, "添加失败",Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.tv_remove_btn:
                boolean removeSuccess = timeChooseView.removePick();
                if(removeSuccess){
                    toast = Toast.makeText( MainActivity.this, "删除成功",Toast.LENGTH_SHORT);
                }else{
                    toast = Toast.makeText( MainActivity.this, "删除失败",Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void timeChooseMove(boolean direction, float distance) {
        if( direction ){
            scrollView.scrollMoveTo(0 ,distance);
//            scrollView.scrollBy((int)distance,,0);
        }else{
            scrollView.scrollMoveTo(0,-distance);
//            scrollView.scrollBy((int)-distance,0);
        }
    }
}
