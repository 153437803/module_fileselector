package lib.kalu.fileselector.filter;

import android.content.Context;
import android.net.Uri;

import java.util.Set;

import lib.kalu.fileselector.mimetype.SelectorMimeType;
import lib.kalu.fileselector.model.MediaModel;

/**
 * description: 过滤器
 * create by Administrator on 2020-03-26
 */
public abstract class Filter {

    // 最少数量
    public static final int MIN = 0;

    // 最多数量
    public static final int MAX = Integer.MAX_VALUE;

    // 文件大小
    public static final int K = 1024;

    // 文件类型集合
    protected abstract Set<SelectorMimeType> constraintTypes();

    public abstract FilterFailCause filter(Context context, MediaModel mediaModel);

    protected boolean needFiltering(Context context, MediaModel mediaModel) {
        for (SelectorMimeType type : constraintTypes()) {

            String mediaUriString = mediaModel.getMediaUriString();
            Uri uri = Uri.parse(mediaUriString);

            if (type.checkType(context.getContentResolver(), uri)) {
                return true;
            }
        }
        return false;
    }
}
