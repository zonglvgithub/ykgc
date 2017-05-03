package shoplistdownload.example.com.myapplication.modoule.recycle.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by zhanghuawei on 2017/5/2.
 */

public class ChooseTimeAdapter extends RecyclerView.Adapter<ChooseTimeAdapter.ViewHolder> {

    private Activity activity ;
    List<String> timeList = new ArrayList<>();
    List<List<TeamInfo> > lists = new ArrayList<>();
    int textSpacing = 60;//default item width dip
    private Toast toast;

    public ChooseTimeAdapter(Activity activity, List<List<TeamInfo>> lists) {
        this.activity = activity;
        this.lists = lists;
    }

    public void setLists(List<List<TeamInfo>> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public ChooseTimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.listitem, parent, false );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChooseTimeAdapter.ViewHolder viewHolder,final int position) {


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

            int max = 23;
            for (int i = 2; i < 23; i++) {


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
            public void outPutSelectedTime(String startTimeStr, String endTimeStr, int choosePosiotionCount) {
                Log.e("TimeChooseView", startTimeStr + "    ===   " + endTimeStr);
            }

            @Override
            public void scrollViewScroolTo(float scrollTo) {


                currentViewHolder.scrollView.scrollMoveTo(0, scrollTo );

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


        viewHolder.timeChooseView.setPositionList( lists.get(position));

        viewHolder.scrollView.setScrollViewListener(new My_ScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
                ViewHolder viewHolderScr = (ViewHolder) paramMy_ScrollView2.getViewHolderl();
                viewHolderScr.timeChooseView.setParamInt1(paramInt1);
            }
        });

        viewHolder.timeChooseView.setWidthHeight(ScreenUtil.dip2px(activity, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示



    }

    @Override
    public int getItemCount() {
        return lists==null?0:lists.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        private TimeChooseView timeChooseView;
        private My_ScrollView scrollView;
        private TextView tv_add_btn;
        private TextView tv_remove_btn;

        public ViewHolder(View contentView) {
            super(contentView);

            tv_add_btn = (TextView) contentView.findViewById(R.id.tv_add_btn);
            tv_remove_btn = (TextView) contentView.findViewById(R.id.tv_remove_btn);
            scrollView = (My_ScrollView) contentView.findViewById(R.id.scrollView);
            timeChooseView = (TimeChooseView) contentView.findViewById(R.id.time_choose);

        }
    }
}
