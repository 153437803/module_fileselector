package lib.kalu.fileselector.model;

import android.content.pm.ActivityInfo;
import android.provider.MediaStore;

import androidx.annotation.Keep;

import java.util.List;

import lib.kalu.fileselector.filter.Filter;
import lib.kalu.fileselector.imageload.BaseImageload;
import lib.kalu.fileselector.imageload.GlideImageload;
import lib.kalu.fileselector.listener.OnCheckedListener;
import lib.kalu.fileselector.listener.OnSelectedListener;

/**
 * description: 选择器
 * create by Administrator on 2020-03-26
 */
@Keep
public final class SelectorModel {

    public int[] mediaTypes = new int[]{MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO};
    public String[] mimeTypes = new String[]{"image/png", "imeg/jpeg", "video/mp4"};
    public int orientation;
    public boolean countable;
    public int maxSelectable;
    public int maxImageSelectable;
    public int maxVideoSelectable;
    public List<Filter> filters;
    public boolean capture;
    public CaptureModel captureModel;
    public int spanCount;
    public int gridExpectedSize;
    public BaseImageload baseImageload;
    public boolean hasInited;
    public OnSelectedListener onSelectedListener;
    public boolean originalable;
    public boolean autoHideToobar;
    public int imageOriginalMaxSize = 10;
    public int videoOriginalMaxSize = 50;
    public OnCheckedListener onCheckedListener;
    public boolean showPreview = false;
    public boolean showMenuFolder = false;

    private SelectorModel() {
    }

    public static SelectorModel getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectorModel getCleanInstance() {
        SelectorModel selectorModel = getInstance();
        selectorModel.reset();
        return selectorModel;
    }

    private void reset() {
        mediaTypes = null;
        mimeTypes = null;
//        themeId = R.style.Theme1;
        orientation = 0;
        countable = false;
        maxSelectable = 1;
        maxImageSelectable = 0;
        maxVideoSelectable = 0;
        filters = null;
        capture = false;
        captureModel = null;
        spanCount = 3;
        gridExpectedSize = 0;
        baseImageload = new GlideImageload();
        hasInited = true;
        originalable = false;
        autoHideToobar = false;
        imageOriginalMaxSize = Integer.MAX_VALUE;
        videoOriginalMaxSize = Integer.MAX_VALUE;
        showPreview = true;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    private static final class InstanceHolder {
        private static final SelectorModel INSTANCE = new SelectorModel();
    }
}
