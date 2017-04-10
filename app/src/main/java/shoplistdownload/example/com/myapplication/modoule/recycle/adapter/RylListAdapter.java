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
import shoplistdownload.example.com.myapplication.TimeMode;

/**
 * Created by zhanghuawei on 2017/3/28.
 */

public class RylListAdapter extends BaseAdapter{

    List<TimeMode> timeList = new ArrayList<>();
    int textSpacing = 60;
    private ViewHolder viewHolder;
    private Activity activity;

    public RylListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View contentView , ViewGroup viewGroup) {

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

        for (int j = 2; j < 23; j++) {
            TimeMode timeMode = new TimeMode();
            timeMode.time = j + "时";
            timeMode.isSelected = false;
            TimeMode timeMode1 = new TimeMode();
            timeMode1.time = "";
            timeMode1.isSelected = false;

            timeList.add(timeMode);
            timeList.add(timeMode1);
        }
        viewHolder.timeChooseView.setTimeChooseMoveIntreface(new TimeChooseView.TimeChooseMoveIntreface() {
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
        });
        viewHolder.timeChooseView.setTime(timeList);
        viewHolder.timeChooseView.setViewHolder(viewHolder);
        viewHolder.scrollView.setViewHolderl(viewHolder);
        viewHolder.timeChooseView.setTextSpacing(textSpacing);
        viewHolder.timeChooseView.setPosition(3);
        List<Integer> list = new ArrayList<>();

        list.add(5);
        list.add(7);
        list.add(8);
        list.add(10);


        viewHolder.timeChooseView.setPositionList(list);
        viewHolder.timeChooseView.setSelectedTime(new TimeChooseView.SelectedTime() {
            @Override
            public void getSelectedTime(String startTimeStr, String endTimeStr) {
                Log.e("TimeChooseView", startTimeStr + "    ===   " + endTimeStr);
            }
        });

        viewHolder.scrollView.setScrollViewListener(new My_ScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
                ViewHolder viewHolderScr = (ViewHolder) paramMy_ScrollView2.getViewHolderl();
                viewHolderScr.timeChooseView.setParamInt1(paramInt1);
            }
        });

        viewHolder.timeChooseView.setWidthHeight(ScreenUtil.dip2px(activity, timeList.size() * textSpacing + 20), ScreenUtil.dip2px(activity, 80));// 重新绘制宽高，不然自定义控件放在ScrollView里面没有高度不显示


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
