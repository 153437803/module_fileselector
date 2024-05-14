package lib.kalu.avselector.imageload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public interface BaseImageload {

    void loadThumbnail(Context context, int thumbnailQuality, Drawable placeholder, ImageView imageView, String uri);

    void loadGifThumbnail(Context context, int thumbnailQuality, Drawable placeholder, ImageView imageView, String uri);

    void loadImage(Context context, ImageView imageView, String uri);

    void loadGifImage(Context context, ImageView imageView, String uri);

    boolean supportAnimatedGif();
}
