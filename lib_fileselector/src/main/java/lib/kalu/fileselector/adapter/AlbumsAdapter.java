package lib.kalu.fileselector.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.model.AlbumModel;

/**
 * description: 相册显示适配器
 * create by Administrator on 2020-03-26
 */
public class AlbumsAdapter extends CursorAdapter {

    private final Drawable mPlaceholder;

    public AlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    public AlbumsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.lib_fs_dialog_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        AlbumModel model = AlbumModel.valueOf(cursor);

        // 相册名称
        String albumName = model.getAlbumName(context);
        ((TextView) view.findViewById(R.id.album_name)).setText(albumName);

        // 相册数量
        long albumNum = model.getAlbumNum();
        String albumNums = String.valueOf(albumNum);
        ((TextView) view.findViewById(R.id.album_media_count)).setText(albumNums);

        // 相册封面
        String albumUriString = model.getAlbumUriString();
        SelectorModel.getInstance().baseImageload.loadThumbnail(context, context.getResources().getDimensionPixelSize(R
                        .dimen.fs_d4), mPlaceholder,
                view.findViewById(R.id.album_cover), albumUriString);
    }
}
