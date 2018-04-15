package org.tootto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.tootto.R;

public class MastodonReblogButton extends View {
    final String DEBUG_TAG = "DEBUG_TAG";
    private PathMeasure mPathMeasure;
    Path path, pathTriangle, pathTriangleRight, pathTrans, pathTransRight;
    Paint paint, paintTriangle, paintTrans;
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private int mWidth, mHeight;
    float mAnimatorValue;
    float rectWidth, rectHeight;
    float triangleWidth, triangleHeight;
    float offset, offsetTrans;
    Xfermode xfermode;
    float strokeWidth, roundCornerHeight, sweepAngle;
    boolean FLAG_SELECTED;
    ValueAnimator valueAnimator;
    private SparkEventListener mListener;
    private int viewBackgroundColor;
    public void setEventListener(SparkEventListener listener){
        this.mListener = listener;
    }
    public static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    public MastodonReblogButton(Context context) {
        super(context);
    }

    public MastodonReblogButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        pathTriangle = new Path();
        pathTriangleRight = new Path();
        pathTrans = new Path();
        pathTransRight = new Path();
        paint = new Paint();
        paintTriangle = new Paint();
        paintTrans = new Paint();
        mPathMeasure = new PathMeasure();

        Drawable background = getBackground();
        if (background instanceof ColorDrawable){
            ColorDrawable colorDrawable = (ColorDrawable) background;
            int color = colorDrawable.getColor();
            viewBackgroundColor = color;
        }else if (background instanceof BitmapDrawable){
            throw new AssertionError("you can't set a bitmap as background ");
        }else {
            viewBackgroundColor = getResources().getColor(R.color.colorBackground);
        }
        //创建一个值从0到xxx的动画
        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(1200);
        //每过10毫秒 调用一次
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                paint.setColor(getResources().getColor(R.color.colorBlue));
                paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public MastodonReblogButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        FLAG_SELECTED = false;
        mWidth = w;
        mHeight = h;
        rectWidth = mWidth * 12 / 42;
        rectHeight =  mHeight* 9 / 36;
        triangleWidth = mWidth * 18 / 42;
        triangleHeight = mHeight * 12 / 36;
        offset = mHeight   / 36 ;
        offsetTrans = mHeight * 3 / 36;
        strokeWidth = mWidth * 6 / 42;
        roundCornerHeight = strokeWidth/2;//矩形的圆角促使矩形缩短的距离
        sweepAngle = 45;//矩形的圆角划过的角度

        //绘制圆角矩形
        path.moveTo(-rectWidth, -offset);//左侧中间点
        path.lineTo(-rectWidth, -(rectHeight-strokeWidth));
        RectF rectF = new RectF(-rectWidth, -rectHeight, -(rectWidth - 2 * roundCornerHeight), -(rectHeight - 2 * roundCornerHeight));
        path.arcTo(rectF, -180 + sweepAngle/2, sweepAngle, false);
        path.lineTo(-(rectWidth-roundCornerHeight), -rectHeight );
        path.lineTo(rectWidth - roundCornerHeight, -rectHeight);
        RectF rectRightTop = new RectF(rectWidth - 2 * roundCornerHeight, -rectHeight, rectWidth, -(rectHeight - 2 * roundCornerHeight));
        path.arcTo(rectRightTop, -90 + sweepAngle/2, sweepAngle);
        path.lineTo(rectWidth, rectHeight - roundCornerHeight);
        RectF rectRightBottom = new RectF(rectWidth - 2 * roundCornerHeight, rectHeight - 2 * roundCornerHeight, rectWidth, rectHeight);
        path.arcTo(rectRightBottom, sweepAngle/2,sweepAngle,false);
        path.lineTo(-(rectWidth-roundCornerHeight), rectHeight);
        RectF rectLeftBottom = new RectF(-rectWidth, rectHeight - 2 * roundCornerHeight, -(rectWidth - 2 * roundCornerHeight), rectHeight);
        path.arcTo(rectLeftBottom, 90 + sweepAngle/2+20, sweepAngle, false);
        path.lineTo(-rectWidth, -offset);

        mPathMeasure.setPath(path, true);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(getResources().getColor(R.color.colorGray));

//        //绘制左侧背景色条
        pathTrans.moveTo(0,-triangleWidth/2*1.2f);
        pathTrans.lineTo(triangleHeight*1.2f,0);
        pathTrans.lineTo(0,triangleWidth/2*1.2f);
        pathTrans.lineTo(0,-triangleWidth/2*1.2f);
        paintTrans.setColor(viewBackgroundColor);
        paintTrans.setStyle(Paint.Style.FILL);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
        paintTrans.setXfermode(xfermode);

//        //绘制左侧三角形
        paintTriangle.setStyle(Paint.Style.FILL);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(offsetTrans);//圆角
        paintTriangle.setPathEffect(cornerPathEffect);
        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
        pathTriangle.lineTo(0,-triangleWidth / 2);
        pathTriangle.lineTo(triangleHeight,0);
        pathTriangle.lineTo(0,triangleWidth / 2);
        pathTriangle.close();

