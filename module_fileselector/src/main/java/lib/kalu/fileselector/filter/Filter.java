package lib.kalu.fileselector.filter;

import android.content.Context;
import android.net.Uri;

import lib.kalu.fileselector.model.MediaModel;

/**
 * description: 过滤器
 * create by Administrator on 2020-03-26
 */
public abstract class Filter {

    public static final int FILE_MIN_COUNT = 0;
    public static final int FILE_MAX_COUNT = 10;
    public static final int IMAGE_MAX_SIZE = 10 * 1024 * 1024; // 10M
    public static final int VIDEO_MAX_SIZE = 50 * 1024 * 1024; // 50M

    public abstract FilterFailCause filter(Context context, MediaModel mediaModel);
}
