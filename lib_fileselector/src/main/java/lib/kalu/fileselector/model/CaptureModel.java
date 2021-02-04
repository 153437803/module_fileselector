package lib.kalu.fileselector.model;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.StringRes;

import java.io.Serializable;

/**
 * description: 选择拍照模型
 * create by Administrator on 2020-03-26
 */
@Keep
public class CaptureModel implements Serializable {

    public final boolean isPublic;
    public final String authority;
    public final String directory;

    public CaptureModel(boolean isPublic, String authority) {
        this(isPublic, authority, null);
    }

    public CaptureModel(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }

    public CaptureModel(Context context, boolean isPublic, @StringRes int authority, String directory) {
        this.isPublic = isPublic;
        this.authority = context.getResources().getString(authority);
        this.directory = directory;
    }
}
