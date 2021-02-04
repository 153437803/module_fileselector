package lib.kalu.fileselector.filter;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;

import java.util.HashSet;
import java.util.Set;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.mimetype.SelectorMimeType;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.util.PhotoMetadataUtils;

public class FilterTypeGif extends Filter {

    private int mMinWidth;
    private int mMinHeight;
    private int mMaxSize;

    public FilterTypeGif(int minWidth, int minHeight, int maxSizeInBytes) {
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        mMaxSize = maxSizeInBytes;
    }

    @Override
    public Set<SelectorMimeType> constraintTypes() {
        return new HashSet<SelectorMimeType>() {{
            add(SelectorMimeType.GIF);
        }};
    }

    @Override
    public FilterFailCause filter(Context context, MediaModel mediaModel) {
        if (!needFiltering(context, mediaModel))
            return null;

        String mediaUriString = mediaModel.getMediaUriString();
        Uri uri = Uri.parse(mediaUriString);

        Point size = PhotoMetadataUtils.getBitmapBound(context.getContentResolver(), uri);
        if (size.x < mMinWidth || size.y < mMinHeight || mediaModel.mMediaSize > mMaxSize) {
            return new FilterFailCause(FilterFailCause.DIALOG, context.getString(R.string.error_gif, mMinWidth,
                    String.valueOf(PhotoMetadataUtils.getSizeInMB(mMaxSize))));
        }
        return null;
    }

}
