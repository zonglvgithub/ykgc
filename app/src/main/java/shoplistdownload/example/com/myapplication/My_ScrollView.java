package shoplistdownload.example.com.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by wangxuan on 17/3/8.
 */

public class My_ScrollView extends HorizontalScrollView {

    public static final String TAG = "My_ScrollView";

    private ScrollViewListener scrollViewListener = null;

    public My_ScrollView(Context paramContext) {
        super(paramContext);
    }

    public My_ScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public My_ScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.scrollViewListener != null) {
            this.scrollViewListener.onScrollChanged(this, paramInt1, paramInt2, paramInt3, paramInt4);
        }
    }

    public void setScrollViewListener(ScrollViewListener paramScrollViewListener) {
        this.scrollViewListener = paramScrollViewListener;
    }

    public static abstract interface ScrollViewListener {
         abstract void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    }



    private ValueAnimator anim;
    //上次移动剩余距离
    private float surplusDistance;
    private float lastAnimatedVale= 0;//上一次动画产生的中间移动距离

    public void scrollMoveTo(float begin ,final float end) {



        if(anim != null && anim.isRunning()){
            anim.cancel();
        }
        lastAnimatedVale = 0;

        Log.d(TAG, "scrollView 起始值："+begin +" 终点值："+end);
         anim = ValueAnimator.ofFloat(begin, end);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();

                surplusDistance = end-currentValue;

                Log.d(TAG, "scrollView 中间值："+currentValue +" 移动值："+surplusDistance);

                float moveDistance = currentValue-lastAnimatedVale;
                if( currentValue >0 ) scrollBy((int)(moveDistance+1),0);
                if( currentValue <0 ) scrollBy((int)(moveDistance-1),0);
//                scrollBy((int)(currentValue-lastAnimatedVale),0);
                lastAnimatedVale =currentValue;

            }
        });
        anim.start();

    }
}
