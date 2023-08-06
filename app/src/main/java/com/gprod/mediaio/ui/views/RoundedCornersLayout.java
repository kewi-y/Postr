package com.gprod.mediaio.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundedCornersLayout extends FrameLayout {
    private float cornerRadius;
    private Path path = new Path();
    public RoundedCornersLayout(@NonNull Context context) {
        super(context);
    }

    public RoundedCornersLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedCornersLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setCornerRadius(float cornerRadius){
        this.cornerRadius = cornerRadius;
        invalidate();
    }
    @Override
    public void draw(Canvas canvas) {
        if(cornerRadius != 0) {
            path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.clipPath(path);
        }
        super.draw(canvas);
    }
}
