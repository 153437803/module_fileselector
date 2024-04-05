package lib.kalu.fileselector.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.model.AlbumModel;
import lib.kalu.fileselector.util.LogUtil;

public class AlbumsSpinner {

    private static final int MAX_SHOWN_COUNT = 6;
    private CursorAdapter mAdapter;
    private TextView mSelected;
    private ListPopupWindow mListPopupWindow;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    public AlbumsSpinner(@NonNull Context context) {
        mListPopupWindow = new ListPopupWindow(context, null, R.attr.listPopupWindowStyle);
        mListPopupWindow.setModal(true);
        float density = context.getResources().getDisplayMetrics().density;
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        mListPopupWindow.setContentWidth(widthPixels);
        mListPopupWindow.setHorizontalOffset((int) (16 * density));
//        mListPopupWindow.setVerticalOffset((int) (-48 * density));

        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumsSpinner.this.onItemSelected(parent.getContext(), position);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(parent, view, position, id);
                }
            }
        });
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelection(Context context, int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(context, position);
    }

    private void onItemSelected(Context context, int position) {
        mListPopupWindow.dismiss();
        if (null != mSelected) {
            Cursor cursor = mAdapter.getCursor();
            cursor.moveToPosition(position);
            AlbumModel albumModel = AlbumModel.valueOf(cursor);
            String displayName = albumModel.getAlbumName(context);
            mSelected.setText(displayName);
        }
    }

    public void setAdapter(CursorAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        if (null != mSelected) {
            //        // tint dropdown arrow icon
//        Drawable[] drawables = mSelected.getCompoundDrawables();
//        Drawable right = drawables[2];
//        TypedArray ta = mSelected.getContext().getTheme().obtainStyledAttributes(
//                new int[]{R.attr.album_element_color});
//        int color = ta.getColor(0, 0);
//        ta.recycle();
//        right.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            mSelected.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        int itemHeight = v.getResources().getDimensionPixelSize(R.dimen.fs_d72);
                        mListPopupWindow.setHeight(
                                mAdapter.getCount() > MAX_SHOWN_COUNT ? itemHeight * MAX_SHOWN_COUNT
                                        : itemHeight * mAdapter.getCount());
                        mListPopupWindow.show();
                        mListPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    } catch (Exception e) {
                        LogUtil.logE("AlbumsSpinner => setSelectedTextView => ");
                    }
                }
            });
            // mSelected.setOnTouchListener(mListPopupWindow.createDragToOpenListener(mSelected));
        }
    }

    public void setPopupAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }

}
