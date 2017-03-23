package shoplistdownload.example.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 需要设置当前activity 去除标题 android:theme="@android:style/Theme.NoTitleBar"
 * <p>
 * 该控件如果非全屏会出现显示坐标与点击坐标错位 通过 setParamInt1 传入起始坐标可正负自行调节 解决控件点击坐标错位
 * Created by wangxuan on 17/3/8.
 */

public class TimeChooseView extends View {

    public static final String TAG = "TimeChooseView";

    private Context context;
    private int statusBarHeight;//状态栏高度
    private boolean isPerform = true;//执行一次
    private OnTouchListener onTouchListener;
    private SelectedTime selectedTime;
    private int position;//起始位置
    private boolean isPosition = false;//执行一次 首次加载执行
    private List<Integer> positionList;//不可选择区域位置集合
    private boolean isPositionList = false;//执行一次 首次加载执行(可刷新)
    private List<NotChoosearea> notChooseareaList = new ArrayList<>();//不可选择区域集合


    private List<TimeMode> timeList;//时间集合
    private float minimum_y;//可移动最小y
    private float maximum_y = 0;//可移动最大y
    private boolean isMaximum_y = true;//获取最大值

    private int textSize = 16;//时间字体大小
    private int textX = 50;//时间字体距左边位置
    private int textSpacing = 0;//时间字体间距


    private Bitmap bitmap;//按钮图片
    private float bitmap_width;//按钮宽度
    private float bitmap_radius;//按钮半径




    private float recordCenterY1;//记录按钮1圆心y轴
    private float recordCenterY2;//记录按钮2圆心y轴


    private float butBottomCircle_x;//按钮2起始x轴
    private float butBottomCircle_y;//按钮2起始y轴


    private float linecCoarse = 1;//线宽

    public void setParamInt1(int paramInt1) {
        this.paramInt1 = paramInt1;
    }

    private int paramInt1 = 0;//scroll移动y轴  解决控件点击坐标错位

    private float line_x;//表格起始x轴

    //矩形
    private float rectangular_y;//矩形起始y轴
    private float rectangular_to_y;//矩形起结束y轴
    private float rectangular_spacing;//矩形高度
    private float rectangular_y1;//记录矩形点击时的起始y轴
    private float rectangular_to_y2;//记录矩形点击时的结束y轴

    //不可选择区域
    private float notChoose_y;
    private float notChoose_to_y;

    private float lastX;//点击位置的x
    private float lastY;//点击位置的y
    private float moveY;//记录移动时的y轴

    private boolean isMoveButton2;//是否移动按钮二
    private boolean isMoveRectangular;//是否移动矩形区域

    private long screenHeigth;

    private boolean showCheckedRect;// false:不显示已选中区域框 true 显示已选中区域
    private int checkPositon;//已选中position


