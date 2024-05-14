package com.kalu.fileselector;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import lib.kalu.avselector.Selector;
import lib.kalu.avselector.imageload.GlideImageload;
import lib.kalu.avselector.model.CaptureModel;
import lib.kalu.avselector.ui.selector.SelectorActivity;

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
                        .setImageload(new GlideImageload())
                        .setThumbnailQuality(100)
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