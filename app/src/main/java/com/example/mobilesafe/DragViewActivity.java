package com.example.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by sing on 14-1-14.
 * desc:
 */
public class DragViewActivity extends Activity {

    public static final String TAG = "DragViewActivity";

    private SharedPreferences sp;

    private View rl_drag_view;
    private ImageView iv_drag_view;
    private TextView tv_drag_view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragview_layout);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        rl_drag_view = findViewById(R.id.rl_drag_view);
        iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
        tv_drag_view = (TextView) findViewById(R.id.tv_drag_view);

        InitControls();

        iv_drag_view.setOnTouchListener(new View.OnTouchListener() {

            int startx = 0;
            int starty = 0;
            int parentLeft = 0;
            int parentTop = 0;
            int parentRight = 0;
            int parentBottom = 0;

            int boundaryHeight = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "按下");
                        startx = (int) motionEvent.getRawX();
                        starty = (int) motionEvent.getRawY();

                        parentLeft = rl_drag_view.getLeft();
                        parentTop = rl_drag_view.getTop();
                        parentRight = rl_drag_view.getRight();
                        parentBottom = rl_drag_view.getBottom();

                        boundaryHeight = (parentBottom - parentTop) / 2;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) motionEvent.getRawX();
                        int y = (int) motionEvent.getRawY();
                        int dx = x - startx;
                        int dy = y - starty;
                        int offsetX = 0;
                        int offsetY = 0;
                        int l = iv_drag_view.getLeft() + dx;
                        int t = iv_drag_view.getTop() + dy;
                        int r = iv_drag_view.getRight() + dx;
                        int b = iv_drag_view.getBottom() + dy;

                        //边界判断及微调
                        if (l < parentLeft) {
                            offsetX = parentLeft - l;
                        }
                        if (r > parentRight) {
                            offsetX = parentRight - r;
                        }
                        if (t < parentTop) {
                            offsetY = parentTop - t;
                        }
                        if (b > parentBottom) {
                            offsetY = parentBottom - b;
                        }

                        int newLeft = l + offsetX;
                        int newTop = t + offsetY;
                        int newRight = r + offsetX;
                        int newBottom = b + offsetY;
                        //iv_drag_view.layout(newLeft, newTop, newRight, newBottom);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = newLeft;
                        params.topMargin = newTop;
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                        iv_drag_view.setLayoutParams(params);

                        if (newTop + (newBottom - newTop) / 2 >= boundaryHeight) {
                            setTipOnMidTop();
                        } else {
                            setTipOnMidBottom();
                        }

                        startx = x;
                        starty = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "松开");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("showCallPosX", iv_drag_view.getLeft());
                        editor.putInt("showCallPosY", iv_drag_view.getTop());
                        editor.commit();
                        break;
                }

                return true;
            }
        });
    }

    private void InitControls() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
//        int parentLeft = rl_drag_view.getLeft();
//        int parentTop = rl_drag_view.getTop();
//        int parentRight = rl_drag_view.getRight();
//        int parentBottom = rl_drag_view.getBottom();
        int boundaryHeight = height / 2;

        int l = tv_drag_view.getLeft();
        int t = tv_drag_view.getTop();
        int r = tv_drag_view.getRight();
        int b = tv_drag_view.getBottom();

        int midX = (width - (r - l)) / 2;
        int midY = (height - (b - t)) / 2;
        int showCallPosX = sp.getInt("showCallPosX", midX);
        int showCallPosY = sp.getInt("showCallPosY", midY);
        if (showCallPosX < 0) {
            showCallPosX = 0;
        }
        if (showCallPosX > width) {
            showCallPosX = width;
        }
        if (showCallPosY < 0) {
            showCallPosY = 0;
        }
        if (showCallPosY > height) {
            showCallPosY = height;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_drag_view.getLayoutParams();
        params.leftMargin = showCallPosX;
        params.topMargin = showCallPosY;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        iv_drag_view.setLayoutParams(params);

        if (showCallPosY + (b - t) / 2 >= boundaryHeight) {
            setTipOnMidTop();
        } else {
            setTipOnMidBottom();
        }
    }

    private void setTipOnMidTop() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        tv_drag_view.setLayoutParams(params);
    }

    private void setTipOnMidBottom() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        tv_drag_view.setLayoutParams(params);
    }
}