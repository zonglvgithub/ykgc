package shoplistdownload.example.com.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
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

    private int textSize = 12;//时间字体大小
    private int textX = 50;//时间字体距左边位置
    private int textSpacing = 60;//时间字体间距 默认60


    private Bitmap bitmap;//按钮图片
    private float bitmap_width;//按钮宽度
    private float bitmap_radius;//按钮半径
    private float recordCenterY2;//记录按钮2圆心y轴
    private float butBottomCircle_y;//按钮2起始y轴
    private float linecCoarse = 1;//线宽
    private int paramInt1 = 0;//scroll移动y轴  解决控件点击坐标错位
    private float line_x;//表格起始x轴

    //矩形
    private float rectangular_y;//矩形起始y轴
    private float rectangular_to_y;//矩形起结束y轴
    private float rectangular_spacing;//矩形高度
    private float rectangular_x_begin;//记录矩形点击时的起始y轴
    private float rectangular_x_end;//记录矩形点击时的结束y轴

    //不可选择区域
    private float notChoose_y;
    private float notChoose_to_y;

    private float clickRawY;//点击位置的x
    private float clickRawX;//点击位置的y
    private float moveY;//记录移动时的y轴

    private boolean isMoveButton2;//是否移动按钮二
    private boolean isMoveRectangular;//是否移动矩形区域

    private long screenHeigth;

    private boolean showCheckedRect;// false:不显示已选中区域框 true 显示已选中区域
    private int checkPositon;//已选中position
    private float bottomH;//底线距离底部高度
    private float lineH; //竖线高度
    private int itemSpacing;//每一个时间块宽度

    private ValueAnimator anim;//移动动画
    private TimeChooseMoveIntreface timeChooseMoveIntreface;

    private Object ViewHolder;

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
        rectangular_x_begin = ScreenUtil.getScreenHeight(context) - line_x - statusBarHeight;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
        bitmap_width = bitmap.getWidth();

        statusBarHeight = ScreenUtil.getStatusBarHeight(context);
        //圆半径
        bitmap_radius = bitmap_width / 2;
        isPerform = true;

        screenHeigth = ScreenUtil.dip2px(context, 80);
        bottomH = ScreenUtil.dip2px(context, 5);
        lineH = screenHeigth / 5 * 3;

        itemSpacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));

    }

    public void setTime(List<TimeMode> timeList) {
        this.timeList = timeList;
    }

    public void setTimeChooseMoveIntreface(TimeChooseMoveIntreface timeChooseMoveIntreface) {
        this.timeChooseMoveIntreface = timeChooseMoveIntreface;
    }



    public void setTextSpacing(int textSpacing) {
        this.textSpacing = textSpacing;
        itemSpacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setParamInt1(int paramInt1) {
        this.paramInt1 = paramInt1;
    }

    public void setPosition(int position) {
        this.position = position;
        isPosition = true;
    }
    public boolean isShowCheckedRect() {
        return showCheckedRect;
    }

    public void setShowCheckedRect(boolean showCheckedRect) {
        this.showCheckedRect = showCheckedRect;

    }


    public Object getViewHolder() {
        return ViewHolder;
    }

    public void setViewHolder(Object viewHolder) {
        ViewHolder = viewHolder;
    }

    /**
     * 展示已选择会议室框
     * @param show t:显示 f:隐藏
     */
    private void showSelectTimeRecr( boolean show){
        setShowCheckedRect(show);
        /**
         * 第一次显示可选区域，回调展示选择会意思详情页面
         */

        if(show){
            if( timeChooseMoveIntreface != null ){
                timeChooseMoveIntreface.showSelectedMettingRoomDetail(show);
            }
        }else{
            if( timeChooseMoveIntreface != null ){
                timeChooseMoveIntreface.showSelectedMettingRoomDetail(show);
            }
        }


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

        Log.d(TAG, "开始绘制页面");

        int h = textSpacing / 2;
        NotChoosearea notChoosearea = null;
        int spacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
        line_x = ScreenUtil.dip2pxf(context, textX / 2);
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
        paintText.setAntiAlias(true);
        paintText.setTextSize(ScreenUtil.dip2pxf(context, textSize));
        canvas.drawLine(0f, screenHeigth - bottomH, BWScreenWidth, screenHeigth - bottomH, paintLine);// 画底部横线
        minimum_y = ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f)));
        float maximum_to_y = 0;
        for (int i = 0; i < timeList.size(); i++) {//画item线
            TimeMode time = timeList.get(i);

            if (i % 2 == 1) {
                canvas.drawLine(ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), screenHeigth - bottomH - lineH, ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), screenHeigth - bottomH, paintLine);// 垂直item线
            } else {
                canvas.drawLine(ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), screenHeigth - bottomH * 5 - lineH, ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), screenHeigth - bottomH, paintLine);// 垂直item线
            }
            maximum_to_y = ScreenUtil.dip2pxf(context, ((h - (textSize / 2.5f)) + textSpacing));
            //获取矩形初始位置
            if (isPerform) {
                rectangular_y = ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f)));
                rectangular_to_y = rectangular_y + ScreenUtil.dip2pxf(context, textSpacing);
                isPerform = false;
            }

            paintText.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(time.time, ScreenUtil.dip2pxf(context, h + 25), screenHeigth - bottomH * 2 - lineH, paintText);// 画文本
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
        canvas.drawRect(notChoose_y, screenHeigth - bottomH - lineH, notChoose_to_y, screenHeigth - bottomH, notChoosePaint);//非选区域
        for (int i = 0; i < notChooseareaList.size(); i++) {//遍历不可选择区域
            NotChoosearea nc = notChooseareaList.get(i);
            canvas.drawRect(nc.not_choosearea_y, screenHeigth - bottomH - lineH, nc.not_choosearea_to_y, screenHeigth - bottomH, notChoosePaint);//非选区域
        }

        if (rectangular_y < notChoose_to_y) {
            paint.setColor(context.getResources().getColor(R.color.red1));
            paintLine.setColor(context.getResources().getColor(R.color.red));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button_);
        } else {

            paintLine.setColor(context.getResources().getColor(R.color.green));
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
                paintLine.setColor(context.getResources().getColor(R.color.green));
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
            }
        }

        if (showCheckedRect) {//显示举行已选框
            paintLine.setStrokeWidth(3);
            canvas.drawLine( rectangular_y, screenHeigth - bottomH - lineH, rectangular_y, screenHeigth - bottomH, paintLine);

            canvas.drawRect( rectangular_y, screenHeigth - bottomH - lineH, rectangular_to_y, screenHeigth - bottomH, paint);//画矩形选择框

            canvas.drawLine( rectangular_to_y, screenHeigth - bottomH - lineH, rectangular_to_y, screenHeigth - bottomH, paintLine);
            //获取按钮初始位置
            butBottomCircle_y = rectangular_to_y - bitmap_radius;
            paint.setColor(context.getResources().getColor(R.color.white));
            canvas.drawBitmap(bitmap, butBottomCircle_y, screenHeigth - bottomH - lineH / 3 * 2, paint);//按钮2
        }


    }

    private float downRawX;//
    private float downRawY;//

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        invalidate();
        Log.e(TAG, "OnTouchEvent 执行 ");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.d(TAG, "DOWN");

                clickRawX = (int) event.getRawX();
                clickRawY = (int) (event.getRawY() - statusBarHeight);

                downRawX = event.getX();
                downRawY = event.getY();

                Log.e(TAG, "点击的坐标 x：" + clickRawX + " y：：" + clickRawY);

                //圆心坐标
                recordCenterY2 = (butBottomCircle_y + bitmap_radius - paramInt1);

                rectangular_x_begin = (rectangular_y - paramInt1);
                rectangular_x_end = (rectangular_to_y - paramInt1);
                onTouchListener(event, false);
                getParent().requestDisallowInterceptTouchEvent(false);
                isMoveButton2 = false;
                isMoveRectangular = false;

                if (clickRawX > recordCenterY2 - ScreenUtil.dip2px(context, 15) && clickRawX < recordCenterY2 + ScreenUtil.dip2px(context, 15)) {//是否按钮2移动
                    isMoveButton2 = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else if (clickRawY < ScreenUtil.getScreenHeight(context)
                        && clickRawX > rectangular_x_begin && clickRawX < rectangular_x_end) {//是否矩形框移动
                    isMoveRectangular = true;
                    rectangular_spacing = Math.abs(rectangular_to_y - rectangular_y);
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:

                Log.d(TAG, "MOVE");

                moveY = event.getRawX() - statusBarHeight + paramInt1;
                float mobile = (moveY - bitmap_radius);

                if (isMoveButton2) {//按钮2移动

                    if (mobile < maximum_y) {
                        if (itemSpacing < (int) (moveY - rectangular_y)) {
                            butBottomCircle_y = mobile;
                            rectangular_to_y = moveY;
                        }
                    }
                } else if (isMoveRectangular) {//矩形选择框移动
                    float rectangular_mobile = rectangular_x_begin + (moveY - clickRawX);
                    float rectangular_to_mobile = rectangular_x_end + (moveY - clickRawX);

                    if (rectangular_mobile > minimum_y && rectangular_to_mobile < maximum_y) {
                        rectangular_to_y = rectangular_to_mobile;
                        rectangular_y = rectangular_mobile;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                Log.d(TAG, "UP");

                float currentX = event.getX();
                float currentY = event.getY();

                if (Math.abs(currentX - downRawX) < 2 && Math.abs(currentY - downRawY) < 2) {

                    boolean clickPositionSlected = clickPositionSelected((int) event.getRawX(), event.getRawY());
                    if (isShowCheckedRect()) {//已选中进行移动

                        checkPositon = click2Position(currentX, currentY);
                        move2Click(itemSpacing);

                    } else {//未选中，选中点击取
                        showSelectTimeRecr(true);

                        checkPositon = click2Position(currentX, currentY);

                        if (isShowCheckedRect()) {
                            rectangular_y = (minimum_y + (checkPositon * itemSpacing));
                            rectangular_to_y = (minimum_y + (checkPositon * itemSpacing)) + itemSpacing;
                        }
                    }

                }

                if (isMoveButton2) {//按钮2移动
                    float mobileY = recordCenterY2;
                    float theOffset = ((moveY - paramInt1 - clickRawX) / itemSpacing);
                    int mobileNumber = Math.abs((int) theOffset);
                    float theOffset1 = (theOffset - mobileNumber);
                    if (theOffset > 0) {//下移
                        float spacing1 = 0;

                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 + itemSpacing * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 + itemSpacing;
                        }
                        if (theOffset1 < 0.5) {
                            spacing1 -= itemSpacing;
                        }
                        mobileY = (spacing1 + paramInt1);
                    } else {//上移
                        float spacing1 = 0;
                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 - itemSpacing * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 - itemSpacing;
                        }
                        if (theOffset1 > 0.5) {
                            mobileY = (spacing1 + paramInt1);
                        } else {
                            mobileY = (spacing1 + itemSpacing + paramInt1);
                        }
                        if ((int) spacing1 <= (int) rectangular_x_begin) {
                            mobileY = rectangular_y + itemSpacing;
                        }
                    }
                    if (mobileY < maximum_y) {
                        rectangular_to_y = mobileY;
                    } else {
                        rectangular_to_y = maximum_y;
                    }
                }
                if (isMoveRectangular) {//矩形选择框移动
                    float rectangularMobile = Math.round((rectangular_y - minimum_y) / itemSpacing);
                    int rectangularNumber = Math.abs((int) rectangularMobile);
                    float mobileY1 = rectangularNumber * itemSpacing + minimum_y;
                    rectangular_y = mobileY1;
                    rectangular_to_y = mobileY1 + rectangular_spacing;
                }

                List<String> startTime = new ArrayList<>();
                int start = (int) ((rectangular_y - minimum_y) / itemSpacing);
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
                int end = (int) ((rectangular_to_y - minimum_y) / itemSpacing);
                String endTimeStr;
                if (end % 2 == 0) {

                    if(end >= timeList.size()){
                        end = timeList.size()-1;
                    }
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

    private float[] click2PositionCoordinate(float x) {
        long textSpacingPx = ScreenUtil.dip2px(context, textSpacing);
        float[] coordinate = new float[2];
        coordinate[0] = (x - line_x) / textSpacingPx * textSpacingPx;
        coordinate[1] = coordinate[0] + (rectangular_x_end - rectangular_x_begin);

        Log.d(TAG, "移动坐标 coordinate x：" + coordinate[0] + "  " + coordinate[1]);
        return coordinate;
    }

    /**
     * 根据当前坐标获取当前的position
     */
    public int click2Position(float rawx, float y) {

        long textSpacingPx = ScreenUtil.dip2px(context, textSpacing);

        Log.d(TAG, "执行点击 textSpacing：" + textSpacingPx);

        int clickPosition = 0;
        clickPosition = (int) ((rawx - line_x) / textSpacingPx);
        float surplusPx = rawx % textSpacingPx;
        if (surplusPx > 0) {
            clickPosition++;
        }
        clickPosition--;//小标初始值0；
        Log.d(TAG, "执行点击 计算后position：" + clickPosition);

        return clickPosition;

    }


    /**
     * 根据坐标返回点击位置是否已被选中
     *
     * @param rawX
     * @param RawY
     * @return
     */
    private boolean clickPositionSelected(int rawX, double RawY) {

        return rawX >= rectangular_x_begin && rawX <= rectangular_x_end;

    }

    private void onTouchListener(MotionEvent event, boolean isChoose) {
        if (onTouchListener != null) {
            onTouchListener.onTouch(event, isChoose);
        }

    }

    private int BWScreenWidth;
    private int BWScreenHeight;
    private int itemSize;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(BWScreenWidth, BWScreenHeight);
    }

    public void setWidthHeight(int width, int height , int itemSize) {
        this.BWScreenHeight = height;
        this.BWScreenWidth = width;
        this.itemSize = itemSize;
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


    private void move2Click(float spacing) {

        if( anim != null && anim.isRunning()) return ;

        float rectangular_y1 = (minimum_y + (checkPositon * spacing));
        int lastCheckPosition = click2Position(rectangular_y, 0);
        int positionDiatance = Math.abs(checkPositon - lastCheckPosition);

        int checkItemSize = (int) ((rectangular_x_end - rectangular_x_begin)/spacing);
        if( checkPositon +positionDiatance == itemSize){//已选区域已经覆盖到右边最大值
            return;

        } else if( checkPositon +checkItemSize>itemSize){//已选区域最多移动到右边最大值
            checkPositon = itemSize-checkItemSize;
            rectangular_y1 = (minimum_y + (checkPositon * spacing));
        }


        float rectangular_to_y1 = (minimum_y + (checkPositon * spacing)) +(rectangular_x_end - rectangular_x_begin);



        int millisInFuture = 200;

        if (positionDiatance > 0) {
            millisInFuture = positionDiatance * 100;
            if (millisInFuture > 1000) {
                millisInFuture = 1000;
            } else if (millisInFuture > 800) {
                millisInFuture = 700;
            } else {
                millisInFuture = 300;
            }
        }

//        Log.d(TAG , "准备偏移  开始值"+rectangular_y+" 结束值："+rectangular_y1 +" 执行时间："+millisInFuture);
        anim = ValueAnimator.ofFloat(rectangular_y, rectangular_y1);
        anim.setDuration(millisInFuture);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                rectangular_y = currentValue;
                rectangular_to_y = currentValue + rectangular_x_end - rectangular_x_begin;

                invalidate();

                Log.d(TAG, "cuurent value is " + currentValue);
            }
        });
        anim.start();
    }

    /**
     * 去掉一个点位时间选择
     * 最少选择一个，否则无效
     */
    public boolean removePick() {

        if( anim != null && anim.isRunning()) return false;
        boolean addSuccess;


        scroll2LeftOnePosition();
        int itemCount = (int) (rectangular_to_y - rectangular_y) / itemSpacing;
        Log.d(TAG, "当前item数量：" + itemCount +"开始值" + rectangular_to_y + " 结束值：" + (rectangular_y ));
        if (itemCount > 1) {//当前状态可进行item递减
            addSuccess = true;

            if( anim != null && anim.isRunning()) return false;

            Log.d(TAG, "准备偏移  开始值" + rectangular_to_y + " 结束值：" + (rectangular_to_y - itemSpacing)  +" spacing:"+itemSpacing);
            anim = ValueAnimator.ofFloat(rectangular_to_y, rectangular_to_y - itemSpacing);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    rectangular_to_y = currentValue;

                    invalidate();

                    Log.d(TAG, "cuurent value is " + currentValue);
                }
            });
            anim.start();

        } else {
            addSuccess = false;
            invalidate();
            showSelectTimeRecr(false);
        }

        return addSuccess;
    }

    /**
     * 添加一个选择
     * 将选择时间右边所有时间选择
     * 否则无效
     */
    public boolean addPick() {

        if( anim != null && anim.isRunning()) return false;
        scroll2RightOnePosition();


        if(!isShowCheckedRect()){showSelectTimeRecr(true);}

        int maxPosition = BWScreenWidth / itemSpacing;//当前view最大item下标
        int currentSellectMaxPosition = (int) rectangular_to_y / itemSpacing;

        boolean removeSuccess;

        Log.d(TAG, "max item position index:" + maxPosition);
        Log.d(TAG, "current max item position index:" + currentSellectMaxPosition);

        if (currentSellectMaxPosition < maxPosition) {//当前状态可进行item的赠

            Log.d(TAG , "=====================================================");
            Log.d(TAG , "准备偏移  开始值"+rectangular_to_y+" 结束值："+(rectangular_to_y+itemSpacing) +" spacing:"+itemSpacing);

            anim = ValueAnimator.ofFloat(rectangular_to_y, rectangular_to_y + itemSpacing);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    rectangular_to_y = currentValue;

                    invalidate();

                    Log.d(TAG , "cuurent value is " + currentValue);
                }
            });
            anim.start();

            removeSuccess = true;
        } else {
            removeSuccess = false;
        }

        return removeSuccess;
    }

