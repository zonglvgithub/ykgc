package shoplistdownload.example.com.myapplication.modoule.main.activity;

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
import shoplistdownload.example.com.myapplication.modoule.bean.TeamInfo;
import shoplistdownload.example.com.myapplication.modoule.recycle.activity.ListActivity;
import shoplistdownload.example.com.myapplication.modoule.recycle.activity.RecycleListActivity;


public class MainActivity extends Activity implements View.OnClickListener,TimeChooseView.TimeChooseIntreface {
    TimeChooseView timeChooseView;
    My_ScrollView scrollView;
    List<String> timeList = new ArrayList<>();
    int textSpacing = 60;


    private TextView tv_add_btn;
    private TextView tv_remove_btn;
    private TextView tv_listActivity;
    private TextView tv_refresh;
    private TextView tv_list;

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
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        tv_list = (TextView) findViewById(R.id.tv_list);


        tv_add_btn.setOnClickListener(this);
        tv_remove_btn.setOnClickListener(this);
        tv_listActivity.setOnClickListener( this );
        tv_refresh.setOnClickListener( this );
        tv_list.setOnClickListener( this );

        int max =23;
        for (int i = 2; i < max; i++) {

            if(i<10){
                timeList.add("0"+String.valueOf(i) +":00");
                if(i<max-1){
                    timeList.add("0"+i+":30");
                }
            }else{
                timeList.add(String.valueOf(i)+":00" );
                if(i<max-1){
                    timeList.add(i+":30");
                }
            }

        }
        timeChooseView.setTime(timeList);
        timeChooseView.setTimeChooseMoveIntreface( this );

        timeChooseView.setItemWidthDip(textSpacing);
        productData();

        scrollView.setScrollViewListener(new My_ScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
                timeChooseView.setParamInt1(paramInt1);
            }
        });

        timeChooseView.setWidthHeight( ScreenUtil.dip2px(this, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示

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
            case R.id.tv_refresh:

                productData();
                break;
            case R.id.tv_list:
                Intent intent1 = new Intent( this, ListActivity.class);
                startActivity( intent1 );
                break;
        }
    }

    private void productData(){

        List<TeamInfo> list = new ArrayList<>();

        int max =  (int)(Math.random()*40);

        for(int i=0;i<max;i++){

            if(i>30 ) break;
            if(i%3==0 ){
                TeamInfo teamInfo = null;

                if(i<4){
                    int position  =  (int)(Math.random()*10);
                    teamInfo = new TeamInfo( "", "", 0, position);
                    i=5;
                    list.add(teamInfo);
                    continue;
                }
                if(i/3==2){
                    teamInfo = new TeamInfo("测试数据",String.valueOf(i),i,i+2);
                }else {
                    teamInfo = new TeamInfo("测试数据",String.valueOf(i),i,i+1);
                }

                list.add(teamInfo);
            }

        }


        timeChooseView.setPositionList(list);
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

    @Override
    public void outPutSelectedTime(String startTimeStr, String endTimeStr,  int choosePosiotionCount) {
        Log.e("TimeChooseView", startTimeStr + "    ===   " + endTimeStr);
    }

    @Override
    public void scrollViewScroolTo(float scrollTo) {
//        ValueAnimator anim = ValueAnimator.ofFloat(0, scrollTo);
//        lastx = 0;
//        anim.setDuration(1000);
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float) animation.getAnimatedValue();
//
//                scrollView.scrollMoveTo(0, currentValue );
//                lastx = currentValue;
//            }
//        });
//        anim.start();
        scrollView.scrollMoveTo(0, scrollTo);
    }

    @Override
    public void chooseTeamId(String teamId) {
        Toast.makeText(this, "团队ID："+teamId, Toast.LENGTH_SHORT).show();
    }
}
