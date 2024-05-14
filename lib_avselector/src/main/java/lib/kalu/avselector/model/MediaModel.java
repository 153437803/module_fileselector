package lib.kalu.avselector.model;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * description: 图片模型
 * create by kalu on 2020-03-25
 */
@Keep
public class MediaModel implements Serializable {

    public static final long ITEM_ID_CAPTURE = -1;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "capture";

    // 图片id
    public long mMediaId;
    // 图片类型
    public String mMediaType;
    // 图片路径
    public String mMediaUriString;
    // 图片大小
    public long mMediaSize;
    // 视频时长
    public long mMediaDuration; // only for video, in ms

    private MediaModel(long id, String mimeType, long size, long duration) {
        this.mMediaId = id;
        this.mMediaType = mimeType;
        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // ?
            contentUri = MediaStore.Files.getContentUri("external");
        }

        Uri uri = ContentUris.withAppendedId(contentUri, id);
        this.mMediaUriString = uri.toString();
        this.mMediaSize = size;
        this.mMediaDuration = duration;
    }

    public static MediaModel valueOf(Cursor cursor) {
        return new MediaModel(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    public String getMediaUriString() {
        return mMediaUriString;
    }

    public boolean isCapture() {
        return mMediaId == ITEM_ID_CAPTURE;
    }

    public boolean isImage() {
        return mMediaType.startsWith("image/");
    }

    public boolean isGif() {
        return mMediaType.equals("image/gif");
    }

    public boolean isVideo() {
        return mMediaType.startsWith("video/");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MediaModel)) {
            return false;
        }

        MediaModel other = (MediaModel) obj;
        return mMediaId == other.mMediaId
                && (mMediaType != null && mMediaType.equals(other.mMediaType)
                || (mMediaType == null && other.mMediaType == null))
                && (mMediaUriString != null && mMediaUriString.equals(other.mMediaUriString)
                || (mMediaUriString == null && other.mMediaUriString == null))
                && mMediaSize == other.mMediaSize
                && mMediaDuration == other.mMediaDuration;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(mMediaId).hashCode();
        if (mMediaType != null) {
            result = 31 * result + mMediaType.hashCode();
        }
        result = 31 * result + mMediaUriString.hashCode();
        result = 31 * result + Long.valueOf(mMediaSize).hashCode();
        result = 31 * result + Long.valueOf(mMediaDuration).hashCode();
        return result;
    }
}
