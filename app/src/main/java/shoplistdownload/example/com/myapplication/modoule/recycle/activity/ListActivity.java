package shoplistdownload.example.com.myapplication.modoule.recycle.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.modoule.bean.TeamInfo;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.ChooseTimeAdapter;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.RylListAdapter;

public class ListActivity extends Activity implements View.OnClickListener{

    private ListView recycleList;
    private RylListAdapter adapter;
    private TextView tv_refresh;

    private List<List<TeamInfo>> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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
                Toast.makeText( this, "刷",Toast.LENGTH_SHORT).show();
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
            adapter = new RylListAdapter( this ,lists);
        }else{

            adapter.notifyDataSetChanged();
        }
    }
}