    public TimeChooseView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TimeChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

    }

    private void init() {
        butBottomCircle_x = ScreenUtil.getScreenHeight(context) - line_x - statusBarHeight;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
        bitmap_width = bitmap.getWidth();

        statusBarHeight = ScreenUtil.getStatusBarHeight(context);
        //圆半径
        bitmap_radius = bitmap_width / 2;
        isPerform = true;

        screenHeigth = ScreenUtil.getScreenHeight(context) / 5;

    }

    public void setTime(List<TimeMode> timeList) {
        this.timeList = timeList;
    }

    public void setTextSpacing(int textSpacing) {
        this.textSpacing = textSpacing;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setPosition(int position) {
        this.position = position;
        isPosition = true;
    }

    /**
     * 设置不可选择区域集合从方法必须在setTextSpacing 之后调用
     *
     * @param positionList
     */
    public void setPositionList(List<Integer> positionList) {
        this.positionList = positionList;
        isPositionList = true;
    }

    public void setSelectedTime(SelectedTime selectedTime) {
        this.selectedTime = selectedTime;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        int h = textSpacing;
        NotChoosearea notChoosearea = null;
        int spacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
        line_x = ScreenUtil.dip2pxf(context, textX);
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.transparencyg_reen_20));

        Paint notChoosePaint = new Paint();
        notChoosePaint.setColor(context.getResources().getColor(R.color.gray1));


        Paint paintLine = new Paint();
        paintLine.setColor(context.getResources().getColor(R.color.gray));
        paintLine.setStrokeWidth(linecCoarse);
        //时间画笔
        Paint paintText = new Paint();
        paintText.setColor(context.getResources().getColor(R.color.black));
        paintText.setTextSize(ScreenUtil.dip2pxf(context, textSize));
        canvas.drawLine(0f, (float) (screenHeigth - line_x - statusBarHeight), BWScreenWidth, (float) (screenHeigth - line_x - statusBarHeight), paintLine);// 画竖线
        minimum_y = ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f)));
        float maximum_to_y = 0;
        for (int i = 0; i < timeList.size(); i++) {//画item线
            TimeMode time = timeList.get(i);
            canvas.drawLine(ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), 0, ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), screenHeigth - line_x - statusBarHeight, paintLine);// 画水平线
            maximum_to_y = ScreenUtil.dip2pxf(context, ((h - (textSize / 2.5f)) + textSpacing));
            //获取矩形初始位置
            if (isPerform) {
                rectangular_y = ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f)));
                rectangular_to_y = rectangular_y + ScreenUtil.dip2pxf(context, textSpacing);
                isPerform = false;
            }

            paintText.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(time.time, ScreenUtil.dip2pxf(context, h), screenHeigth + statusBarHeight - ScreenUtil.dip2pxf(context, textX), paintText);// 画文本
            h += textSpacing;
        }
        if (isMaximum_y) {
            maximum_y = maximum_to_y - (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
            isMaximum_y = false;
        }

        if (isPosition) {
            isPosition = false;

            notChoose_y = minimum_y;
            notChoose_to_y = (minimum_y + (position * spacing)) + spacing;
        }

        if( showCheckedRect ){
            rectangular_y = (minimum_y + (checkPositon * spacing));
            rectangular_to_y = (minimum_y + (checkPositon * spacing)) + spacing;
        }


        if (isPositionList) {
            isPositionList = false;
            int t = 0;
            for (int i = 0; i < positionList.size(); i++) {//遍历不可选择区域
                int position = positionList.get(i);
                if ((++t) == position) {
                    notChoosearea = notChooseareaList.get((notChooseareaList.size() - 1));
                    notChoosearea.not_choosearea_to_y = (minimum_y + (position * spacing)) + spacing;
                    notChooseareaList.set((notChooseareaList.size() - 1), notChoosearea);
                } else {
                    notChoosearea = new NotChoosearea();
                    notChoosearea.not_choosearea_y = (minimum_y + (position * spacing));
                    notChoosearea.not_choosearea_to_y = (minimum_y + (position * spacing)) + spacing;
                    notChooseareaList.add(notChoosearea);
                }
                t = position;
            }
        }
        canvas.drawRect(notChoose_y, 0, notChoose_to_y, screenHeigth - statusBarHeight - line_x, notChoosePaint);//非选区域

        for (int i = 0; i < notChooseareaList.size(); i++) {//遍历不可选择区域
            NotChoosearea nc = notChooseareaList.get(i);
            canvas.drawRect(nc.not_choosearea_y, 0, nc.not_choosearea_to_y, screenHeigth - line_x - statusBarHeight, notChoosePaint);//非选区域
        }

        if (rectangular_y < notChoose_to_y) {
            paint.setColor(context.getResources().getColor(R.color.red1));
            paintLine.setColor(context.getResources().getColor(R.color.red));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button_);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
        }
        for (int i = 0; i < notChooseareaList.size(); i++) {//浮框占用已经选择区域切换可选（绿色）不可选（红色）状态
            NotChoosearea nc = notChooseareaList.get(i);
            if (rectangular_to_y > nc.not_choosearea_y && rectangular_to_y < nc.not_choosearea_to_y
                    || rectangular_y > nc.not_choosearea_y && rectangular_y < nc.not_choosearea_to_y
                    || rectangular_y == nc.not_choosearea_y && rectangular_to_y == nc.not_choosearea_to_y
                    || rectangular_y < nc.not_choosearea_y && rectangular_to_y >= nc.not_choosearea_to_y
                    || rectangular_y <= nc.not_choosearea_y && rectangular_to_y > nc.not_choosearea_to_y) {
                paint.setColor(context.getResources().getColor(R.color.red1));
                paintLine.setColor(context.getResources().getColor(R.color.red));
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button_);
                break;
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
            }
        }

        if( showCheckedRect ){//显示举行已选框

            canvas.drawRect(rectangular_y, 0, rectangular_to_y, screenHeigth - line_x - statusBarHeight, paint);//画矩形选择框
            //获取按钮初始位置
            butBottomCircle_y = rectangular_to_y - bitmap_radius;
            canvas.drawBitmap(bitmap, butBottomCircle_y, 60, paint);//按钮2
        }



    }

    private float downRawX;//
    private float downRawY;//

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        invalidate();
        Log.e("==================", "    =====       ");
        int spacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                Log.d( TAG, "DOWN");

                lastY = (int) event.getRawX();
                lastX = (int) (event.getRawY() - statusBarHeight);

                downRawX = event.getX();
                downRawY = event.getY();

                Log.e( TAG, "点击的坐标 x：" + lastY + " y：：" + lastX);

                //圆心坐标
                recordCenterY2 = (butBottomCircle_y + bitmap_radius - paramInt1);

                rectangular_y1 = (rectangular_y - paramInt1);
                rectangular_to_y2 = (rectangular_to_y - paramInt1);
                onTouchListener(event, false);
                getParent().requestDisallowInterceptTouchEvent(false);
                isMoveButton2 = false;
                isMoveRectangular = false;

                    if (lastY>recordCenterY2-ScreenUtil.dip2px(context, 15) && lastY<recordCenterY2+ScreenUtil.dip2px(context, 15)) {//是否按钮2移动
                    isMoveButton2 = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if (lastX < (screenHeigth * 5 - statusBarHeight - line_x) && lastX < ScreenUtil.getScreenHeight(context)
                        && lastY > rectangular_y1 && lastY < rectangular_to_y2) {//是否矩形框移动
                    isMoveRectangular = true;
                    rectangular_spacing = Math.abs(rectangular_to_y - rectangular_y);
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:

                Log.d( TAG, "MOVE");

                moveY = event.getRawX() - statusBarHeight + paramInt1;
                float mobile = (moveY - bitmap_radius);

                    if (isMoveButton2) {//按钮2移动

                    if (mobile < maximum_y) {
                        if (spacing < (int) (moveY - rectangular_y)) {
                            butBottomCircle_y = mobile;
                            rectangular_to_y = moveY;
                        }
                    }
                } else if (isMoveRectangular) {//矩形选择框移动

                    float rectangular_mobile = rectangular_y1 + (moveY - lastY);
                    float rectangular_to_mobile = rectangular_to_y2 + (moveY - lastY);

                    if (rectangular_mobile > minimum_y && rectangular_to_mobile < maximum_y) {
                        rectangular_to_y = rectangular_to_mobile;
                        rectangular_y = rectangular_mobile;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                Log.d( TAG, "UP");


                float currentRawX = event.getX();
                float currentRawY = event.getY();

                if( Math.abs(currentRawX -downRawX)<2 && Math.abs(currentRawY -downRawY)<2){

                    checkPositon = click2Position(currentRawX, currentRawY);
                    showCheckedRect = true;

                }

                if (isMoveButton2) {//按钮2移动
                    float mobileY = recordCenterY2;
                    float theOffset = ((moveY - paramInt1 - lastY) / spacing);
                    int mobileNumber = Math.abs((int) theOffset);
                    float theOffset1 = (theOffset - mobileNumber);
                    if (theOffset > 0) {//下移
                        float spacing1 = 0;

                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 + spacing * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 + spacing;
                        }
                        if (theOffset1 < 0.5) {
                            spacing1 -= spacing;
                        }
                        mobileY = (spacing1 + paramInt1);
                    } else {//上移
                        float spacing1 = 0;
                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 - spacing * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 - spacing;
                        }
                        if (theOffset1 > 0.5) {
                            mobileY = (spacing1 + paramInt1);
                        } else {
                            mobileY = (spacing1 + spacing + paramInt1);
                        }
                        if ((int) spacing1 <= (int) recordCenterY1) {
                            mobileY = rectangular_y + spacing;
                        }
                    }
                    if (mobileY < maximum_y) {
                        rectangular_to_y = mobileY;
                    } else {
                        rectangular_to_y = maximum_y;
                    }
                }
                if (isMoveRectangular ) {//矩形选择框移动
                    float rectangularMobile = Math.round((rectangular_y - minimum_y) / spacing);
                    int rectangularNumber = Math.abs((int) rectangularMobile);
                    float mobileY1 = rectangularNumber * spacing + minimum_y;
                    rectangular_y = mobileY1;
                    rectangular_to_y = mobileY1 + rectangular_spacing;
                }

                List<String> startTime = new ArrayList<>();
                int start = (int) ((rectangular_y - minimum_y) / spacing);
                String startTimeStr;
                if (start % 2 == 0) {
                    startTimeStr = timeList.get(start).time;
                } else {
                    StringTokenizer st = new StringTokenizer(timeList.get((start - 1)).time, ":");
                    while (st.hasMoreElements()) {
                        startTime.add(st.nextToken());
                    }
                    startTimeStr = startTime.get(0) + ":30";
                }
                List<String> endTime = new ArrayList<>();
                int end = (int) ((rectangular_to_y - minimum_y) / spacing);
                String endTimeStr;
                if (end % 2 == 0) {
                    endTimeStr = timeList.get(end).time;
                } else {
                    StringTokenizer st = new StringTokenizer(timeList.get((end - 1)).time, ":");
                    while (st.hasMoreElements()) {
                        endTime.add(st.nextToken());
                    }
                    endTimeStr = endTime.get(0) + ":30";
                }
                selectedTime.getSelectedTime(startTimeStr, endTimeStr);
                break;
        }


        return true;
    }

    /**
     * 根据当前坐标获取当前的position
     */
    public int click2Position( float rawx, float y){

        long textSpacingPx = ScreenUtil.dip2px(context, textSpacing);

        Log.d( TAG, "执行点击 textSpacing："+textSpacingPx);

        int clickPosition = 0;
        clickPosition = (int)((rawx-line_x)/textSpacingPx);
        float surplusPx = rawx%textSpacingPx;
        if( surplusPx>0){
            clickPosition++;
        }
        clickPosition--;//小标初始值0；
        Log.d( TAG, "执行点击 计算后position："+clickPosition);

        return clickPosition;

    }


    private void onTouchListener(MotionEvent event, boolean isChoose) {
        if (onTouchListener != null) {
            onTouchListener.onTouch(event, isChoose);
        }

    }

    private int BWScreenWidth;
    private int BWScreenHeight;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(BWScreenWidth, BWScreenHeight);
    }

    public void setWidthHeight(int width, int height) {
        this.BWScreenHeight = height;
        this.BWScreenWidth = width;
    }


    public interface OnTouchListener {
        void onTouch(MotionEvent event, boolean isChoose);
    }

    public interface SelectedTime {
        void getSelectedTime(String startTimeStr, String endTimeStr);
    }

    class NotChoosearea {
        public float not_choosearea_y;
        public float not_choosearea_to_y;

    }


}
