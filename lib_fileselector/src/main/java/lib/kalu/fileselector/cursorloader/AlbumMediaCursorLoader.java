package lib.kalu.fileselector.cursorloader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;

import androidx.loader.content.CursorLoader;

import lib.kalu.fileselector.model.AlbumModel;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.sql.SqlString;
import lib.kalu.fileselector.util.MediaStoreCompat;

/**
 * description: 相册文件信息
 * create by kalu on 2020-05-19
 */
public class AlbumMediaCursorLoader extends CursorLoader {

    private final boolean mEnableCapture;

    private AlbumMediaCursorLoader(Context context, String selection, String[] selectionArgs, boolean capture) {
        super(context, SqlString.QUERY_URI, SqlString.PROJECTION, selection, selectionArgs, SqlString.ORDER_BY);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, AlbumModel albumModel, boolean capture) {

        String[] args;
        String selection;

        // 全部, gif
        if (albumModel.isAll() && SelectorModel.getInstance().onlyShowGif()) {

            selection = SqlString.SELECTION_ALBUM_FOR_GIF;
            args = SqlString.getSelectionAlbumForGifType();

        }
        //  全部, video
        else if (albumModel.isAll() && SelectorModel.getInstance().onlyShowVideos()) {

            selection = SqlString.SELECTION_ALBUM_FOR_VIDEO_ALL;
            args = SqlString.getSelectionAlbumArgsForVideoAllType();

        }
        // 全部, image no gif
        else if (albumModel.isAll() && SelectorModel.getInstance().onlyShowImagesNoGif()) {

            selection = SqlString.SELECTION_ALBUM_FOR_IMAGE_NO_GIF;
            args = SqlString.getSelectionAlbumArgsForImageNoGifType();
        }
        // 全部, image contains gif
        else if (albumModel.isAll() && SelectorModel.getInstance().onlyShowImages()) {

            selection = SqlString.SELECTION_ALBUM_FOR_IMAGE_All;
            args = SqlString.getSelectionAlbumArgsForImageAllType();
        }
        // 全部
        else if (albumModel.isAll()) {

            selection = SqlString.SELECTION_ALBUM_ALL;
            args = SqlString.getSelectionAlbumAll();
        }
        // 子相册, gif
        else if (SelectorModel.getInstance().onlyShowGif()) {

            String id = albumModel.getId();
            selection = SqlString.SELECTION_BUCKETID_FOR_GIF;
            args = SqlString.getSelectionBucketidForGifType(id);

        }
        //  子相册, video
        else if (SelectorModel.getInstance().onlyShowVideos()) {

            String id = albumModel.getId();
            selection = SqlString.SELECTION_BUCKETID_VIDEO_ALL;
            args = SqlString.getSelectionBucketidVideoAll(id);

        }
        // 子相册, image no gif
        else if (SelectorModel.getInstance().onlyShowImagesNoGif()) {

            String id = albumModel.getId();
            selection = SqlString.SELECTION_BUCKETID_IMAGE_NO_GIF;
            args = SqlString.getSelectionBucketidImageNoGif(id);
        }
        // 子相册, image contains gif
        else if (SelectorModel.getInstance().onlyShowImages()) {

            String id = albumModel.getId();
            selection = SqlString.SELECTION_BUCKETID_IMAGE_ALL;
            args = SqlString.getSelectionBucketidImageAll(id);
        }
        // 子相册, all
        else {

            String id = albumModel.getId();
            selection = SqlString.SELECTION_BUCKETID_All;
            args = SqlString.getSelectionBucketidAll(id);
        }

        return new AlbumMediaCursorLoader(context, selection, args, capture);

    }

    @Override
    public Cursor loadInBackground() {

        Cursor result = super.loadInBackground();
        if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(SqlString.PROJECTION);
        dummy.addRow(new Object[]{MediaModel.ITEM_ID_CAPTURE, MediaModel.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
