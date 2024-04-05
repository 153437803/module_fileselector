package lib.kalu.fileselector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import lib.kalu.fileselector.ui.selector.SelectorActivity;

@Keep
public final class Selector {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private Selector(Activity activity) {
        this(activity, null);
    }

    private Selector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private Selector(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static Selector with(Activity activity) {
        return new Selector(activity);
    }

    public static Selector with(Fragment fragment) {
        return new Selector(fragment);
    }

    public static List<Uri> obtainResult(Intent data) {
        return (List<Uri>) data.getSerializableExtra(SelectorActivity.EXTRA_RESULT_SELECTION);
    }

    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(SelectorActivity.EXTRA_RESULT_SELECTION_PATH);
    }

    public static boolean obtainOriginalState(Intent data) {
        return data.getBooleanExtra(SelectorActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
    }

    public SelectionCreator newBuilder() {
        return new SelectionCreator(this);
    }


    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
