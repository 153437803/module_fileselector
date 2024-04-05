package lib.kalu.fileselector.loader;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.filter.FilterFailCause;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.util.PathUtils;
import lib.kalu.fileselector.util.PhotoMetadataUtils;
import lib.kalu.fileselector.widget.CheckView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";
    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;
    private final Context mContext;
    private Set<MediaModel> mMediaModels;
    private int mCollectionType = COLLECTION_UNDEFINED;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            mMediaModels = new LinkedHashSet<>();
        } else {
            List<MediaModel> saved = (List<MediaModel>) bundle.getSerializable(STATE_SELECTION);
            mMediaModels = new LinkedHashSet<>(saved);
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
    }

    public void setDefaultSelection(List<MediaModel> uris) {
        mMediaModels.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_SELECTION, new ArrayList<>(mMediaModels));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STATE_SELECTION, new ArrayList<>(mMediaModels));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    public boolean add(MediaModel mediaModel) {
        if (typeConflict(mediaModel)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        boolean added = mMediaModels.add(mediaModel);
        if (added) {
            if (mCollectionType == COLLECTION_UNDEFINED) {
                if (mediaModel.isImage()) {
                    mCollectionType = COLLECTION_IMAGE;
                } else if (mediaModel.isVideo()) {
                    mCollectionType = COLLECTION_VIDEO;
                }
            } else if (mCollectionType == COLLECTION_IMAGE) {
                if (mediaModel.isVideo()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            } else if (mCollectionType == COLLECTION_VIDEO) {
                if (mediaModel.isImage()) {
                    mCollectionType = COLLECTION_MIXED;
                }
            }
        }
        return added;
    }

    public boolean remove(MediaModel mediaModel) {
        boolean removed = mMediaModels.remove(mediaModel);
        if (removed) {
            if (mMediaModels.size() == 0) {
                mCollectionType = COLLECTION_UNDEFINED;
            } else {
                if (mCollectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
        }
        return removed;
    }

    public void overwrite(ArrayList<MediaModel> mediaModels, int collectionType) {
        if (mediaModels.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mMediaModels.clear();
        mMediaModels.addAll(mediaModels);
    }


    public List<MediaModel> asList() {
        return new ArrayList<>(mMediaModels);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (MediaModel mediaModel : mMediaModels) {

            String mediaUriString = mediaModel.getMediaUriString();
            Uri uri = Uri.parse(mediaUriString);

            uris.add(uri);
        }
        return uris;
    }

    public List<String> asListOfString() {
        List<String> paths = new ArrayList<>();
        for (MediaModel mediaModel : mMediaModels) {

            String mediaUriString = mediaModel.getMediaUriString();
            Uri uri = Uri.parse(mediaUriString);

            paths.add(PathUtils.getPath(mContext, uri));
        }
        return paths;
    }

    public boolean isEmpty() {
        return mMediaModels == null || mMediaModels.isEmpty();
    }

    public boolean isSelected(MediaModel mediaModel) {
        return mMediaModels.contains(mediaModel);
    }

    public FilterFailCause isAcceptable(MediaModel mediaModel) {
        if (maxSelectableReached()) {
            int maxSelectable = currentMaxSelectable();
            String cause;

            try {
                cause = mContext.getResources().getString(
                        R.string.lib_fs_string_error_over_count,
                        maxSelectable
                );
            } catch (Resources.NotFoundException e) {
                cause = mContext.getString(
                        R.string.lib_fs_string_error_over_count,
                        maxSelectable
                );
            } catch (NoClassDefFoundError e) {
                cause = mContext.getString(
                        R.string.lib_fs_string_error_over_count,
                        maxSelectable
                );
            }

            return new FilterFailCause(cause);
        } else if (typeConflict(mediaModel)) {
            return new FilterFailCause(mContext.getString(R.string.lib_fs_string_error_type_conflict));
        }

        return PhotoMetadataUtils.isAcceptable(mContext, mediaModel);
    }

    public boolean maxSelectableReached() {
        return mMediaModels.size() == currentMaxSelectable();
    }

    // depends
    private int currentMaxSelectable() {
        SelectorModel spec = SelectorModel.getInstance();
        if (spec.maxSelectable > 0) {
            return spec.maxSelectable;
        } else if (mCollectionType == COLLECTION_IMAGE) {
            return spec.maxImageSelectable;
        } else if (mCollectionType == COLLECTION_VIDEO) {
            return spec.maxVideoSelectable;
        } else {
            return spec.maxSelectable;
        }
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (MediaModel i : mMediaModels) {
            if (i.isImage() && !hasImage) hasImage = true;
            if (i.isVideo() && !hasVideo) hasVideo = true;
        }
        if (hasImage && hasVideo) {
            mCollectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            mCollectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            mCollectionType = COLLECTION_VIDEO;
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     */
    public boolean typeConflict(MediaModel mediaModel) {
        return  SelectorModel.getInstance().selectSingleMediaType &&  ((mediaModel.isImage() && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (mediaModel.isVideo() && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED)));
    }

    public int count() {
        return mMediaModels.size();
    }

    public int checkedNumOf(MediaModel mediaModel) {
        int index = new ArrayList<>(mMediaModels).indexOf(mediaModel);
        return index == -1 ? CheckView.UNCHECKED : index + 1;
    }
}