        //绘制右侧三角形
        pathTriangleRight.lineTo(0,-triangleWidth/2);
        pathTriangleRight.lineTo(-triangleHeight,0);
        pathTriangleRight.lineTo(0,triangleWidth/2);
        pathTriangleRight.close();

        //绘制右侧背景色条
        pathTransRight.moveTo(0,-triangleWidth/2*1.2f);
        pathTransRight.lineTo(-triangleHeight*1.2f,0);
        pathTransRight.lineTo(0,triangleWidth/2*1.2f);
        pathTransRight.lineTo(0,-triangleWidth/2*1.2f);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(240,200);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(240, heightSpecSize);
        }else if(heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize, 200);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPathMeasure.getPosTan(mAnimatorValue * mPathMeasure.getLength()/2, pos, tan);

        canvas.drawColor(viewBackgroundColor);
        canvas.save();
        canvas.translate(mWidth/2, mHeight/2);//坐标系原点切到控件1/2处
        canvas.drawPath(path, paint);

        float degree = (float) (Math.atan2(tan[1], tan[0])*180.0/ Math.PI);
        //坐标系移动到左侧的path起点
        canvas.translate(pos[0],pos[1]);
        //画布旋转趋势与x轴的夹角
        canvas.rotate(degree);
        int i = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangle, paintTriangle);
        canvas.drawPath(pathTrans, paintTrans);
        canvas.restoreToCount(i);
        //画布画布转回原来的夹角
        canvas.rotate(-degree);
        //坐标系移动到原点
        canvas.translate(-pos[0], -pos[1]);
        //坐标系移动到右侧的path起点
        canvas.translate(-pos[0], -pos[1]);
        //画布旋转趋势与x轴的夹角
        canvas.rotate(degree);
        int j = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(pathTriangleRight, paintTriangle);
        canvas.drawPath(pathTransRight, paintTrans);
        canvas.restoreToCount(j);
        canvas.restore();
    }

    void setSelected(){
        paint.setColor(getResources().getColor(R.color.colorBlue));
        paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
        postInvalidate();
    }
    void setUnSelected(){
        paint.setColor(getResources().getColor(R.color.colorGray));
        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
        postInvalidate();
    }
    void startMove(){
        valueAnimator.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){
            case (MotionEvent.ACTION_DOWN) :
                animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).setInterpolator(DECELERATE_INTERPOLATOR);
                //防止连点
                if (valueAnimator.isRunning()){
                    return false;
                }
                setPressed(true);
                postInvalidate();
                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_UP) :
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(DECELERATE_INTERPOLATOR);
                if (isPressed()){
                    setPressed(false);
                }
                //对抬起时的区域判断
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    if (FLAG_SELECTED) {
                        //区域内, 选中变色未选中
                        paint.setColor(getResources().getColor(R.color.colorGray));
                        paintTriangle.setColor(getResources().getColor(R.color.colorGray));
                        postInvalidate();
                        FLAG_SELECTED = false;

                    } else {
                        //区域内, 未选中开始动画
                        paint.setColor(getResources().getColor(R.color.colorBlue));
                        paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
                        FLAG_SELECTED = true;
                        startMove();
                    }
                }else {
                    //区域外, 回到原来颜色, 无效点击
                   if (FLAG_SELECTED){
                       paint.setColor(getResources().getColor(R.color.colorBlue));
                       paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
                       postInvalidate();
                   }else {
                       paint.setColor(getResources().getColor(R.color.colorGray));
                       paintTriangle.setColor(getResources().getColor(R.color.colorGray));
                       postInvalidate();
                   }
                }
                if (mListener!=null) {
                    mListener.onFingerUp(FLAG_SELECTED);
                }
                break;
            case (MotionEvent.ACTION_CANCEL) :
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(DECELERATE_INTERPOLATOR);
                if (FLAG_SELECTED){
                    paint.setColor(getResources().getColor(R.color.colorBlue));
                    paintTriangle.setColor(getResources().getColor(R.color.colorBlue));
                    postInvalidate();
                }else {
                    paint.setColor(getResources().getColor(R.color.colorGray));
                    paintTriangle.setColor(getResources().getColor(R.color.colorGray));
                    postInvalidate();
                }
                break;
        }
        return true;
    }

    //添加页面被移除时的动画取消, 防止内存泄漏
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null && valueAnimator.isRunning()){
            valueAnimator.cancel();
        }
        animate().cancel();
    }

    public interface SparkEventListener{
        void onFingerUp(boolean flag);
    }
}
