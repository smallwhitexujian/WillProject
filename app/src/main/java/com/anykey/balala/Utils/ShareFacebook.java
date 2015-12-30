package com.anykey.balala.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.anykey.balala.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import net.dev.mylib.DebugLogs;

/**
 * Created by xujian on 15/9/9.
 * Facebook分享
 */
public class ShareFacebook {
    private Context mcontext;
    private boolean canPresentShareDialog;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private Profile profile;
    private FacebookCallback<Sharer.Result> shareCallback ;
    public ShareFacebook(Context context, Activity activity) {
        this.mcontext = context;
        callbackManager = CallbackManager.Factory.create();
        profile = Profile.getCurrentProfile();
        FacebookSdk.sdkInitialize(context);
        shareCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {
                DebugLogs.e("HelloFacebook" + "Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                DebugLogs.e("HelloFacebook" + String.format("Error: %s", error.toString()));
                String title = mcontext.getString(R.string.error);
                String alertMessage = error.getMessage();
                showResult(title, alertMessage);
            }

            @Override
            public void onSuccess(Sharer.Result result) {
                DebugLogs.e("HelloFacebook" + "Success!");
                if (result.getPostId() != null) {
                    String title = mcontext.getString(R.string.success);
                    String id = result.getPostId();
                    String alertMessage = mcontext.getString(R.string.successfully_posted_post, id);
                    showResult(title, alertMessage);
                }
            }
        };
        shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, shareCallback);
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
    }
    private void showResult(String title, String alertMessage) {
        new AlertDialog.Builder(mcontext)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    /**
     * 分享内容加链接
     */
    public void postStatusUpdate(String sharetitle,String Description,String url) {
        if (url == null){
            String url2 = " http://goo.gl/ExzX9I";
            sharetitle = mcontext.getString(R.string.shareTitle);
            Description =String.format(mcontext.getString(R.string.shareDescription),url2);
            url = "http://goo.gl/ExzX9I";
        }
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(sharetitle)
                .setContentDescription(Description)
                .setContentUrl(Uri.parse(url))
                .build();
        if (canPresentShareDialog) {
            if (shareDialog.getShouldFailOnDataError()){//正常显示分享
                shareDialog.show(linkContent);
            }else{
                shareDialog.show(linkContent,ShareDialog.Mode.WEB);
            }
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        }
    }

    public void postStatusUpdate(String sharetitle, String Description, String url, Bitmap bitmap) {
        if (url == null) {
            String url2 = "http://goo.gl/ExzX9I";
            sharetitle = mcontext.getString(R.string.shareTitle);
            Description = String.format(mcontext.getString(R.string.shareDescription), url2);
            url = "http://goo.gl/ExzX9I";
        }

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(sharetitle)
                .setContentDescription(Description)
                .setContentUrl(Uri.parse(url))
                .build();
        if (canPresentShareDialog) {
            if (bitmap != null) {
                postPhoto(bitmap);
            } else {
                shareDialog.show(linkContent);
            }
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        }
    }

    /**
     * 上传图片 到Facebook
     * 这些照片必须小于12MB的大小
     * @param bitmap
     */
    public void postPhoto(Bitmap bitmap){
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        if (canPresentShareDialog) {
            if (!shareDialog.getShouldFailOnDataError()){
                shareDialog.show(content,ShareDialog.Mode.WEB);
            }else{
                shareDialog.show(content);
            }
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(content, shareCallback);
        }
    }


    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }
}
