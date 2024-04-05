package lib.kalu.fileselector.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.cursor.TreeCursorLoader;

/**
 * description: 相册模型
 * create by kalu on 2020-03-25
 */
@Keep
public class AlbumModel implements Serializable {

    public static final String ALBUM_ID_ALL = String.valueOf(-1);
    public static final String ALBUM_NAME_ALL = "All";

    // fragmentByTags
    private List<String> fragmentByTags;
    // 相册id
    private String mAlbumId;
    // 相册封面
    private String mAlbumUriString;
    // 相册名称
    private String mAlbumName;
    // 相册数量
    private long mAlbumNum;

    public AlbumModel(String id, Uri coverUri, String albumName, long count) {
        this.mAlbumId = id;

        mAlbumUriString = coverUri.toString();
        mAlbumName = albumName;
        mAlbumNum = count;
    }

    public static AlbumModel valueOf(Cursor cursor) {
        String clumn = cursor.getString(cursor.getColumnIndex(TreeCursorLoader.COLUMN_URI));
        return new AlbumModel(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                Uri.parse(clumn != null ? clumn : ""),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(TreeCursorLoader.COLUMN_COUNT)));
    }

    public String getId() {
        return mAlbumId;
    }

    public String getAlbumUriString() {
        return mAlbumUriString;
    }

    public long getAlbumNum() {
        return mAlbumNum;
    }

    public void addAlbumNum() {
        mAlbumNum++;
    }

    public String getAlbumName(Context context) {
        if (isAll()) {
            return context.getString(R.string.lib_fs_string_all);
        } else if (TextUtils.isEmpty(mAlbumName)) {
            return context.getString(R.string.lib_fs_string_unnamed);
        } else {
            return mAlbumName;
        }
    }

    public boolean isAll() {
        return ALBUM_ID_ALL.equals(mAlbumId);
    }

    public boolean isEmpty() {
        return mAlbumNum == 0;
    }

    public List<String> getFragmentByTags() {
        return fragmentByTags;
    }

    public void setFragmentByTags(List<String> fragmentByTags) {
        this.fragmentByTags = fragmentByTags;
    }
}