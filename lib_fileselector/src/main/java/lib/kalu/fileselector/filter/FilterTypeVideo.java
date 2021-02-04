package lib.kalu.fileselector.filter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import lib.kalu.fileselector.mimetype.SelectorMimeType;
import lib.kalu.fileselector.model.MediaModel;

public class FilterTypeVideo extends Filter {


    @Override
    public Set<SelectorMimeType> constraintTypes() {
        Log.d("filtertypevideo", "constraintTypes => ");
        return new HashSet<SelectorMimeType>() {{
            addAll(SelectorMimeType.ofVideo());
        }};
    }

    @Override
    public FilterFailCause filter(Context context, MediaModel mediaModel) {
        Log.d("filtertypevideo", "filter => ");
        return null;
    }

    @Override
    protected boolean needFiltering(Context context, MediaModel mediaModel) {

        ContentResolver contentResolver = context.getContentResolver();

        String mediaUriString = mediaModel.getMediaUriString();
        Uri uri = Uri.parse(mediaUriString);

        String type1 = contentResolver.getType(uri);
        Log.d("filtertypevideo", "needFiltering => url = " + mediaUriString + ", type = " + type1);

        for (SelectorMimeType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), uri)) {
                return true;
            }
        }
        return false;
    }
}
