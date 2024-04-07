package lib.kalu.fileselector.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import lib.kalu.fileselector.model.MediaModel;
import lib.kalu.fileselector.ui.priview.PreviewFragment;

import java.util.ArrayList;
import java.util.List;

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<MediaModel> mMediaModels = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewFragment.newInstance(mMediaModels.get(position));
    }

    @Override
    public int getCount() {
        return mMediaModels.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public MediaModel getMediaItem(int position) {
        return mMediaModels.get(position);
    }

    public void addAll(List<MediaModel> mediaModels) {
        mMediaModels.addAll(mediaModels);
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

}
