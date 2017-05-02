package shoplistdownload.example.com.myapplication.modoule.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.modoule.bean.TeamInfo;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.RylListAdapter;


public class RecycleListActivity extends Activity implements View.OnClickListener{

    private ListView recycleList;
    private RylListAdapter adapter;
    private TextView tv_refresh;

    private List<List<TeamInfo> > lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_list);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);




        tv_refresh.setOnClickListener( this );
        getData();
        recycleList = (ListView) findViewById(R.id.ryl_metting_room);
        recycleList.setAdapter( adapter );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_refresh:
                getData();
                break;
        }
    }

    private void getData(){

        if(lists != null ){
            lists.clear();
        }
        for(int i=0 ; i< 30;i++){

            List<TeamInfo> list = new ArrayList<>();

            int max =  (int)(Math.random()*40);

            for(int j=0;j<max;j++){

                if(j>30 ) break;
                if(j%3==0 ){
                    TeamInfo teamInfo = null;

                    if(j<1){
                        int position  =  (int)(Math.random()*10);
                        teamInfo = new TeamInfo( "", "", 0, position);
                        j=5;
                        list.add(teamInfo);
                        continue;
                    }
                    if(j/3==2){
                        teamInfo = new TeamInfo("测试数据",String.valueOf(j), j, j+2);
                    }else {
                        teamInfo = new TeamInfo("测试数据",String.valueOf(j), j, j+1);
                    }

                    list.add(teamInfo);
                }

            }

            lists.add( list );
        }
        refreshAdapter();
    }

    private void refreshAdapter(){

        if(adapter == null ){
            adapter = new RylListAdapter( this ,lists);
        }else{
            adapter.notifyDataSetChanged();
        }
    }
}
