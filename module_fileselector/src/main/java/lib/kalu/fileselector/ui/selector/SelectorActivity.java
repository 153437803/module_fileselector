package lib.kalu.fileselector.ui.selector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.adapter.AlbumMediaAdapter;
import lib.kalu.fileselector.adapter.AlbumsAdapter;
import lib.kalu.fileselector.loader.AlbumCollection;
import lib.kalu.fileselector.loader.SelectedItemCollection;
import lib.kalu.fileselector.model.AlbumModel;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.ui.base.BasePreviewActivity;
import lib.kalu.fileselector.ui.priview.PreviewAllActivity;
import lib.kalu.fileselector.ui.priview.PreviewSelectorActivity;
import lib.kalu.fileselector.util.LogUtil;
import lib.kalu.fileselector.util.MediaStoreCompat;
import lib.kalu.fileselector.util.PathUtils;
import lib.kalu.fileselector.util.PhotoMetadataUtils;
import lib.kalu.fileselector.util.SingleMediaScanner;
import lib.kalu.fileselector.widget.AlbumsSpinner;
import lib.kalu.fileselector.widget.CheckRadioView;
import lib.kalu.fileselector.widget.IncapableDialog;

/**
 * description:
 * create by kalu on 2020-03-26
 */
public class SelectorActivity extends AppCompatActivity implements
        AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener,
        SelectorFragment.SelectionProvider, View.OnClickListener,
        AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener,
        AlbumMediaAdapter.OnPhotoCapture {

    public static final int RESULT_FAIL = 4001;
    public static final int RESULT_SUCC = 4002;

    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    private static final int REQUEST_CODE_PREVIEW = 23;
    private static final int REQUEST_CODE_CAPTURE = 24;
    public static final String CHECK_STATE = "checkState";
    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    private SelectorModel mSpec;

    private AlbumsSpinner mAlbumsSpinner = null;
    private AlbumsAdapter mAlbumsAdapter = null;
    private TextView mButtonPreview;
    private TextView mButtonApply;
    private View mContainer;
    private View mEmptyView;

    private LinearLayout mOriginalLayout;
    private CheckRadioView mOriginal;
    private boolean mOriginalEnable;

    private void loadData() {

        // 取消
        findViewById(R.id.fs_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            if (mSpec.captureModel == null)
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureModel);
        }

        mButtonPreview = (TextView) findViewById(R.id.lib_fs_string_preview);
        mButtonApply = (TextView) findViewById(R.id.lib_fs_string_apply);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        mContainer = findViewById(R.id.container);
        mEmptyView = findViewById(R.id.empty_view);
        mOriginalLayout = findViewById(R.id.originalLayout);
        mOriginal = findViewById(R.id.original);
        mOriginalLayout.setOnClickListener(this);

        mAlbumsAdapter = new AlbumsAdapter(this, null, false);
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setAdapter(mAlbumsAdapter);

        updateBottomToolbar();

        // 显示二级菜单
        boolean showMenuFolder = mSpec.showMenuFolder;
        if (showMenuFolder) {
            TextView textView = findViewById(R.id.selector_title);
            textView.setEnabled(true);
            mAlbumsSpinner.setSelectedTextView(textView);
            mAlbumsSpinner.setPopupAnchorView(textView);
            mAlbumsSpinner.setOnItemSelectedListener(this);
        } else {
            TextView textView = findViewById(R.id.selector_title);
            textView.setEnabled(false);
            mAlbumsSpinner.setSelectedTextView(null);
            mAlbumsSpinner.setPopupAnchorView(null);
            mAlbumsSpinner.setOnItemSelectedListener(null);
        }

        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.loadAlbums();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode != 1001)
                throw new Exception();
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                throw new Exception();
            loadData();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.lib_fs_string_permission_write_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        mSpec = SelectorModel.getInstance();
        if (!mSpec.hasInited) {
            setResult(RESULT_FAIL);
            finish();
            return;
        }

        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mSelectedCollection.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadData();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_FAIL);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<MediaModel> selected = (ArrayList<MediaModel>) resultBundle.getSerializable(SelectedItemCollection.STATE_SELECTION);
            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                if (selected != null) {
                    for (MediaModel mediaModel : selected) {

                        String mediaUriString = mediaModel.getMediaUriString();
                        Uri uri = Uri.parse(mediaUriString);

                        selectedUris.add(uri);
                        selectedPaths.add(PathUtils.getPath(this, uri));
                    }
                }
                result.putExtra(EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
                setResult(RESULT_SUCC, result);
                finish();
            } else {
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(
                        SelectorFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof SelectorFragment) {
                    ((SelectorFragment) mediaSelectionFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            // Just pass the data back to previous calling Activity.
            Uri contentUri = mMediaStoreCompat.getCurrentPhotoUri();
            String path = mMediaStoreCompat.getCurrentPhotoPath();
            ArrayList<Uri> selected = new ArrayList<>();
            selected.add(contentUri);
            ArrayList<String> selectedPath = new ArrayList<>();
            selectedPath.add(path);
            Intent result = new Intent();
            result.putExtra(EXTRA_RESULT_SELECTION, selected);
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPath);
            setResult(RESULT_SUCC, result);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                SelectorActivity.this.revokeUriPermission(contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            new SingleMediaScanner(this.getApplicationContext(), path, new SingleMediaScanner.ScanListener() {
                @Override
                public void onScanFinish() {
                    Log.i("SingleMediaScanner", "scan finish!");
                }
            });
            finish();
        }
    }

    private void updateBottomToolbar() {

        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.lib_fs_string_apply_default));
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setText(R.string.lib_fs_string_apply_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.lib_fs_string_apply, selectedCount));
        }


        if (mSpec.originalable) {
            mOriginalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            mOriginalLayout.setVisibility(View.INVISIBLE);
        }


    }


    private void updateOriginalState() {
        mOriginal.setChecked(mOriginalEnable);
        if (countOverMaxSize() > 0) {

            if (mOriginalEnable) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.lib_fs_string_error_over_original_size, mSpec.imageOriginalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());

                mOriginal.setChecked(false);
                mOriginalEnable = false;
            }
        }
    }


    private int countOverMaxSize() {
        int count = 0;
        int selectedCount = mSelectedCollection.count();
        for (int i = 0; i < selectedCount; i++) {
            MediaModel mediaModel = mSelectedCollection.asList().get(i);

            if (mediaModel.isImage()) {
                float size = PhotoMetadataUtils.getSizeInMB(mediaModel.mMediaSize);
                if (size > mSpec.imageOriginalMaxSize) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lib_fs_string_preview) {
            Intent intent = new Intent(this, PreviewSelectorActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        } else if (v.getId() == R.id.lib_fs_string_apply) {

            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.lib_fs_string_error_over_original_count, count, mSpec.imageOriginalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            Intent result = new Intent();
            ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
            result.putExtra(EXTRA_RESULT_SELECTION, selectedUris);
            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
            result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            setResult(RESULT_SUCC, result);
            finish();
        } else if (v.getId() == R.id.originalLayout) {
            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.lib_fs_string_error_over_original_count, count, mSpec.imageOriginalMaxSize));
                incapableDialog.show(getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            mOriginalEnable = !mOriginalEnable;
            mOriginal.setChecked(mOriginalEnable);

            if (mSpec.onCheckedListener != null) {
                mSpec.onCheckedListener.onCheck(mOriginalEnable);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent1, View view1, int position, long id1) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        AlbumModel albumModel = AlbumModel.valueOf(mAlbumsAdapter.getCursor());
        if (albumModel.isAll() && SelectorModel.getInstance().capture) {
            albumModel.addAlbumNum();
        }
        onAlbumSelected(albumModel);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onAlbumLoad(final Cursor cursor) {

        new AsyncTask<Void, Void, List<AlbumModel>>() {

            @Override
            protected void onPreExecute() {
                mAlbumsAdapter.swapCursor(cursor);
            }

            @Override
            protected List<AlbumModel> doInBackground(Void... voids) {
                ArrayList<AlbumModel> list = new ArrayList<>();

                // 显示二级菜单
                boolean showMenuFolder = mSpec.showMenuFolder;
                if (showMenuFolder) {
                    if (cursor.moveToFirst()) {
                        do {
                            AlbumModel albumModel = AlbumModel.valueOf(cursor);
                            if (albumModel.isAll() && SelectorModel.getInstance().capture) {
                                albumModel.addAlbumNum();
                            }
                            list.add(albumModel);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                } else {
                    cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                    AlbumModel albumModel = AlbumModel.valueOf(cursor);
                    if (albumModel.isAll() && SelectorModel.getInstance().capture) {
                        albumModel.addAlbumNum();
                    }
                    list.add(albumModel);
                    cursor.close();
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<AlbumModel> albumModels) {

                int currentSelection = mAlbumCollection.getCurrentSelection();
                mAlbumsSpinner.setSelection(SelectorActivity.this, currentSelection);

                ArrayList<String> fragmentByTags = new ArrayList<>();
                for (AlbumModel albumModel : albumModels) {
                    if (null == albumModels)
                        continue;
                    String albumUriString = albumModel.getAlbumUriString();
                    if (null == albumUriString || albumUriString.length() == 0)
                        continue;
                    if (fragmentByTags.contains(albumUriString))
                        continue;
                    fragmentByTags.add(albumUriString);
                }


                for (AlbumModel albumModel : albumModels) {
                    if (null == albumModels)
                        continue;
                    String albumName = albumModel.getAlbumName(getApplicationContext());
                    LogUtil.logE("SelectorActivity => onAlbumLoad => albumName = " + albumName);
                }


//                for (AlbumModel albumModel : albumModels) {
//                    if (null == albumModels)
//                        continue;
//                    String albumUriString = albumModel.getAlbumUriString();
//                    if (null == albumUriString || albumUriString.length() == 0)
//                        continue;
//                    albumModel.setFragmentByTags(fragmentByTags);
//                    getSupportFragmentManager().beginTransaction()
//                            .add(R.id.container, SelectorFragment.newInstance(albumModel), albumUriString)
//                            .commitNow();
//                }
                onAlbumSelected(albumModels.get(0));
            }
        }.execute();
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    private void onAlbumSelected(AlbumModel albumModel) {

        try {
            TextView textView = findViewById(R.id.selector_title);
            textView.setText(albumModel.getAlbumName(getApplicationContext()));
        } catch (Exception e) {
        }

        try {
            if (null == albumModel)
                throw new Exception();
            boolean empty = albumModel.isEmpty();
            if (empty)
                throw new Exception();
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SelectorFragment.newInstance(albumModel))
                    .commitNow();

//            String albumUriString = albumModel.getAlbumUriString();
//            List<String> fragmentByTags = albumModel.getFragmentByTags();
//            for (String s : fragmentByTags) {
//                Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(s);
//                if (null == fragmentByTag)
//                    continue;
//                if (albumUriString.equals(s)) {
//                    LogUtil.logE("SelectorActivity => onAlbumSelected => show => albumUriString = " + s);
//                    getSupportFragmentManager().beginTransaction()
//                            .show(fragmentByTag)
//                            .commitNow();
//                } else {
//                    LogUtil.logE("SelectorActivity => onAlbumSelected => hide => albumUriString = " + s);
//                    getSupportFragmentManager().beginTransaction()
//                            .hide(fragmentByTag)
//                            .commitNow();
//                }
//            }

        } catch (Exception e) {
            LogUtil.logE("SelectorActivity => onAlbumSelected => " + e.getMessage());
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();

        if (mSpec.onSelectedListener != null) {
            mSpec.onSelectedListener.onSelected(
                    mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
        }
    }

    @Override
    public void onMediaClick(AlbumModel albumModel, MediaModel mediaModel, int adapterPosition) {
        Intent intent = new Intent(this, PreviewAllActivity.class);
        intent.putExtra(PreviewAllActivity.EXTRA_ALBUM, albumModel);
        intent.putExtra(PreviewAllActivity.EXTRA_ITEM, mediaModel);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void capture() {
        if (mMediaStoreCompat != null) {
            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
        }
    }
}