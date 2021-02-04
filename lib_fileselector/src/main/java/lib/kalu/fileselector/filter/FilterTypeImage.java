package lib.kalu.fileselector.filter;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import lib.kalu.fileselector.mimetype.SelectorMimeType;
import lib.kalu.fileselector.model.MediaModel;

public class FilterTypeImage extends Filter {

    @Override
    public Set<SelectorMimeType> constraintTypes() {
        return new HashSet<SelectorMimeType>() {{
            addAll(SelectorMimeType.ofImage());
        }};
    }

    @Override
    public FilterFailCause filter(Context context, MediaModel mediaModel) {
//        if (!needFiltering(context, mediaModel))
//            return null;
//
//        Point size = PhotoMetadataUtils.getBitmapBound(context.getContentResolver(), mediaModel.getMediaUri());
//        if (size.x < mMinWidth || size.y < mMinHeight || mediaModel.mMediaSize > mMaxSize) {
//            return new FilterFailCause(FilterFailCause.DIALOG, context.getString(R.string.error_gif, mMinWidth,
//                    String.valueOf(PhotoMetadataUtils.getSizeInMB(mMaxSize))));
//        }
        return null;
    }

}
