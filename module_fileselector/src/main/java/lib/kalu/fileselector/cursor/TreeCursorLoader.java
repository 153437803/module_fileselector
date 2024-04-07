package lib.kalu.fileselector.cursor;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;
import java.util.Arrays;

import lib.kalu.fileselector.model.AlbumModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.util.LogUtil;

/**
 * description: 相册目录信息
 * create by kalu on 2020-05-19
 */
public class TreeCursorLoader extends CursorLoader {

    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_URI,
            COLUMN_COUNT};

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private TreeCursorLoader(Context context, String[] projection, String selection, String[] args) {
        super(context, QUERY_URI, projection, selection, args, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context) {

        String[] args;
        String selection;
        String[] projection;

        if (android.os.Build.VERSION.SDK_INT < 29) {
            projection = new String[]{
                    MediaStore.Files.FileColumns._ID,
                    COLUMN_BUCKET_ID,
                    COLUMN_BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.MIME_TYPE,
                    "COUNT(*) AS " + COLUMN_COUNT};
        } else {
            projection = new String[]{
                    MediaStore.Files.FileColumns._ID,
                    COLUMN_BUCKET_ID,
                    COLUMN_BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.MIME_TYPE};
        }


        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append("(");

        // step1
        int[] mediaTypes = SelectorModel.getInstance().mediaTypes;
        int mediaTypesLength = mediaTypes.length;
        for (int i = 0; i < mediaTypesLength; i++) {
            if (i > 0) {
                selectionBuilder.append(" OR ");
            }
            selectionBuilder.append(MediaStore.Files.FileColumns.MEDIA_TYPE);
            selectionBuilder.append("=?");
        }
        selectionBuilder.append(")");

        if (android.os.Build.VERSION.SDK_INT < 29) {
            selectionBuilder.append(" AND ");
            selectionBuilder.append(MediaStore.MediaColumns.SIZE);
            selectionBuilder.append(">0");
            selectionBuilder.append(") GROUP BY (bucket_id");
        } else {
            selectionBuilder.append(" AND ");
            selectionBuilder.append(MediaStore.MediaColumns.SIZE);
            selectionBuilder.append(">0");
        }

        ArrayList<String> argsList = new ArrayList<>();
        for (int mediaType : mediaTypes) {
            argsList.add(String.valueOf(mediaType));
        }

        args = argsList.toArray(new String[argsList.size()]);
        selection = selectionBuilder.toString();

        LogUtil.logE("TreeCursorLoader => newInstance => projection = " + Arrays.toString(projection));
        LogUtil.logE("TreeCursorLoader => newInstance => args = " + Arrays.toString(args));
        LogUtil.logE("TreeCursorLoader => newInstance => selection = " + selection);

        return new TreeCursorLoader(context, projection, selection, args);
    }

    @Override
    public Cursor loadInBackground() {

        try {
            Cursor cursor = super.loadInBackground();
            if (null == cursor)
                throw new Exception("error: null == cursor");
            // 1. 构建全部相册
            if (!cursor.moveToFirst())
                throw new Exception();
            int _id_all = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
            long id_all = cursor.getLong(_id_all);
            int _mime_type_all = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            String mimeType_all = cursor.getString(_mime_type_all);
            Uri url_all;
            if (mimeType_all.startsWith("image/")) {
                url_all = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id_all);
            } else if (mimeType_all.startsWith("video/")) {
                url_all = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id_all);
            } else {
                url_all = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id_all);
            }
            // 2. 查找相册
            int totalCount = 0;
            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            while (cursor.moveToNext()) {
                // 1
                int _id = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
                long id = cursor.getLong(_id);
                // 2
                int _bucket_id = cursor.getColumnIndex(COLUMN_BUCKET_ID);
                long bucketId = cursor.getLong(_bucket_id);
                // 3
                int _bucket_display_name = cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME);
                String bucketDisplayName = cursor.getString(_bucket_display_name);
                // 4
                int _mime_type = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
                String mimeType = cursor.getString(_mime_type);
                // 5
                int _count = cursor.getColumnIndex(COLUMN_COUNT);
                int count = cursor.getInt(_count);
                // 6
                Uri uri;
                if (mimeType.startsWith("image/")) {
                    uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                } else if (mimeType.startsWith("video/")) {
                    uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                } else {
                    uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
                }
                String uriString = uri.toString();
                LogUtil.logE("TreeCursorLoader => loadInBackground => id = " + id + ", bucketId = " + bucketId + ", bucketDisplayName = " + bucketDisplayName + ", mimeType = " + mimeType + ", count = " + count + ", uriString = " + uriString);
                // 7
                otherAlbums.addRow(new String[]{Long.toString(id), Long.toString(bucketId), bucketDisplayName, mimeType, uriString, String.valueOf(count)});
                totalCount += count;
            }
            // 3. 合并相册
            MatrixCursor allAlbums = new MatrixCursor(COLUMNS);
            allAlbums.addRow(new String[]{
                    AlbumModel.ALBUM_ID_ALL, AlbumModel.ALBUM_ID_ALL, AlbumModel.ALBUM_NAME_ALL, null,
                    url_all == null ? null : url_all.toString(),
                    String.valueOf(totalCount)});
            return new MergeCursor(new Cursor[]{allAlbums, otherAlbums});
        } catch (Exception e) {
            LogUtil.logE("TreeCursorLoader => loadInBackground => " + e.getMessage());
            return null;
        }
    }


    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}