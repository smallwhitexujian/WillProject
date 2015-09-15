package com.anykey.balala.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import com.anykey.balala.R;

import android.widget.ImageView;
import android.widget.ListView;
import com.anykey.balala.Utils.PictureObtain;
import com.anykey.balala.adapter.CommonAdapter;
import com.anykey.balala.adapter.ViewHolder;
import com.anykey.balala.model.DynamicModel;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.cache.fileCheanCache.FileCache;
import net.dev.mylib.time.DateUtil;
import net.dev.mylib.view.ActionSheetDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanli on 15/9/9.
 * 动态页面
 */
public class DynamicActivity extends BaseActivity implements View.OnClickListener {

    //控件定义
    private ListView listView;
    private ImageView button;
    private CommonAdapter mAdapter;

    private List<DynamicModel> mDatas;
    private PictureObtain mObtain;

    private static final String PHOTO_TEMP_NAME = "/balala/img/balabalaphotocache.jpg";
    private static final int SET_ADD_PHOTO_CAMERA = 1101,
            SET_ADD_PHOTO_ALBUM = 1102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                new ActionSheetDialog(DynamicActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        camear_pic();
                                    }
                                })
                        .addSheetItem("从手机相册选择", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //choose_pic();
                                        mObtain.getLocalPicture(DynamicActivity.this, SET_ADD_PHOTO_ALBUM);
                                    }
                                }).show();
                break;
        }
    }



    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    //拍照
    private void camear_pic() {
        Intent intentFromCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (FileCache.isSDCardEnable()) {
            File destDir = new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/balala/img/"
                    + DateUtil.DateFormat(DateUtil.GetDateTimeNowlong(),
                    "yyyy_MM_dd") + "/");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            intentFromCapture
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    PHOTO_TEMP_NAME)));
        }
        startActivityForResult(intentFromCapture, SET_ADD_PHOTO_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SET_ADD_PHOTO_CAMERA:
                    // 拍照上传
                    getNewPhotoFromCamera(data);
                    break;
                case SET_ADD_PHOTO_ALBUM:
                    // 相册选取

                    Uri uri = data.getData();
                    String path = mObtain.getRealPathFromURI(DynamicActivity.this, uri);
                    Intent sendintent = new Intent(mContext, SendDynamicActivity.class);
                    sendintent.putExtra("filePath", path);
                    startActivityForResult(sendintent, 1);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拍照后的照片
     */
    private void getNewPhotoFromCamera(Intent data) {
        Intent sendintent = new Intent(mContext, SendDynamicActivity.class);
        sendintent.putExtra("filePath", Environment
                .getExternalStorageDirectory() + PHOTO_TEMP_NAME);
        startActivityForResult(sendintent, 1);
    }
}
