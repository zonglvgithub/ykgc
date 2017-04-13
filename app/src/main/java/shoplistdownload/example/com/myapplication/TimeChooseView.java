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
    private OnTouchListener onTouchListener;
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
    private int chooseViewMarginLeft = 50;//时间字体距左边位置
    private int textSpacing = 60;//时间字体间距 默认60


    private Bitmap bitmap;//按钮图片
    private float bitmap_width;//按钮宽度
    private float bitmap_radius;//按钮半径
    private float recordCenterY2;//记录按钮2圆心y轴
    private float butBottomCircle_y;//按钮2起始y轴
    private float linecWidth = 1;//线宽
    private int scrollView_scroll_distance = 0;//scroll移动x轴  解决控件点击坐标错位
    private float chooseTimeStart_x;//表格起始x轴

    //矩形
    private float rectangular_x;//已选矩形起始x轴
    private float rectangular_to_x;//已选矩形起结束x轴
    private float rectangular_spacing;//已选矩形宽度
    private float rectangular_x_begin;//记录矩形点击时的起始y轴
    private float rectangular_x_end;//记录矩形点击时的结束y轴

    //不可选择区域
    private float notChoose_left_x;//不可选区域左边x值               整理后将移除这组值
    private float notChoose_right_x;//不可选区域右边x值

    private float clickRawY;//点击位置的y
    private float clickRawX;//点击位置的x
    private float moveRawX;//记录移动时的x轴

    private boolean isMoveButton2;//是否移动按钮二
    private boolean isMoveRectangular;//是否移动矩形区域

    private long timeViewHeigth;//整个自定义view的高度

    private boolean showCheckedRect;// false:不显示已选中区域框 true 显示已选中区域
    private int checkPositon;//已选中position
    private float bottomH;//底线距离底部高度
    private float lineH; //竖线高度
    private int itemWidth;//每一个时间块宽度

    private ValueAnimator anim;//移动动画
    private TimeChooseIntreface timeChooseIntreface;

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

        rectangular_x_begin = ScreenUtil.getScreenHeight(context) - chooseTimeStart_x - statusBarHeight;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
        bitmap_width = bitmap.getWidth();

        statusBarHeight = ScreenUtil.getStatusBarHeight(context);
        //圆半径
        bitmap_radius = bitmap_width / 2;

        timeViewHeigth = ScreenUtil.dip2px(context, 80);
        bottomH = ScreenUtil.dip2px(context, 5);
        lineH = timeViewHeigth / 5 * 3;

        itemWidth = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));

    }

    public void setTime(List<TimeMode> timeList) {
        this.timeList = timeList;
    }

    public void setTimeChooseMoveIntreface(TimeChooseIntreface timeChooseIntreface) {
        this.timeChooseIntreface = timeChooseIntreface;
    }


    public void setTextSpacing(int textSpacing) {
        this.textSpacing = textSpacing;
        itemWidth = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setParamInt1(int scrollView_scroll_distance) {
        this.scrollView_scroll_distance = scrollView_scroll_distance;
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
     *
     * @param show t:显示 f:隐藏
     */
    private void showSelectTimeRecr(boolean show) {
        setShowCheckedRect(show);
        /**
         * 第一次显示可选区域，回调展示选择会意思详情页面
         */

        if (show) {
            if (timeChooseIntreface != null) {
                timeChooseIntreface.showSelectedMettingRoomDetail(show);
            }
        } else {
            if (timeChooseIntreface != null) {
                timeChooseIntreface.showSelectedMettingRoomDetail(show);
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

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Log.d(TAG, "开始绘制页面");

        int h = textSpacing / 2;
        NotChoosearea notChoosearea = null;
        int spacing = (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
        chooseTimeStart_x = ScreenUtil.dip2pxf(context, chooseViewMarginLeft / 2);
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.transparencyg_reen_20));

        Paint notChoosePaint = new Paint();
        notChoosePaint.setColor(context.getResources().getColor(R.color.gray1));


        Paint paintLine = new Paint();
        paintLine.setColor(context.getResources().getColor(R.color.gray));
        paintLine.setStrokeWidth(linecWidth);
        //时间画笔
        Paint paintText = new Paint();
        paintText.setColor(context.getResources().getColor(R.color.black));
        paintText.setAntiAlias(true);
        paintText.setTextSize(ScreenUtil.dip2pxf(context, textSize));
        canvas.drawLine(0f, timeViewHeigth - bottomH, BWScreenWidth, timeViewHeigth - bottomH, paintLine);// 画底部横线
        minimum_y = ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f)));
        float maximum_to_y = 0;
        for (int i = 0; i < timeList.size(); i++) {//画item线
            TimeMode time = timeList.get(i);

            if (i % 2 == 1) {
                canvas.drawLine(ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), timeViewHeigth - bottomH - lineH, ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), timeViewHeigth - bottomH, paintLine);// 垂直item线
            } else {
                canvas.drawLine(ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), timeViewHeigth - bottomH * 5 - lineH, ScreenUtil.dip2pxf(context, (h - (textSize / 2.5f))), timeViewHeigth - bottomH, paintLine);// 垂直item线
            }
            maximum_to_y = ScreenUtil.dip2pxf(context, ((h - (textSize / 2.5f)) + textSpacing));

            paintText.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(time.time, ScreenUtil.dip2pxf(context, h + 25), timeViewHeigth - bottomH * 2 - lineH, paintText);// 画文本
            h += textSpacing;
        }
        if (isMaximum_y) {
            maximum_y = maximum_to_y - (int) Math.abs(ScreenUtil.dip2pxf(context, textSpacing));
            isMaximum_y = false;
        }

        if (isPosition) {
            isPosition = false;

            notChoose_left_x = minimum_y;
            notChoose_right_x = (minimum_y + (position * spacing)) + spacing;
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
        canvas.drawRect(notChoose_left_x, timeViewHeigth - bottomH - lineH, notChoose_right_x, timeViewHeigth - bottomH, notChoosePaint);//非选区域
        for (int i = 0; i < notChooseareaList.size(); i++) {//遍历不可选择区域
            NotChoosearea nc = notChooseareaList.get(i);
            canvas.drawRect(nc.not_choosearea_y, timeViewHeigth - bottomH - lineH, nc.not_choosearea_to_y, timeViewHeigth - bottomH, notChoosePaint);//非选区域
        }

        if (rectangular_x < notChoose_right_x) {
            paint.setColor(context.getResources().getColor(R.color.red1));
            paintLine.setColor(context.getResources().getColor(R.color.red));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button_);
        } else {
            paintLine.setColor(context.getResources().getColor(R.color.green));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_select_view_button);
        }
        for (int i = 0; i < notChooseareaList.size(); i++) {//浮框占用已经选择区域切换可选（绿色）不可选（红色）状态
            NotChoosearea nc = notChooseareaList.get(i);
            if (rectangular_to_x > nc.not_choosearea_y && rectangular_to_x < nc.not_choosearea_to_y
                    || rectangular_x > nc.not_choosearea_y && rectangular_x < nc.not_choosearea_to_y
                    || rectangular_x == nc.not_choosearea_y && rectangular_to_x == nc.not_choosearea_to_y
                    || rectangular_x < nc.not_choosearea_y && rectangular_to_x >= nc.not_choosearea_to_y
                    || rectangular_x <= nc.not_choosearea_y && rectangular_to_x > nc.not_choosearea_to_y) {
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
            canvas.drawLine(rectangular_x, timeViewHeigth - bottomH - lineH, rectangular_x, timeViewHeigth - bottomH, paintLine);

            canvas.drawRect(rectangular_x, timeViewHeigth - bottomH - lineH, rectangular_to_x, timeViewHeigth - bottomH, paint);//画矩形选择框

            canvas.drawLine(rectangular_to_x, timeViewHeigth - bottomH - lineH, rectangular_to_x, timeViewHeigth - bottomH, paintLine);
            //获取按钮初始位置
            butBottomCircle_y = rectangular_to_x - bitmap_radius;
            paint.setColor(context.getResources().getColor(R.color.white));
            canvas.drawBitmap(bitmap, butBottomCircle_y, timeViewHeigth - bottomH - lineH / 3 * 2, paint);//按钮2
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
                recordCenterY2 = (butBottomCircle_y + bitmap_radius - scrollView_scroll_distance);

                rectangular_x_begin = (rectangular_x - scrollView_scroll_distance);
                rectangular_x_end = (rectangular_to_x - scrollView_scroll_distance);
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
                    rectangular_spacing = Math.abs(rectangular_to_x - rectangular_x);
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:

                Log.d(TAG, "MOVE");

                moveRawX = event.getRawX() - statusBarHeight + scrollView_scroll_distance;
                float mobile = (moveRawX - bitmap_radius);

                if (isMoveButton2) {//按钮2移动

                    if (mobile < maximum_y) {
                        if (itemWidth < (int) (moveRawX - rectangular_x)) {
                            butBottomCircle_y = mobile;
                            rectangular_to_x = moveRawX;
                        }
                    }
                } else if (isMoveRectangular) {//矩形选择框移动
                    float rectangular_mobile = rectangular_x_begin + (moveRawX - clickRawX);
                    float rectangular_to_mobile = rectangular_x_end + (moveRawX - clickRawX);

                    if (rectangular_mobile > minimum_y && rectangular_to_mobile < maximum_y) {
                        rectangular_to_x = rectangular_to_mobile;
                        rectangular_x = rectangular_mobile;
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
                        move2Click(itemWidth);

                    } else {//未选中，选中点击取
                        showSelectTimeRecr(true);

                        checkPositon = click2Position(currentX, currentY);

                        if (isShowCheckedRect()) {
                            rectangular_x = (minimum_y + (checkPositon * itemWidth));
                            rectangular_to_x = (minimum_y + (checkPositon * itemWidth)) + itemWidth;
                        }
                    }

                }

                if (isMoveButton2) {//按钮2移动
                    float mobileY = recordCenterY2;
                    float theOffset = ((moveRawX - scrollView_scroll_distance - clickRawX) / itemWidth);
                    int mobileNumber = Math.abs((int) theOffset);
                    float theOffset1 = (theOffset - mobileNumber);
                    if (theOffset > 0) {//下移
                        float spacing1 = 0;

                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 + itemWidth * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 + itemWidth;
                        }
                        if (theOffset1 < 0.5) {
                            spacing1 -= itemWidth;
                        }
                        mobileY = (spacing1 + scrollView_scroll_distance);
                    } else {//上移
                        float spacing1 = 0;
                        if (mobileNumber >= 1) {
                            spacing1 = recordCenterY2 - itemWidth * (mobileNumber + 1);
                        } else {
                            spacing1 = recordCenterY2 - itemWidth;
                        }
                        if (theOffset1 > 0.5) {
                            mobileY = (spacing1 + scrollView_scroll_distance);
                        } else {
                            mobileY = (spacing1 + itemWidth + scrollView_scroll_distance);
                        }
                        if ((int) spacing1 <= (int) rectangular_x_begin) {
                            mobileY = rectangular_x + itemWidth;
                        }
                    }
                    if (mobileY < maximum_y) {
                        rectangular_to_x = mobileY;
                    } else {
                        rectangular_to_x = maximum_y;
                    }
                }
                if (isMoveRectangular) {//矩形选择框移动
                    float rectangularMobile = Math.round((rectangular_x - minimum_y) / itemWidth);
                    int rectangularNumber = Math.abs((int) rectangularMobile);
                    float mobileY1 = rectangularNumber * itemWidth + minimum_y;
                    rectangular_x = mobileY1;
                    rectangular_to_x = mobileY1 + rectangular_spacing;
                }

                List<String> startTime = new ArrayList<>();
                int start = (int) ((rectangular_x - minimum_y) / itemWidth);
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
                int end = (int) ((rectangular_to_x - minimum_y) / itemWidth);
                String endTimeStr;
                if (end % 2 == 0) {

                    if (end >= timeList.size()) {
                        end = timeList.size() - 1;
                    }
                    endTimeStr = timeList.get(end).time;
                } else {
                    StringTokenizer st = new StringTokenizer(timeList.get((end - 1)).time, ":");
                    while (st.hasMoreElements()) {
                        endTime.add(st.nextToken());
                    }
                    endTimeStr = endTime.get(0) + ":30";
                }
                if( timeChooseIntreface != null){
                    timeChooseIntreface.getSelectedTime(startTimeStr, endTimeStr);
                }

                break;
        }


        return true;
    }

    private float[] click2PositionCoordinate(float x) {
        long textSpacingPx = ScreenUtil.dip2px(context, textSpacing);
        float[] coordinate = new float[2];
        coordinate[0] = (x - chooseTimeStart_x) / textSpacingPx * textSpacingPx;
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
        clickPosition = (int) ((rawx - chooseTimeStart_x) / textSpacingPx);
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

    public void setWidthHeight(int width, int height, int itemSize) {
        this.BWScreenHeight = height;
        this.BWScreenWidth = width;
        this.itemSize = itemSize;
    }

    public interface OnTouchListener {
        void onTouch(MotionEvent event, boolean isChoose);
    }


    class NotChoosearea {
        public float not_choosearea_y;
        public float not_choosearea_to_y;

    }

    private void move2Click(float spacing) {

        if (anim != null && anim.isRunning()) return;

        float rectangular_y1 = (minimum_y + (checkPositon * spacing));
        int lastCheckPosition = click2Position(rectangular_x, 0);
        int positionDiatance = Math.abs(checkPositon - lastCheckPosition);

        int checkItemSize = (int) ((rectangular_x_end - rectangular_x_begin) / spacing);
        if (checkPositon + positionDiatance == itemSize) {//已选区域已经覆盖到右边最大值
            return;

        } else if (checkPositon + checkItemSize > itemSize) {//已选区域最多移动到右边最大值
            checkPositon = itemSize - checkItemSize;
            rectangular_y1 = (minimum_y + (checkPositon * spacing));
        }

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

        anim = ValueAnimator.ofFloat(rectangular_x, rectangular_y1);
        anim.setDuration(millisInFuture);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                rectangular_x = currentValue;
                rectangular_to_x = currentValue + rectangular_x_end - rectangular_x_begin;

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

        if (anim != null && anim.isRunning()) return false;
        boolean addSuccess;


        scroll2LeftOnePosition();
        int itemCount = (int) (rectangular_to_x - rectangular_x) / itemWidth;
        Log.d(TAG, "当前item数量：" + itemCount + "开始值" + rectangular_to_x + " 结束值：" + (rectangular_x));
        if (itemCount > 1) {//当前状态可进行item递减
            addSuccess = true;

            if (anim != null && anim.isRunning()) return false;

            Log.d(TAG, "准备偏移  开始值" + rectangular_to_x + " 结束值：" + (rectangular_to_x - itemWidth) + " spacing:" + itemWidth);
            anim = ValueAnimator.ofFloat(rectangular_to_x, rectangular_to_x - itemWidth);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    rectangular_to_x = currentValue;

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

        if (anim != null && anim.isRunning()) return false;
        scroll2RightOnePosition();


        if (!isShowCheckedRect()) {
            showSelectTimeRecr(true);
        }

        int maxPosition = BWScreenWidth / itemWidth;//当前view最大item下标
        int currentSellectMaxPosition = (int) rectangular_to_x / itemWidth;

        boolean removeSuccess;

        Log.d(TAG, "max item position index:" + maxPosition);
        Log.d(TAG, "current max item position index:" + currentSellectMaxPosition);

        if (currentSellectMaxPosition < maxPosition) {//当前状态可进行item的赠

            Log.d(TAG, "=====================================================");
            Log.d(TAG, "准备偏移  开始值" + rectangular_to_x + " 结束值：" + (rectangular_to_x + itemWidth) + " spacing:" + itemWidth);

            anim = ValueAnimator.ofFloat(rectangular_to_x, rectangular_to_x + itemWidth);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    rectangular_to_x = currentValue;

                    invalidate();

                    Log.d(TAG, "cuurent value is " + currentValue);
                }
            });
            anim.start();

            removeSuccess = true;
        } else {
            removeSuccess = false;
        }

        return removeSuccess;
    }


    public boolean move2NextPosition() {

        if (anim != null && anim.isRunning()) return false;
        scroll2RightOnePosition();

        int maxPosition = BWScreenWidth / itemWidth;//当前view最大item下标
        int currentSellectMaxPosition = (int) rectangular_to_x / itemWidth;

        boolean removeSuccess;

        Log.d(TAG, "max item position index:" + maxPosition);
        Log.d(TAG, "current max item position index:" + currentSellectMaxPosition);

        if (currentSellectMaxPosition < maxPosition) {//当前状态可进行item的赠

            Log.d(TAG, "=====================================================");
            Log.d(TAG, "准备偏移  开始值" + rectangular_to_x + " 结束值：" + (rectangular_to_x + itemWidth) + " spacing:" + itemWidth);

            anim = ValueAnimator.ofFloat(rectangular_x, rectangular_x + itemWidth);
            anim.setDuration(300);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    rectangular_x = currentValue;
                    rectangular_to_x = currentValue + rectangular_x_end - rectangular_x_begin;
                    invalidate();


                    Log.d(TAG, "cuurent value is " + currentValue);
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
    private void scroll2LeftOnePosition() {

        float screenLeft_x = getScreenLeft_x();
        int screenLeftPosition = (int) screenLeft_x / itemWidth;
        int currentMaxPosition = (int) rectangular_to_x / itemWidth;

        if (currentMaxPosition == screenLeftPosition || currentMaxPosition == (screenLeftPosition + 1)) {//进行北京页面滚动
            Log.d(TAG, "移动到左边某个位置");
            if (timeChooseIntreface != null) {
                timeChooseIntreface.timeChooseMove(getViewHolder(), false, itemWidth);
            }
        }


    }


    private void scroll2RightOnePosition() {

        float screenRight = getScreenRight_x();
        int screenRightPosition = (int) screenRight / itemWidth;
        int currentMaxPosition = (int) rectangular_to_x / itemWidth;

        if (currentMaxPosition == screenRightPosition || currentMaxPosition == (screenRightPosition - 1)) {
            Log.d(TAG, "移动到右边某个位置");
            if (timeChooseIntreface != null) {
                timeChooseIntreface.timeChooseMove(getViewHolder(), true, itemWidth);
            }
        }

    }

    /**
     * 返回屏幕右边框距离scrollView左边的x
     *
     * @return
     */
    private float getScreenRight_x() {
        return scrollView_scroll_distance + ScreenUtil.getScreenWidth(context);
    }

    /**
     * 返回屏幕左边框距离scrollview左边的距离
     *
     * @return
     */
    private float getScreenLeft_x() {
        return scrollView_scroll_distance;
    }


    /**
     * view移动接口
     */
    public interface TimeChooseIntreface {

        /**
         * 移动回调
         *
         * @param direction 移动距离
         * @param distance  t:移动到右边某个位置 f:移动到左边某个位置
         */
        void timeChooseMove(Object viewHolder, boolean direction, float distance);

        /**
         * 隐藏/显示会议室已选详情布局
         *
         * @param show t:显示详情布局 f:隐藏详情布局
         */
        void showSelectedMettingRoomDetail(boolean show);

        /**
         * 返回当前选中的其实时间与结束时间
         * @param startTimeStr
         * @param endTimeStr
         */
        void getSelectedTime(String startTimeStr, String endTimeStr);

    }

    /**
     * 当选择框移动到屏幕边缘父ScrollView进行滚动
     */
    private void moveScrollViewOnTouch() {
        float screenRight = getScreenRight_x();
        float screenleft = getScreenLeft_x();
        if (timeChooseIntreface != null && (rectangular_to_x > screenRight)) {
            timeChooseIntreface.timeChooseMove(getViewHolder(), true, itemWidth);
            move2NextPosition();
        }
        if (timeChooseIntreface != null && (rectangular_to_x < screenleft)) {
            timeChooseIntreface.timeChooseMove(getViewHolder(), true, -itemWidth);
            removePick();
        }
    }


}
