package lib.kalu.fileselector.imageload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Selection;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Keep;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import lib.kalu.fileselector.SelectionCreator;
import lib.kalu.fileselector.Selector;

@Keep
public class GlideImageload implements BaseImageload {

    private int thumbnailQuality;

    public GlideImageload() {
        thumbnailQuality = 10;
    }

    public GlideImageload(int quality) {
        if (quality < 1) {
            thumbnailQuality = 1;
        } else if (quality > 100) {
            thumbnailQuality = 100;
        } else {
            thumbnailQuality = quality;
        }
    }

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resize, resize)
                .placeholder(placeholder)
                .encodeQuality(thumbnailQuality)
                .format(DecodeFormat.PREFER_RGB_565)
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resize, resize)
                .placeholder(placeholder)
                .encodeQuality(thumbnailQuality)
                .format(DecodeFormat.PREFER_RGB_565)
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resizeX, resizeY)
                .encodeQuality(100)
                .format(DecodeFormat.PREFER_RGB_565)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter();

        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .transition(BitmapTransitionOptions.withCrossFade(500))
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resizeX, resizeY)
                .encodeQuality(100)
                .format(DecodeFormat.PREFER_RGB_565)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter();

        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}
