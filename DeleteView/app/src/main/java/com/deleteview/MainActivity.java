package com.deleteview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> lists = new ArrayList<>();
    private MyListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (MyListView) findViewById(R.id.list_view);

        init();
    }

    private void init(){
        for (int i=0;i<20;i++){
            lists.add("item="+i);
        }

        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.item_view,null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
                viewHolder.deleteText = (TextView) view.findViewById(R.id.delete_text);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.textView.setText(lists.get(i));
            viewHolder.deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_LONG).show();
                }
            });

            return view;
        }

        class ViewHolder{
            TextView textView;
            TextView deleteText;
        }
    }
}
