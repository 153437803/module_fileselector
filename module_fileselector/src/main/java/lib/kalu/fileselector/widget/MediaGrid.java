package lib.kalu.fileselector.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.
RecyclerView;

import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.util.PhotoMetadataUtils;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {

    private ImageView mThumbnail;
    private CheckView mCheckView;
    private TextView mGifTag;

    private MediaModel mMedia;
    private PreBindInfo mPreBindInfo;
    private OnMediaGridClickListener mListener;

    public MediaGrid(Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.activity_selector_type_item, this, true);

        mThumbnail = (ImageView) findViewById(R.id.media_thumbnail);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        mGifTag = (TextView) findViewById(R.id.gif);

        mThumbnail.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(Context context, MediaModel mediaModel) {
        mMedia = mediaModel;
        setGifTag();
        initCheckView();
        setImage();
        setDuration(context);
        setSize();
    }

    public MediaModel getMedia() {
        return mMedia;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mMedia.isGif()) {
            SelectorModel.getInstance().baseImageload.loadGifThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getMediaUriString());
        } else {
            SelectorModel.getInstance().baseImageload.loadThumbnail(getContext(), mPreBindInfo.mResize,
                    mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getMediaUriString());
        }
    }

    private void setDuration(Context context) {

        TextView textView = (TextView) findViewById(R.id.duration);
        if (mMedia.isVideo()) {
            textView.setVisibility(VISIBLE);

            String time = DateUtils.formatElapsedTime(mMedia.mMediaDuration / 1000);
            String times = context.getResources().getString(R.string.lib_fs_string_long, time);

            textView.setText(times);
        } else {
            textView.setVisibility(GONE);
        }
    }

    private void setSize() {

        TextView textView = (TextView) findViewById(R.id.size);
        textView.setVisibility(VISIBLE);
        String sizeToString = PhotoMetadataUtils.getSizeToString(mMedia.mMediaSize);
        textView.setText(sizeToString);
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public void removeOnMediaGridClickListener() {
        mListener = null;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, MediaModel mediaModel, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, MediaModel mediaModel, RecyclerView.ViewHolder holder);
    }

    public static class PreBindInfo {
        int mResize;
        Drawable mPlaceholder;
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable,
                           RecyclerView.ViewHolder viewHolder) {
            mResize = resize;
            mPlaceholder = placeholder;
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }

}
