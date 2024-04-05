package lib.kalu.fileselector.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.fragment.app.Fragment;

import lib.kalu.fileselector.model.CaptureModel;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaStoreCompat {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private CaptureModel mCaptureModel;
    private Uri mCurrentPhotoUri;
    private String mCurrentPhotoPath;

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void setCaptureStrategy(CaptureModel strategy) {
        mCaptureModel = strategy;
    }

    public void dispatchCaptureIntent(Context context, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {

            if (Build.VERSION.SDK_INT >= 29) {

                String status = Environment.getExternalStorageState();
                // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    Uri insert = mContext.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                    mCurrentPhotoUri = insert;
                } else {
                    Uri insert = mContext.get().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
                    mCurrentPhotoUri = insert;
                }

                mCurrentPhotoPath = "path";
            } else {

                File photoFile = createCaptureFile(context);

                mCurrentPhotoPath = photoFile.getAbsolutePath();
                mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(),  mCaptureModel.authority, photoFile);
            }

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ResolveInfo> resInfoList = context.getPackageManager()
                        .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, mCurrentPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            if (mFragment != null) {
                mFragment.get().startActivityForResult(captureIntent, requestCode);
            } else {
                mContext.get().startActivityForResult(captureIntent, requestCode);
            }
        }
    }

    private File createCaptureFile(Context context) {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("capture_%s.jpg", timeStamp);
        File storageDir;
        if (mCaptureModel.isPublic) {
//            storageDir = Environment.getExternalStoragePublicDirectory(
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) storageDir.mkdirs();
        } else {
            storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (mCaptureModel.directory != null) {
            storageDir = new File(storageDir, mCaptureModel.directory);
            if (!storageDir.exists()) storageDir.mkdirs();
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        Log.e("kalu", storageDir.getAbsolutePath());
        Log.e("kalu", imageFileName);
        // Avoid joining path components manually
        File tempFile = new File(storageDir, imageFileName);

        if (tempFile.exists()) {
            tempFile.delete();
        }
        tempFile.mkdir();

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            Log.e("kalu", "null");
            return null;
        }

        Log.e("kalu", tempFile.getAbsolutePath());
        return tempFile;
    }

    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}
