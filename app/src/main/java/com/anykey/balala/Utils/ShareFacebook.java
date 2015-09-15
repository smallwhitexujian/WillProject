package com.anykey.balala.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;

import com.anykey.balala.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
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
    private FacebookCallback<Sharer.Result> shareCallback;
    public ShareFacebook(Context context,Activity activity){
        this.mcontext = context;
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

            private void showResult(String title, String alertMessage) {
                new AlertDialog.Builder(mcontext)
                        .setTitle(title)
                        .setMessage(alertMessage)
                        .setPositiveButton(R.string.ok, null)
                        .show();
            }
        };
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager,shareCallback);
        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow( ShareLinkContent.class);
    }

   public void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Hello Facebook")
                .setContentDescription("The 'Hello Facebook' sample  showcases simple Facebook integration")
                .setContentUrl(Uri.parse("http://www.baidu.com"))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }
}
