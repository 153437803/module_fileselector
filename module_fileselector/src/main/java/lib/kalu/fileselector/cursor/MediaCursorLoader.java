package lib.kalu.fileselector.cursor;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.CursorLoader;

import java.util.ArrayList;
import java.util.Arrays;

import lib.kalu.fileselector.model.AlbumModel;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.sql.SqlString;
import lib.kalu.fileselector.util.LogUtil;
import lib.kalu.fileselector.util.MediaStoreCompat;

/**
 * description: 相册文件信息
 * create by kalu on 2020-05-19
 */
public class MediaCursorLoader extends CursorLoader {

    private final boolean mEnableCapture;

    private MediaCursorLoader(Context context, String selection, String[] selectionArgs, boolean capture) {
        super(context, SqlString.QUERY_URI, SqlString.PROJECTION, selection, selectionArgs, SqlString.ORDER_BY);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, AlbumModel albumModel, boolean capture) {
        String[] args;
        String selection;

        // 全部相册
        if (albumModel.isAll()) {

            StringBuilder selectionBuilder = new StringBuilder();

            // step1
            selectionBuilder.append("(");
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

            // step2
            selectionBuilder.append(" AND (");
            String[] mimeTypes = SelectorModel.getInstance().mimeTypes;
            int mimeTypesLength = mimeTypes.length;
            for (int i = 0; i < mimeTypesLength; i++) {
                if (i > 0) {
                    selectionBuilder.append(" OR ");
                }
                selectionBuilder.append(MediaStore.MediaColumns.MIME_TYPE);
                selectionBuilder.append("=?");
            }
            selectionBuilder.append(")");
            selectionBuilder.append(" AND ");
            selectionBuilder.append(MediaStore.MediaColumns.SIZE);
            selectionBuilder.append(">0");

            ArrayList<String> argsList = new ArrayList<>();
            for (int mediaType : mediaTypes) {
                argsList.add(String.valueOf(mediaType));
            }
            for (String mimeType : mimeTypes) {
                argsList.add(mimeType);
            }

            args = argsList.toArray(new String[argsList.size()]);
            selection = selectionBuilder.toString();
        }
        // 子相册
        else {


            StringBuilder selectionBuilder = new StringBuilder();

            // step1
            selectionBuilder.append("(");
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
            selectionBuilder.append(" AND ");
            selectionBuilder.append("bucket_id=?");

            // step2
            selectionBuilder.append(" AND (");
            String[] mimeTypes = SelectorModel.getInstance().mimeTypes;
            int mimeTypesLength = mimeTypes.length;
            for (int i = 0; i < mimeTypesLength; i++) {
                if (i > 0) {
                    selectionBuilder.append(" OR ");
                }
                selectionBuilder.append(MediaStore.MediaColumns.MIME_TYPE);
                selectionBuilder.append("=?");
            }
            selectionBuilder.append(")");
            selectionBuilder.append(" AND ");
            selectionBuilder.append(MediaStore.MediaColumns.SIZE);
            selectionBuilder.append(">0");

            ArrayList<String> argsList = new ArrayList<>();
            for (int mediaType : mediaTypes) {
                argsList.add(String.valueOf(mediaType));
            }
            argsList.add(String.valueOf(albumModel.getId()));

            for (String mimeType : mimeTypes) {
                argsList.add(mimeType);
            }

            args = argsList.toArray(new String[argsList.size()]);
            selection = selectionBuilder.toString();
        }

        LogUtil.logE("MediaCursorLoader => newInstance => args = " + Arrays.toString(args));
        LogUtil.logE("MediaCursorLoader => newInstance => selection = " + selection);

        return new MediaCursorLoader(context, selection, args, capture);
    }

    @Override
    public Cursor loadInBackground() {
        LogUtil.logE("MediaCursorLoader => loadInBackground =>");
        Cursor result = super.loadInBackground();
        try {
            if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(getContext()))
                throw new Exception();
            MatrixCursor dummy = new MatrixCursor(SqlString.PROJECTION);
            dummy.addRow(new Object[]{MediaModel.ITEM_ID_CAPTURE, MediaModel.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
            return new MergeCursor(new Cursor[]{dummy, result});
        } catch (Exception e) {
            return result;
        }
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
