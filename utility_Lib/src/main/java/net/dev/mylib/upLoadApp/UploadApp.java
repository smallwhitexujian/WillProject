package net.dev.mylib.upLoadApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xujian on 15/9/1.
 * 更新APP下载
 */
public class UploadApp {
    private ProgressDialog mpDialog;// 创建精度条
    private int fileSize;// 设置文件大小
    private int downLoadFileSize;// 当前已下载的文件的大小
    private Context mContext;
    // APK的安装路径
    private static final String savePath = "/sdcard/updatedemo/"; //保存下载文件的路径
    private static final String saveFileName = savePath + "UpdateDemo.apk";//保存下载文件的名称

    /**
     * 提示用户更新
     *
     * @param mcontext
     * @param url 下载链接
     * @param str 更新内容
     */
    public void uploadApp(Context mcontext, String str, final String url) {
        this.mContext = mcontext;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("有新的版本升级，是否下载安装？\n" + str);
        builder.setTitle("系统版本更新");// str可以提示的内容显示
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mpDialog = new ProgressDialog(mContext);
                mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mpDialog.setTitle("提示");
                mpDialog.setMessage("正在下载中，请稍后");
                mpDialog.setIndeterminate(false);// 是进度条是否明确
                mpDialog.setCancelable(false);// 点击返回按钮的时候无法取消对话框
                mpDialog.setCanceledOnTouchOutside(false);// 点击对话框外部取消对话框显示
                mpDialog.setProgress(0);// 设置初始进度条为0
                mpDialog.incrementProgressBy(1);// 设置进度条增涨。
                mpDialog.show();
                new Thread() {
                    public void run() {
                        String apkUrl = url;// 下载APK的url
                        URL url = null;
                        try {
                            url = new URL(apkUrl);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            InputStream in = con.getInputStream();
                            fileSize = con.getContentLength();// 获取下载文件的长度
                            File file = new File(savePath);
                            if (!file.exists()) {
                                file.mkdir();
                                File fileOut = new File(saveFileName);// 下载文件的存放地址
                                FileOutputStream out = new FileOutputStream(fileOut);
                                byte[] bytes = new byte[1024];
                                downLoadFileSize = 0;
                                sendMsg(0);// sendMeg为0的时候显示下载完成
                                int c;
                                while ((c = in.read(bytes)) != -1) {
                                    out.write(bytes, 0, c);
                                    downLoadFileSize += c;
                                    sendMsg(1);
                                }
                                in.close();
                                out.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMsg(2);
                    }
                }.start();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    // 安装apk方法
    private void installApk(String filename) {
        File file = new File(filename);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        mContext.startActivity(intent);
        if (mpDialog != null) {
            mpDialog.cancel();
        }
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        mpDialog.setMax(100);
                        break;
                    case 1:
                        int result = downLoadFileSize * 100 / fileSize;
                        mpDialog.setProgress(result);
                        break;
                    case 2:
                        mpDialog.setMessage("文件下载完成");
                        installApk(saveFileName);
                        break;
                    case -1:
                        String error = msg.getData().getString("error");
                        mpDialog.setMessage(error);
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };
}
