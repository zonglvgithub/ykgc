package shoplistdownload.example.com.myapplication.modoule.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.modoule.bean.TeamInfo;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.ChooseTimeAdapter;


public class RecycleListActivity extends Activity implements View.OnClickListener{

    private RecyclerView recycleList;
    private ChooseTimeAdapter adapter;
    private TextView tv_refresh;

    private List<List<TeamInfo> > lists = new ArrayList<>();
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_list);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);

        tv_refresh.setOnClickListener( this );
        getData();
        recycleList = (RecyclerView) findViewById(R.id.ryl_metting_room);
        recycleList.setLayoutManager(new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false));
        recycleList.setAdapter( adapter );
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_refresh:
                getData();
                if( toast != null ) toast.cancel();
                toast = Toast.makeText( this, "刷",Toast.LENGTH_SHORT);

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
                        teamInfo = new TeamInfo("测试"+i,String.valueOf(j), j, j+2);
                    }else {
                        teamInfo = new TeamInfo("测试"+i,String.valueOf(j), j, j+1);
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
            adapter = new ChooseTimeAdapter( this ,lists);
//        }else{
            adapter.setLists(lists);
//            adapter.notifyDataSetChanged();
        }
    }
}
