package com.deleteview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;


/**
 * Created by 54420 on 2016/10/29.
 */
public class SlideView extends LinearLayout {
    private int downX,firstX;
    private Scroller scroller;
    private VelocityTracker mVelocityTracker;   //手速监听
    private int mTouchSLop;           //最小移动距离

    private int mDeleteWidth;     //删除按钮宽度

    private int minVelocity = 600;
    private State state = State.CLOSE;
    private int xVelocity,yVelocity;          //横向和纵向的手速

    public enum State{
        CLOSE,OPEN
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mTouchSLop = ViewConfiguration.get(context).getScaledTouchSlop();
        scroller = new Scroller(context);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addVelocityTracker(event);                      //添加手速监听
        switch (MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                xVelocity = 0;
                yVelocity = 0;
                firstX = downX = (int) event.getX();
                if(getChildCount() < 2){
                    try {
                        throw new Exception("必需包含两个子View");
                    } catch (Exception e) {
                        Log.e("DeleteView","必需包含两个子View");
                        e.printStackTrace();
                    }
                }else
                mDeleteWidth = getChildAt(1).getWidth();
                break;
            case MotionEvent.ACTION_MOVE:
                moveAction(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                scrollerRight();             //失去事件时，删除按钮隐藏
                removeVelocityTracker(event);         //移除手速监听
                break;
            case MotionEvent.ACTION_UP:
                if(Math.abs(xVelocity) > Math.abs(yVelocity) && Math.abs(yVelocity) < minVelocity){        //抬手时的手速判断
                    if(xVelocity > minVelocity ){
                        scrollerRight();
                    }else if(xVelocity < -minVelocity){
                        scrollerLeft();
                    }else{
                        scrollerByDisX();              //根据滑动的距离判断滑动方向
                    }
                }else {
                    scrollerByDisX();
                }
                removeVelocityTracker(event);            //移除手速监听
                break;
        }
        return true;
    }

    //滑动事件
    private void moveAction(MotionEvent event){
        int x = (int) event.getX();
        xVelocity = getXvelocity();
        yVelocity = getYvelocity();
        if(Math.abs(x - firstX) > mTouchSLop){
            getParent().requestDisallowInterceptTouchEvent(true);        //告诉父层已经处理了滑动事件，不要去拦截
            if(getScrollX() <= 0){         //如果删除按钮完全隐藏，则只能向左滑
                if(x < downX){
                    scrollerItem(x);
                }
            }else if(getScrollX() >= mDeleteWidth){      //如果完全显示删除按钮则只能向右滑
                if(x > downX){
                    scrollerItem(x);
                }
            }else {
                scrollerItem(x);
            }
            rectifyScroller();                //纠正因滑速过快造成的偏差
        }

        downX = x;
    }



    //移动
    private void scrollerItem(int x){
        int offsetX = downX - x;
        scrollBy(offsetX,0);
    }

    //移动到最左边
    private void  scrollerLeft(){
        int offsetX = mDeleteWidth - getScrollX();
        scroller.startScroll(getScrollX(),0,offsetX,0,Math.abs(offsetX));
        postInvalidate();
        state = State.OPEN;
    }

    //移动到最右边
    private void scrollerRight(){
        int offsetX = getScrollX();
        scroller.startScroll(getScrollX(),0,-offsetX,0,Math.abs(offsetX));
        postInvalidate();
        state =  State.CLOSE;
    }

    //纠正因移动过快产生的偏移差
    private void rectifyScroller(){
        if(getScrollX() < 0){
            scrollTo(0,0);
        }else if(getScrollX() > mDeleteWidth){
            scrollTo(mDeleteWidth,0);
        }
    }

    //抬手时根据移动距离判断是否显示删除按钮
    private void  scrollerByDisX(){
        if(getScrollX() > mDeleteWidth/2){
            scrollerLeft();
        }else {
            scrollerRight();
        }
    }

    @Override
    public void computeScroll() {
         if(scroller.computeScrollOffset()){
             scrollTo(scroller.getCurrX(),scroller.getCurrY());
             postInvalidate();
         }
    }

    //获取X轴的移动速度
    private int getXvelocity(){
       if(mVelocityTracker != null){
           mVelocityTracker.computeCurrentVelocity(1000);
           return (int) mVelocityTracker.getXVelocity();
       }
        return 0;
    }

    //获取Y轴的移动速度
    private int getYvelocity(){
        if(mVelocityTracker != null){
            mVelocityTracker.computeCurrentVelocity(1000);
            return (int) mVelocityTracker.getYVelocity();
        }
        return 0;
    }

    //添加手速监听
    private void addVelocityTracker(MotionEvent event){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    //移除速度监听器
    private void removeVelocityTracker(MotionEvent event){
        if(mVelocityTracker != null){
            mVelocityTracker.recycle();
        }
        mVelocityTracker = null;
    }

    //关闭删除按钮显示
    public void close(){
        if(state == State.OPEN){
            scrollerRight();
        }
    }
}