//    ...............
    public boolean move2NextPosition() {

        if( anim != null && anim.isRunning()) return false;
        scroll2RightOnePosition();

        int maxPosition = BWScreenWidth / itemSpacing;//当前view最大item下标
        int currentSellectMaxPosition = (int) rectangular_to_y / itemSpacing;

        boolean removeSuccess;

        Log.d(TAG, "max item position index:" + maxPosition);
        Log.d(TAG, "current max item position index:" + currentSellectMaxPosition);

        if (currentSellectMaxPosition < maxPosition) {//当前状态可进行item的赠

            Log.d(TAG , "=====================================================");
            Log.d(TAG , "准备偏移  开始值"+rectangular_to_y+" 结束值："+(rectangular_to_y+itemSpacing) +" spacing:"+itemSpacing);

            anim = ValueAnimator.ofFloat(rectangular_y, rectangular_y + itemSpacing);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
//                    if( currentValue>=){ }
                    rectangular_y = currentValue;
                    rectangular_to_y =currentValue + rectangular_x_end - rectangular_x_begin;
                    invalidate();



                    Log.d(TAG , "cuurent value is " + currentValue);
                }
            });
            anim.start();

            removeSuccess = true;
        } else {
            removeSuccess = false;
        }

        return removeSuccess;
    }


    /**
     * 当前选择最大item存在屏幕左边进行背景像item递减反方向移动
     */
    private void scroll2LeftOnePosition(){

        float screenLeft_x = getScreenLeft_x();
        int screenLeftPosition = (int)screenLeft_x/itemSpacing;
        int currentMaxPosition = (int)rectangular_to_y/itemSpacing;

        if( currentMaxPosition == screenLeftPosition || currentMaxPosition== (screenLeftPosition+1)){//进行北京页面滚动
            Log.d(TAG, "移动到左边某个位置");
            if( timeChooseMoveIntreface != null ){
                timeChooseMoveIntreface.timeChooseMove(getViewHolder(), false, itemSpacing);
            }
        }


    }


    private void scroll2RightOnePosition(){

        float screenRight = getScreenRight_x();
        int screenRightPosition = (int)screenRight/itemSpacing;
        int currentMaxPosition = (int)rectangular_to_y/itemSpacing;

        if( currentMaxPosition == screenRightPosition || currentMaxPosition == (screenRightPosition-1)){
            Log.d(TAG, "移动到右边某个位置");
            if( timeChooseMoveIntreface != null ){
                timeChooseMoveIntreface.timeChooseMove( getViewHolder(), true, itemSpacing);
            }
        }

    }

    /**
     * 返回屏幕右边框距离scrollView左边的x
     * @return
     */
    private float getScreenRight_x(){
        return paramInt1+ScreenUtil.getScreenWidth(context);
    }

    /**
     * 返回屏幕左边框距离scrollview左边的距离
     * @return
     */
    private float getScreenLeft_x(){
        return paramInt1;
    }


    /**
     * view移动接口
     */
    public interface TimeChooseMoveIntreface{

        /**
         * 移动回调
         * @param direction 移动距离
         * @param distance t:移动到右边某个位置 f:移动到左边某个位置
         */
        void timeChooseMove( Object viewHolder, boolean direction, float distance);

        /**
         * 隐藏/显示会议室已选详情布局
         * @param show t:显示详情布局 f:隐藏详情布局
         */
        void showSelectedMettingRoomDetail(boolean show);
    }

    /**
     * 当选择框移动到屏幕边缘父ScrollView进行滚动
     */
    private void moveScrollViewOnTouch(){
        float screenRight = getScreenRight_x();
        float screenleft = getScreenLeft_x();
        if( timeChooseMoveIntreface != null && (rectangular_to_y>screenRight )){
            timeChooseMoveIntreface.timeChooseMove( getViewHolder(),true, itemSpacing);
            move2NextPosition();
        }
        if( timeChooseMoveIntreface != null && (rectangular_to_y < screenleft)){
            timeChooseMoveIntreface.timeChooseMove(getViewHolder(),true, -itemSpacing);
            removePick();
        }
    }


}
