package shoplistdownload.example.com.myapplication.modoule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shoplistdownload.example.com.myapplication.My_ScrollView;
import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.ScreenUtil;
import shoplistdownload.example.com.myapplication.TimeChooseView;
import shoplistdownload.example.com.myapplication.TimeMode;
import shoplistdownload.example.com.myapplication.modoule.recycle.activity.RecycleListActivity;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.RylListAdapter;


public class MainActivity extends Activity implements View.OnClickListener,TimeChooseView.TimeChooseMoveIntreface {
    TimeChooseView timeChooseView;
    My_ScrollView scrollView;
    List<TimeMode> timeList = new ArrayList<>();
    int textSpacing = 60;


    private TextView tv_add_btn;
    private TextView tv_remove_btn;
    private TextView tv_listActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeChooseView = (TimeChooseView) findViewById(R.id.time_choose);
        scrollView = (My_ScrollView) findViewById(R.id.scrollView);
        tv_add_btn = (TextView) findViewById(R.id.tv_add_btn);
        tv_remove_btn = (TextView) findViewById(R.id.tv_remove_btn);
        tv_listActivity = (TextView) findViewById(R.id.tv_listActivity);


        tv_add_btn.setOnClickListener(this);
        tv_remove_btn.setOnClickListener(this);
        tv_listActivity.setOnClickListener( this );

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

        timeChooseView.setWidthHeight(ScreenUtil.dip2px(this, timeList.size() * textSpacing + 20), ScreenUtil.dip2px(this, 80),timeList.size());// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示

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
            case R.id.tv_listActivity:
                Intent intent = new Intent( this, RecycleListActivity.class);
                startActivity( intent);
                break;
        }
    }

    @Override
    public void timeChooseMove( Object viewHolder, boolean direction, float distance) {

        if( direction ){
            scrollView.scrollMoveTo(0 ,distance);
        }else{
            scrollView.scrollMoveTo(0,-distance);
        }
    }

    @Override
    public void showSelectedMettingRoomDetail(boolean show) {
        if(show){
            toast = Toast.makeText( MainActivity.this, "准备显示已选定会议室信息",Toast.LENGTH_SHORT);
            Log.d("TimeChooseView", "======================准备显示已选定会议室信息======================");
        }else{
            toast = Toast.makeText( MainActivity.this, "准备隐藏已选定会议室信息",Toast.LENGTH_SHORT);
            Log.d("TimeChooseView", "======================准备隐藏已选定会议室信息======================");
        }
    }
}
