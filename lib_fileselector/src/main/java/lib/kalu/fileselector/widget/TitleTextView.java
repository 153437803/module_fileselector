package lib.kalu.fileselector.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TitleTextView extends AppCompatTextView {

    public TitleTextView(Context context) {
        super(context);
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        CharSequence text = getText();
        if (TextUtils.isEmpty(text))
            return;

        float measureText = getPaint().measureText(text.toString());
        int width = getWidth();
        int height = getHeight();

        TextPaint paint = getPaint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);

        Path path = new Path();
        path.moveTo(width / 2 + measureText / 2 + height / 5, height * 9 / 20);// 此点为多边形的起点
        path.lineTo(width / 2 + measureText / 2 + height / 3, height * 9 / 20);
        path.lineTo(width / 2 + measureText / 2 + height / 4, height * 11 / 20);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, paint);
    }
}
