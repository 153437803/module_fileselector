package lib.kalu.fileselector.imageload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Keep;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

@Keep
public class GlideImageload implements BaseImageload {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resize, resize)
                .placeholder(placeholder)
                .priority(Priority.LOW)
                .centerCrop();

        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .transition(BitmapTransitionOptions.withCrossFade(100))
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
                .priority(Priority.LOW)
                .centerCrop();

        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .transition(BitmapTransitionOptions.withCrossFade(100))
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String uriString) {

        if (TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);

        RequestOptions requestOptions = new RequestOptions()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
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
                .priority(Priority.HIGH)
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
