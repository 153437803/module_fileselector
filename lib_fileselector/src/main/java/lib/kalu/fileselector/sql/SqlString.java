package lib.kalu.fileselector.sql;

import android.net.Uri;
import android.provider.MediaStore;

public class SqlString {

    // simple

    public static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    public static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    public static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};

    // simple

    // all
    public static final String SELECTION_ALBUM_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static final String[] getSelectionAlbumAll() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    }

    public static final String SELECTION_BUCKETID_All =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionBucketidAll(String bucketId) {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), bucketId};
    }

    // 只选图片, 不包含gif

    public static final String SELECTION_ALBUM_FOR_IMAGE_NO_GIF =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND ("
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionAlbumArgsForImageNoGifType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), "image/jpeg", "image/png", "image/x-ms-bmp", "image/webp"};
    }

    public static final String SELECTION_BUCKETID_IMAGE_NO_GIF =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND ("
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionBucketidImageNoGif(String bucketId) {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), bucketId, "image/jpeg", "image/png", "image/x-ms-bmp", "image/webp"};
    }

    // 只选图片, 包含gif

    public static final String SELECTION_ALBUM_FOR_IMAGE_All =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionAlbumArgsForImageAllType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
    }

    public static final String SELECTION_BUCKETID_IMAGE_ALL =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionBucketidImageAll(String bucketId) {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), bucketId};
    }

    // 只选gif

    public static final String SELECTION_ALBUM_FOR_GIF =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionAlbumForGifType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), "image/gif"};
    }

    public static final String SELECTION_BUCKETID_FOR_GIF =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionBucketidForGifType(String bucketId) {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), bucketId, "image/gif"};
    }

    // 只选视频

    public static final String SELECTION_ALBUM_FOR_VIDEO_ALL =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionAlbumArgsForVideoAllType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    }

    public static final String SELECTION_BUCKETID_VIDEO_ALL =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getSelectionBucketidVideoAll(String bucketId) {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), bucketId};
    }


    //////////////////////////////////////////////////////////////////////////////////////

    public static final String TREE_SELECTION_IMAGE_NO_GIF =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND ("
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";
    public static final String TREE_SELECTION_IMAGE_NO_GIF_Q =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND ("
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + " OR "
                    + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0";

    public static String[] getTreeSelectionImageNoGif(int mediaType) {
        return new String[]{String.valueOf(mediaType), "image/jpeg", "image/png", "image/x-ms-bmp", "image/webp"};
    }
}
