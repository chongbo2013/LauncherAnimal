package com.tos.launcher.animal;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.view.ViewHelper;

/**
 * 桌面上的小动物
 * Created by ferris.xu on 2016/7/26.
 */
public class AnimalView extends ImageView {


    private int iconWidth=0,iconHeight=0;
    private int mTouchSlop;
    private int offsetX,offsetY;
    private Rect mVisibityRect=new Rect();

    //走路动画
    private AnimationDrawable walkFrontAnimation;

    private AnimationDrawable walkLeftAnimation;


    public AnimalView(Context context) {
        super(context);
        init();
    }

    public AnimalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){

        mTouchSlop = 8;
        setBackgroundResource(R.drawable.animation_walk);
        walkFrontAnimation = (AnimationDrawable) getBackground();

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //请保证为充满屏幕的



    }


    public void initGloble(){
            offsetX=0;
            offsetY=SizeUtils.getStatusBarHeight(getContext());
            mVisibityRect.set(0,0,SizeUtils.getScreenWidth(),SizeUtils.getScreenHeight()-offsetY);
            iconWidth=getMeasuredWidth();
            iconHeight=getMeasuredHeight();
    }

    boolean isFrist=false;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initGloble();

        if(!isFrist){
            ViewHelper.setTranslationX(this,-iconWidth/2);
            ViewHelper.setTranslationY(this,(SizeUtils.getScreenHeight()-offsetY)/2-iconHeight/2);
            isFrist=true;
        }

    }

    //只要是按在了动物图片上，则默认消耗 了整个事件

    int lastX;
    int lastY;




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initGloble();
        //偏移 状态栏
        int x= (int) event.getRawX()-offsetX;
        int y= (int) event.getRawY()-offsetY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX=x;
                lastY=y;
                scaleAnimation(true);
                walkFrontAnimation.stop();
                walkFrontAnimation.start();
                break;
            case MotionEvent.ACTION_MOVE:

                float deltaX=x-lastX;
                float deltaY=y-lastY;
                Log.d("AnimalView","x="+x+",y="+y);
                if(Math.abs(deltaX)>mTouchSlop||Math.abs(deltaY)>mTouchSlop){
                    transientXY(x,y);
                }

                lastX=x;
                lastY=y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                scaleAnimation(false);
                smooth(x);
                lastX=0;
                lastY=0;
            default:
                break;
        }

        return true;
    }

    public void transientXY(int x,int y){
        if(mVisibityRect.contains(x,y)){
            x-=iconWidth/2;
            y-=iconHeight/2;
            ViewHelper.setTranslationX(this,x);
            ViewHelper.setTranslationY(this,y);
        }
    }

    public void scaleAnimation(boolean isBig){
            if(isBig){
              ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("scaleX",1.2f),PropertyValuesHolder.ofFloat("scaleY",1.2f)).setDuration(200L).start();
            }else{
                ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("scaleX",1f),PropertyValuesHolder.ofFloat("scaleY",1f)).setDuration(200L).start();
            }
    }

    //是否滚动到左边
    private boolean isLeft(int x){
       int ceterX= mVisibityRect.width()/2;
        return x<=ceterX;
    }

    public void smooth(int x){
        smoothToEG(isLeft(x));
    }
    private void smoothToEG(boolean isLeft){

        ObjectAnimator animation= ObjectAnimator.ofFloat(this,"translationX",isLeft?-iconWidth/2:mVisibityRect.width()-iconWidth/2);
        animation.setDuration(800L);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                walkFrontAnimation.stop();
                if(walkLeftAnimation==null){
                    setBackgroundResource(R.drawable.animation_walk_left);
                    walkLeftAnimation = (AnimationDrawable) getBackground();
                }else{
                    setBackground(walkLeftAnimation);
                }
                walkLeftAnimation.stop();
                walkLeftAnimation.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                walkLeftAnimation.stop();

                if(walkFrontAnimation!=null){
                    setBackground(walkFrontAnimation);
                }

                walkFrontAnimation.stop();
                walkFrontAnimation.start();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                walkLeftAnimation.stop();


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }




}
