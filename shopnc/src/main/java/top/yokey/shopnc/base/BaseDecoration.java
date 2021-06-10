package top.yokey.shopnc.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView分割线
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseDecoration extends RecyclerView.ItemDecoration {

    public final int[] ATRRS = new int[]{android.R.attr.listDivider};
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;
    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

    private Context context;
    private Drawable divider;

    private int orientation;

    public BaseDecoration(Context context, int orientation) {

        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(ATRRS);
        this.divider = typedArray.getDrawable(0);
        typedArray.recycle();
        setOrientation(orientation);

    }

    public void setOrientation(int orientation) {

        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        orientation = orientation;

    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {

        if (orientation == HORIZONTAL) {
            drawVerticalLine(canvas, recyclerView, state);
        } else {
            drawHorizontalLine(canvas, recyclerView, state);
        }

    }

    public void drawHorizontalLine(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {

        int left = recyclerView.getPaddingLeft();
        int right = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }

    }

    public void drawVerticalLine(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {

        int top = recyclerView.getPaddingTop();
        int bottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + divider.getIntrinsicWidth();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }

    }

    @Override
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {

        if (orientation == HORIZONTAL) {
            rect.set(0, 0, 0, 1);
        } else {
            rect.set(0, 0, 1, 0);
        }

    }

}
