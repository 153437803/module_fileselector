package lib.kalu.fileselector.model;

import android.content.pm.ActivityInfo;

import androidx.annotation.Keep;

import java.util.List;
import java.util.Set;

import lib.kalu.fileselector.filter.Filter;
import lib.kalu.fileselector.imageload.BaseImageload;
import lib.kalu.fileselector.imageload.GlideImageload;
import lib.kalu.fileselector.listener.OnCheckedListener;
import lib.kalu.fileselector.listener.OnSelectedListener;
import lib.kalu.fileselector.mimetype.SelectorMimeType;

/**
 * description: 选择器
 * create by Administrator on 2020-03-26
 */
@Keep
public final class SelectorModel {

    public Set<SelectorMimeType> selectorMimeTypeSet;
    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType;
    //    @StyleRes
//    public int themeId;
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
    public float thumbnailScale;
    public BaseImageload baseImageload;
    public boolean hasInited;
    public OnSelectedListener onSelectedListener;
    public boolean originalable;
    public boolean autoHideToobar;
    public int originalMaxSize;
    public OnCheckedListener onCheckedListener;
    public boolean showPreview;

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
        selectorMimeTypeSet = null;
        mediaTypeExclusive = true;
        showSingleMediaType = false;
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
        thumbnailScale = 0.5f;
        baseImageload = new GlideImageload();
        hasInited = true;
        originalable = false;
        autoHideToobar = false;
        originalMaxSize = Integer.MAX_VALUE;
        showPreview = true;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && SelectorMimeType.ofImage().containsAll(selectorMimeTypeSet);
    }

    public boolean onlyShowImagesNoGif() {
        return showSingleMediaType && SelectorMimeType.ofImageNoGif().containsAll(selectorMimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && SelectorMimeType.ofVideo().containsAll(selectorMimeTypeSet);
    }

    public boolean onlyShowGif() {
        return showSingleMediaType && SelectorMimeType.ofGif().equals(selectorMimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectorModel INSTANCE = new SelectorModel();
    }
}
