package com.longhuapuxin.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.Expression.ExpressionNode;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.U5Application;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZH on 2016/1/26.
 * Email zh@longhuapuxin.com
 */
public class ExpressionTextView extends TextView {

    private CharSequence text;
    private Context mContext;

    public ExpressionTextView(Context context) {
        this(context, null, 0);
        mContext = context;
    }

    public ExpressionTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public ExpressionTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }


    @Override
    public CharSequence getText() {
        return text == null ? "" : text;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;
        U5Application u5App = (U5Application)(getContext().getApplicationContext());
        List<ExpressionNode> expressionList = u5App.getExpressionList();

        if (expressionList == null) {
            super.setText(text, type);
        } else {
            String cs = text.toString();
            String patternString = "\\[(";
            for(ExpressionNode item : expressionList) {
                patternString += item.getName();
                patternString += "|";
            }

            patternString = patternString.substring(0, patternString.length() - 1);
            patternString += ")\\]";

            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(cs);

            SpannableString ss = new SpannableString(cs);
            while(matcher.find()) {

                String expressionName = matcher.group(1);
                try {
                    Field f = R.drawable.class.getDeclaredField(expressionName);
                    int resId = f.getInt(R.drawable.class);
                    Drawable drawable = getResources().getDrawable(resId);
                    int wPx = Utils.dip2px(mContext, 20);
                    int hPx = Utils.dip2px(mContext, 20);
                    drawable.setBounds(0, 0, wPx, hPx);
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    ss.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception  e) {
                    e.printStackTrace();
                }
            }

            super.setText(ss, type);
        }
    }
}
