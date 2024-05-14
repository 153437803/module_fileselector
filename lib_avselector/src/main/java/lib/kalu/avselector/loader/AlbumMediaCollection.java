package lib.kalu.avselector.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.lang.ref.WeakReference;

import lib.kalu.avselector.cursor.MediaCursorLoader;
import lib.kalu.avselector.model.AlbumModel;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumMediaCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        try {
            Log.e("HHJHJH", "AlbumMediaCollection => onCreateLoader =>");
            Context context = mContext.get();
            if (context == null)
                throw new Exception("error: context == null");
            AlbumModel albumModel = (AlbumModel) args.getSerializable(ARGS_ALBUM);
            if (albumModel == null)
                throw new Exception("error: albumModel == null");
            boolean all = albumModel.isAll();
            return MediaCursorLoader.newInstance(context, albumModel, all && args.getBoolean(ARGS_ENABLE_CAPTURE, false));
        } catch (Exception e) {
            Log.e("HHJHJH", "AlbumMediaCollection => onCreateLoader => " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumMediaLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumMediaReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull AlbumMediaCallbacks callbacks) {
        mContext = new WeakReference<>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void load(@Nullable AlbumModel target) {
        load(target, false);
    }

    public void load(@Nullable AlbumModel target, boolean enableCapture) {

        if (null == target)
            return;

        Bundle args = new Bundle();
        args.putSerializable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public interface AlbumMediaCallbacks {

        void onAlbumMediaLoad(Cursor cursor);

        void onAlbumMediaReset();
    }
}
