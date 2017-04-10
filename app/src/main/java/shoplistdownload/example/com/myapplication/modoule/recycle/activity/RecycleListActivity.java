package shoplistdownload.example.com.myapplication.modoule.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import shoplistdownload.example.com.myapplication.R;
import shoplistdownload.example.com.myapplication.modoule.recycle.adapter.RylListAdapter;


public class RecycleListActivity extends Activity {

    private ListView recycleList;
    private RylListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_list);

        adapter = new RylListAdapter( this );
        recycleList = (ListView) findViewById(R.id.ryl_metting_room);
        recycleList.setAdapter( adapter );
    }
}
