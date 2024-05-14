package lib.kalu.avselector.provider;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public class FileProvider extends androidx.core.content.FileProvider {

    private static Context mContext;

    public static final Context getApplicationContext() {
        return mContext;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext().getApplicationContext();
        return super.onCreate();
    }
}
