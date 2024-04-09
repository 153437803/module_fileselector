package lib.kalu.fileselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import lib.kalu.fileselector.filter.Filter;
import lib.kalu.fileselector.imageload.BaseImageload;
import lib.kalu.fileselector.listener.OnCheckedListener;
import lib.kalu.fileselector.listener.OnSelectedListener;
import lib.kalu.fileselector.model.CaptureModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.ui.selector.SelectorActivity;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_BEHIND;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;

@Keep
public final class SelectionCreator {
    private final Selector mSelector;
    private final SelectorModel mSelectorModel;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntDef({
            SCREEN_ORIENTATION_UNSPECIFIED,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_USER,
            SCREEN_ORIENTATION_BEHIND,
            SCREEN_ORIENTATION_SENSOR,
            SCREEN_ORIENTATION_NOSENSOR,
            SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_FULL_SENSOR,
            SCREEN_ORIENTATION_USER_LANDSCAPE,
            SCREEN_ORIENTATION_USER_PORTRAIT,
            SCREEN_ORIENTATION_FULL_USER,
            SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {
    }

    /**
     * Constructs a new specification builder on the context.
     *
     * @param selector a requester context wrapper.
     */
    SelectionCreator(Selector selector) {
        mSelector = selector;
        mSelectorModel = SelectorModel.getCleanInstance();
        mSelectorModel.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * 显示图片选择顺序
     *
     * @param countable
     * @return
     */
    public SelectionCreator setSelectOrderEnable(boolean countable) {
        mSelectorModel.countable = countable;
        return this;
    }

    /**
     * Maximum selectable count.
     *
     * @param maxSelectable Maximum selectable count. Default value is 1.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setSelectMax(int maxSelectable) {
        if (maxSelectable < 1)
            throw new IllegalArgumentException("maxSelectable must be greater than or equal to one");
        if (mSelectorModel.maxImageSelectable > 0 || mSelectorModel.maxVideoSelectable > 0)
            throw new IllegalStateException("already set maxImageSelectable and maxVideoSelectable");
        mSelectorModel.maxSelectable = maxSelectable;
        return this;
    }

    /**
     * selectable files for image and video media types.
     *
     * @param maxImageSelectable Maximum selectable count for image.
     * @param maxVideoSelectable Maximum selectable count for video.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator maxSelectablePerMediaType(int maxImageSelectable, int maxVideoSelectable) {
        if (maxImageSelectable < 1 || maxVideoSelectable < 1)
            throw new IllegalArgumentException(("max selectable must be greater than or equal to one"));
        mSelectorModel.maxSelectable = -1;
        mSelectorModel.maxImageSelectable = maxImageSelectable;
        mSelectorModel.maxVideoSelectable = maxVideoSelectable;
        return this;
    }

    /**
     * Add filter to filter each selecting item.
     *
     * @param filter {@link Filter}
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setPreviewFilter(@NonNull Filter filter) {
        if (mSelectorModel.filters == null) {
            mSelectorModel.filters = new ArrayList<>();
        }
        if (filter == null) throw new IllegalArgumentException("filter cannot be null");
        mSelectorModel.filters.add(filter);
        return this;
    }

    /**
     * Determines whether the photo capturing is enabled or not on the media grid view.
     * <p>
     * If this value is set true, photo capturing entry will appear only on All Media's page.
     *
     * @param enable Whether to enable capturing or not. Default value is false;
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator showCamera(boolean enable) {
        mSelectorModel.capture = enable;
        return this;
    }

    /**
     * 是否显示原图按钮
     *
     * @param enable
     * @return
     */
    public SelectionCreator showImageOriginal(boolean enable) {
        mSelectorModel.originalable = enable;
        return this;
    }


    /**
     * 单机自动隐藏Toolbar
     *
     * @param enable
     * @return
     */
    public SelectionCreator setAutoHideToolbarOnSingleTap(boolean enable) {
        mSelectorModel.autoHideToobar = enable;
        return this;
    }

    /**
     * Maximum original size,the unit is MB. Only useful when {link@originalEnable} set true
     *
     * @param size Maximum original size. Default value is Integer.MAX_VALUE
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setFilterImageMaxSizeMb(int size) {
        mSelectorModel.imageOriginalMaxSize = size;
        return this;
    }

    public SelectionCreator setFilterVideoMaxSizeMb(int size) {
        mSelectorModel.videoOriginalMaxSize = size;
        return this;
    }

    /**
     * Capture strategy provided for the location to save photos including internal and external
     * storage and also a authority for {@link androidx.core.content.FileProvider}.
     *
     * @param captureModel {@link CaptureModel}, needed only when capturing is enabled.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setFileProvider(CaptureModel captureModel) {
        mSelectorModel.captureModel = captureModel;
        return this;
    }

    /**
     * Set the desired orientation of this activity.
     *
     * @param orientation An orientation constant as used in {@link ScreenOrientation}.
     *                    Default value is {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
     * @return {@link SelectionCreator} for fluent API.
     * @see Activity#setRequestedOrientation(int)
     */
    public SelectionCreator setOrientation(@ScreenOrientation int orientation) {
        mSelectorModel.orientation = orientation;
        return this;
    }

    public SelectionCreator spanCount(int spanCount) {
        if (spanCount < 1) throw new IllegalArgumentException("spanCount cannot be less than 1");
        mSelectorModel.spanCount = spanCount;
        return this;
    }

    /**
     * Set expected size for media grid to adapt to different screen sizes. This won't necessarily
     * be applied cause the media grid should fill the view container. The measured media grid's
     * size will be as close to this value as possible.
     *
     * @param size Expected media grid size in pixel.
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setGridSpacingSize(int size) {
        mSelectorModel.gridExpectedSize = size;
        return this;
    }

    public SelectionCreator setImageload(BaseImageload baseImageload) {
        mSelectorModel.baseImageload = baseImageload;
        return this;
    }

    /**
     * Set listener for callback immediately when user select or unselect something.
     * <p>
     * It's a redundant API with {@link Selector#obtainResult(Intent)},
     * we only suggest you to use this API when you need to do something immediately.
     *
     * @param listener {@link OnSelectedListener}
     * @return {@link SelectionCreator} for fluent API.
     */
    @NonNull
    public SelectionCreator setOnSelectedListener(@Nullable OnSelectedListener listener) {
        mSelectorModel.onSelectedListener = listener;
        return this;
    }

    /**
     * Set listener for callback immediately when user check or uncheck original.
     *
     * @param listener {@link OnSelectedListener}
     * @return {@link SelectionCreator} for fluent API.
     */
    public SelectionCreator setOnCheckedListener(@Nullable OnCheckedListener listener) {
        mSelectorModel.onCheckedListener = listener;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void startActivityForResult(int requestCode) {
        try {
            if (null == mSelector)
                throw new Exception("error: null == mSelector");
            Activity activity = mSelector.getActivity();
            if (null == activity)
                throw new Exception("error: null == activity");
            Intent intent = new Intent(activity, SelectorActivity.class);
            Fragment fragment = mSelector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
        }
    }

    public SelectionCreator showPreview(boolean showPreview) {
        mSelectorModel.showPreview = showPreview;
        return this;
    }

    public SelectionCreator showFolders(boolean showFolders) {
        mSelectorModel.showMenuFolder = showFolders;
        return this;
    }

//    public SelectionCreator setFilterMediaTypes(boolean hasImage, boolean hasVideo, boolean hasFile) {
//        if (hasImage && hasVideo && hasFile) {
//            mSelectorModel.mediaTypes = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_NONE};
//        } else if (hasImage && hasVideo) {
//            mSelectorModel.mediaTypes = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
//        } else if (hasVideo) {
//            mSelectorModel.mediaTypes = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
//        } else if (hasImage) {
//            mSelectorModel.mediaTypes = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE};
//        }
//        return this;
//    }

    public SelectionCreator setFilterMediaTypes(String[] strings) {
        try {
            ArrayList<Integer> list = new ArrayList<>();
            for (String s : strings) {
                if (null == s || s.length() == 0)
                    continue;
                if ("image".equalsIgnoreCase(s)) {
                    if (!list.contains(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)) {
                        list.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    }
                } else if ("video".equalsIgnoreCase(s)) {
                    if (!list.contains(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)) {
                        list.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
                    }
                }
            }
            int size = list.size();
            int[] mediaTypes = new int[size];
            for (int i = 0; i < size; i++) {
                mediaTypes[i] = list.get(i);
            }
            mSelectorModel.mediaTypes = mediaTypes;
        } catch (Exception e) {
        }
        return this;
    }

    public SelectionCreator setFilterMimeTypes(String[] strings) {
        try {
            ArrayList<String> list = new ArrayList<>();
            for (String s : strings) {
                if (null == s || s.length() == 0)
                    continue;
                if ("png".equalsIgnoreCase(s)) {
                    if (!list.contains("image/png")) {
                        list.add("image/png");
                    }
                } else if ("jpg".equalsIgnoreCase(s) || "jpeg".equalsIgnoreCase(s)) {
                    if (!list.contains("image/jpg")) {
                        list.add("image/jpg");
                        list.add("image/jpeg");
                    }
                } else if ("mp4".equalsIgnoreCase(s) || "m4a".equalsIgnoreCase(s)) {
                    if (!list.contains("video/mp4")) {
                        list.add("video/mp4");
                        list.add("video/m4a");
                    }
                }
            }
            int size = list.size();
            String[] mimeTypes = new String[size];
            for (int i = 0; i < size; i++) {
                mimeTypes[i] = list.get(i);
            }
            mSelectorModel.mimeTypes = mimeTypes;
        } catch (Exception e) {
        }
        return this;
    }
}
