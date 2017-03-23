package shoplistdownload.example.com.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * Created by wangxuan on 17/3/8.
 */

public class My_ScrollView extends HorizontalScrollView {

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
        public abstract void onScrollChanged(My_ScrollView paramMy_ScrollView2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    }


}
