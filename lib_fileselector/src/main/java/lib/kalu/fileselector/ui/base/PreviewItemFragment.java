package lib.kalu.fileselector.ui.base;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import lib.kalu.fileselector.R;
import lib.kalu.fileselector.listener.OnFragmentInteractionListener;
import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.model.SelectorModel;
import lib.kalu.fileselector.util.PhotoMetadataUtils;
import lib.kalu.fileselector.util.UriUtil;
import lib.kalu.fileselector.widget.zoomimage.ImageViewTouch;
import lib.kalu.fileselector.widget.zoomimage.ImageViewTouchBase;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private OnFragmentInteractionListener mListener;

    public static PreviewItemFragment newInstance(MediaModel mediaModel) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGS_ITEM, mediaModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lib_selector_activity_preview_type_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MediaModel mediaModel = (MediaModel) getArguments().getSerializable(ARGS_ITEM);
        if (mediaModel == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.video_play_button);
        if (mediaModel.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mediaUriString = mediaModel.getMediaUriString();
                    Uri uri = Uri.parse(mediaUriString);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = (ImageViewTouch) view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        String mediaUriString = mediaModel.getMediaUriString();
        Uri uri = Uri.parse(mediaUriString);

        Point size = PhotoMetadataUtils.getBitmapSize(uri, getActivity());

        boolean gif = mediaModel.isGif();
        String realPathFromUri = UriUtil.getRealPathFromUri(getContext(), uri);
        Log.e("previewitemfragment", "onViewCreated => gif = " + gif);
        Log.e("previewitemfragment", "onViewCreated => mediaUri = " + uri);
        Log.e("previewitemfragment", "onViewCreated => realPathFromUri = " + realPathFromUri);

        if (gif) {
            SelectorModel.getInstance().baseImageload.loadGifImage(getContext(), size.x, size.y, image, mediaUriString);
        } else {
            SelectorModel.getInstance().baseImageload.loadImage(getContext(), size.x, size.y, image, mediaUriString);
        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
