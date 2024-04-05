package lib.kalu.fileselector.imageload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Keep;

/**
 * description: 原生图片加载, 不支持GIF
 * create by Administrator on 2020-03-26
 */
@Keep
public class UriImageload implements BaseImageload {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, String uriString) {

        if (null == imageView)
            return;

        if(TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);
        if (null == uri)
            return;

        imageView.setImageURI(uri);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, String uriString) {

        if (null == imageView)
            return;

        if(TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);
        if (null == uri)
            return;

        imageView.setImageURI(uri);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String uriString) {

        if (null == imageView)
            return;

        if(TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);
        if (null == uri)
            return;

        imageView.setImageURI(uri);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, String uriString) {

        if (null == imageView)
            return;

        if(TextUtils.isEmpty(uriString))
            return;

        Uri uri = Uri.parse(uriString);
        if (null == uri)
            return;

        imageView.setImageURI(uri);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }

}
