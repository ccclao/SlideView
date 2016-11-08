package com.deleteview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/10/31.
 */
public class MyListView extends ListView {
    private SlideView itemView;
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)){
            case MotionEvent.ACTION_DOWN:
                if(itemView != null){                                          //关闭之前的ItemView删除按钮的显示状态
                    itemView.close();
                }
                int dowX = (int) ev.getX();
                int dowY = (int) ev.getY();
                int itemPosition = pointToPosition(dowX,dowY);
                if(itemPosition == AbsListView.INVALID_POSITION){
                    return  super.dispatchTouchEvent(ev);
                }
                itemView = (SlideView) getChildAt(itemPosition - getFirstVisiblePosition());                  //记录当前ItemVIew
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
