package com.kalu.fileselector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import lib.kalu.fileselector.Selector;
import lib.kalu.fileselector.imageload.GlideImageload;
import lib.kalu.fileselector.imageload.UriImageload;
import lib.kalu.fileselector.model.CaptureModel;
import lib.kalu.fileselector.ui.selector.SelectorActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 选择图片
        findViewById(R.id.selector1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Selector.with(MainActivity.this)
                        .newBuilder()
                        .setFilterMediaTypes(new String[]{"image", "video"})
                        .setFilterMimeTypes(new String[]{"png", "mp4"})
                        .setFilterImageMaxSizeMb(10)
                        .setFilterVideoMaxSizeMb(50)
                        .showCamera(false)
                        .showFolders(true)
                        .showImageOriginal(false)
                        .setSelectMax(4)
                        .setFileProvider(new CaptureModel(getApplicationContext(), false, "test"))
                        .setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .setThumbnailScale(0.85f)
//                        .setImageload(new UriImageload())
                        .setImageload(new GlideImageload())
                        .startActivityForResult(1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == SelectorActivity.RESULT_SUCC) {
            List<String> list = Selector.obtainPathResult(data);
            Toast.makeText(getApplicationContext(), "选中：" + list.size() + "个", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1001 && resultCode == SelectorActivity.RESULT_FAIL) {
            Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
        }
    }
}