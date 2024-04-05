package lib.kalu.fileselector.cursor;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Cursor cursor = super.loadInBackground();
        LogUtil.logE("TreeCursorLoader => loadInBackground => cursor = " + cursor);
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);

        if (android.os.Build.VERSION.SDK_INT < 29) {
            int totalCount = 0;
            Uri allAlbumCoverUri = null;
            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    LogUtil.logE("TreeCursorLoader => loadInBackground => moveToNext =>");
                    long fileId = cursor.getLong(
                            cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    long bucketId = cursor.getLong(
                            cursor.getColumnIndex(COLUMN_BUCKET_ID));
                    String bucketDisplayName = cursor.getString(
                            cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                    String mimeType = cursor.getString(
                            cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                    Uri uri = getUri(cursor);
                    int count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));

                    otherAlbums.addRow(new String[]{
                            Long.toString(fileId),
                            Long.toString(bucketId), bucketDisplayName, mimeType, uri.toString(),
                            String.valueOf(count)});
                    totalCount += count;
                }
                if (cursor.moveToFirst()) {
                    allAlbumCoverUri = getUri(cursor);
                }
            }

            allAlbum.addRow(new String[]{
                    AlbumModel.ALBUM_ID_ALL, AlbumModel.ALBUM_ID_ALL, AlbumModel.ALBUM_NAME_ALL, null,
                    allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                    String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        } else {
            int totalCount = 0;
            Uri allAlbumCoverUri = null;

            // Pseudo GROUP BY
            Map<Long, Long> countMap = new HashMap<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long bucketId = cursor.getLong(cursor.getColumnIndex(COLUMN_BUCKET_ID));

                    Long count = countMap.get(bucketId);
                    if (count == null) {
                        count = 1L;
                    } else {
                        count++;
                    }
                    countMap.put(bucketId, count);
                }
            }

            MatrixCursor otherAlbums = new MatrixCursor(COLUMNS);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    allAlbumCoverUri = getUri(cursor);

                    Set<Long> done = new HashSet<>();

                    do {
                        long bucketId = cursor.getLong(cursor.getColumnIndex(COLUMN_BUCKET_ID));

                        if (done.contains(bucketId)) {
                            continue;
                        }

                        long fileId = cursor.getLong(
                                cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                        String bucketDisplayName = cursor.getString(
                                cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                        String mimeType = cursor.getString(
                                cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                        Uri uri = getUri(cursor);
                        long count = countMap.get(bucketId);

                        otherAlbums.addRow(new String[]{
                                Long.toString(fileId),
                                Long.toString(bucketId),
                                bucketDisplayName,
                                mimeType,
                                uri.toString(),
                                String.valueOf(count)});
                        done.add(bucketId);

                        totalCount += count;
                    } while (cursor.moveToNext());
                }
            }

            allAlbum.addRow(new String[]{
                    AlbumModel.ALBUM_ID_ALL,
                    AlbumModel.ALBUM_ID_ALL, AlbumModel.ALBUM_NAME_ALL, null,
                    allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                    String.valueOf(totalCount)});

            return new MergeCursor(new Cursor[]{allAlbum, otherAlbums});
        }
    }

    private static Uri getUri(Cursor cursor) {

        try {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            LogUtil.logE("TreeCursorLoader => getUri => id = " + id + ", mimeType = " + mimeType);
            Uri contentUri;
            if (mimeType.startsWith("image/")) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (mimeType.startsWith("video/")) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else {
                contentUri = MediaStore.Files.getContentUri("external");
            }
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            return uri;
        } catch (Exception e) {
            return null;
        }


//        Uri contentUri;
//

//
//        Uri uri = ContentUris.withAppendedId(contentUri, id);
//        return uri;
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}