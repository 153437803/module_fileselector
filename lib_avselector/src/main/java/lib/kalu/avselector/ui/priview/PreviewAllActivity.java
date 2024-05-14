package lib.kalu.avselector.ui.priview;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.avselector.adapter.PreviewPagerAdapter;
import lib.kalu.avselector.loader.AlbumMediaCollection;
import lib.kalu.avselector.model.AlbumModel;
import lib.kalu.avselector.model.MediaModel;
import lib.kalu.avselector.model.SelectorModel;
import lib.kalu.avselector.ui.base.BasePreviewActivity;

/**
 * description: 默认预览
 * create by Administrator on 2020-03-26
 */
public class PreviewAllActivity extends BasePreviewActivity implements
        AlbumMediaCollection.AlbumMediaCallbacks {

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    private AlbumMediaCollection mCollection = new AlbumMediaCollection();

    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectorModel.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        mCollection.onCreate(this, this);
        AlbumModel albumModel = (AlbumModel) getIntent().getSerializableExtra(EXTRA_ALBUM);
        mCollection.load(albumModel);

        MediaModel mediaModel = (MediaModel) getIntent().getSerializableExtra(EXTRA_ITEM);
        if (mSpec.countable) {
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(mediaModel));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(mediaModel));
        }
        updateSize(mediaModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<MediaModel> mediaModels = new ArrayList<>();
        while (cursor.moveToNext()) {
            mediaModels.add(MediaModel.valueOf(cursor));
        }
        cursor.close();

        if (mediaModels.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        adapter.addAll(mediaModels);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true;
            MediaModel selected = (MediaModel) getIntent().getSerializableExtra(EXTRA_ITEM);
            int selectedIndex = mediaModels.indexOf(selected);
            mPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
    }

    @Override
    public void onAlbumMediaReset() {

    }
}
