package shoplistdownload.example.com.myapplication.modoule.recycle.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shoplistdownload.example.com.myapplication.My_ScrollView;
import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.ScreenUtil;
import shoplistdownload.example.com.myapplication.TimeChooseView;
import shoplistdownload.example.com.myapplication.modoule.bean.TeamInfo;

/**
 * Created by zhanghuawei on 2017/3/28.
 */

public class RylListAdapter extends BaseAdapter{

    public static final String TAG = "RylListAdapter";

    List<String> timeList = new ArrayList<>();
    List<TeamInfo> list = new ArrayList<>();
    int textSpacing = 60;//default item width dip
    private ViewHolder viewHolder;
    private Activity activity;

    public RylListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    float lastx = 0;
    @Override
    public View getView(final int position, View contentView , ViewGroup viewGroup) {


        Log.d(TAG, "getView 调用");
        if( contentView == null ){
            viewHolder = new ViewHolder();
            contentView = LayoutInflater.from(activity).inflate(R.layout.listitem,viewGroup,false);
            viewHolder.tv_add_btn = (TextView) contentView.findViewById(R.id.tv_add_btn);
            viewHolder.tv_remove_btn = (TextView) contentView.findViewById(R.id.tv_remove_btn);
            viewHolder.scrollView = (My_ScrollView) contentView.findViewById(R.id.scrollView);
            viewHolder.timeChooseView = (TimeChooseView) contentView.findViewById(R.id.time_choose);

            contentView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) contentView.getTag();
        }

        final ViewHolder currentViewHolder = viewHolder;

        viewHolder.tv_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean addSuccess = currentViewHolder.timeChooseView.addPick();
                if( toast != null ) toast.cancel();

                if(addSuccess){
                    toast = Toast.makeText( activity, "添加成功"+position, Toast.LENGTH_SHORT);
                }else{

                    toast = Toast.makeText( activity, "添加失败"+position, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        viewHolder.tv_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( toast != null ) toast.cancel();
                boolean removeSuccess = currentViewHolder.timeChooseView.removePick();
                if(removeSuccess){
                    toast = Toast.makeText( activity, "删除成功"+position,Toast.LENGTH_SHORT);
                }else{
                    toast = Toast.makeText( activity, "删除失败"+position,Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        if( timeList == null || timeList.size()<1){
            for (int j = 2; j < 23; j++) {

                timeList.add(String.valueOf(j) );
                timeList.add(j+"：30");
            }
            Log.d(TAG, "初始化时间资源");
        }

        viewHolder.timeChooseView.setTimeChooseMoveIntreface(new TimeChooseView.TimeChooseIntreface() {
            @Override
            public void timeChooseMove( Object viewHolder,boolean direction, float distance) {
                ViewHolder viewHolder1 = (ViewHolder) viewHolder;
                if( direction ){
                    viewHolder1.scrollView.scrollMoveTo(0 ,distance);
                }else{
                    viewHolder1.scrollView.scrollMoveTo(0,-distance);
                }
            }

            @Override
            public void showSelectedMettingRoomDetail(boolean show) {

            }

            @Override
            public void outPutSelectedTime(String startTimeStr, String endTimeStr) {
                Log.e("TimeChooseView", startTimeStr + "    ===   " + endTimeStr);
            }

            @Override
            public void scrollViewScroolTo(float scrollTo) {


//                ValueAnimator anim = ValueAnimator.ofFloat(0, scrollTo);
//                lastx = 0;
//                anim.setDuration(500);
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        float currentValue = (float) animation.getAnimatedValue();
//
//                        currentViewHolder.scrollView.scrollMoveTo(lastx, currentValue );
//                        lastx = currentValue;
//                    }
//                });
//                anim.start();
                currentViewHolder.scrollView.scrollMoveTo(lastx, scrollTo );

            }

            @Override
            public void chooseTeamId(String teamId) {
                Toast.makeText( activity, "团队ID："+teamId, Toast.LENGTH_SHORT).show();
            }
        });

        //TODO：数据源支持初始已选框position 保证getView item 显示数据不会错乱
        viewHolder.timeChooseView.setTime(timeList);
        viewHolder.timeChooseView.setViewHolder(viewHolder);
        viewHolder.scrollView.setViewHolderl(viewHolder);
        viewHolder.timeChooseView.setItemWidthDip(textSpacing);


        if( list == null || list.size()<1){

            for(int i=0;i<30;i++){


                if(i%3==0 ){
                    TeamInfo teamInfo = null;
                    if(i<4){
                        teamInfo = new TeamInfo("","",0,4);
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

        }



        viewHolder.timeChooseView.setPositionList(list);

        viewHolder.scrollView.setScrollViewListener(new My_ScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
                ViewHolder viewHolderScr = (ViewHolder) paramMy_ScrollView2.getViewHolderl();
                viewHolderScr.timeChooseView.setParamInt1(paramInt1);
            }
        });

        viewHolder.timeChooseView.setWidthHeight(ScreenUtil.dip2px(activity, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示


        return contentView;
    }

    private Toast toast;


    class ViewHolder{

        private TimeChooseView timeChooseView;
        private My_ScrollView scrollView;
        private TextView tv_add_btn;
        private TextView tv_remove_btn;

    }

}
