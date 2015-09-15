package com.anykey.balala.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.anykey.balala.AppBalala;
import com.anykey.balala.CommonUrlConfig;
import com.anykey.balala.R;
import com.anykey.balala.model.ExampleDBModel;
import com.anykey.balala.service.BackgroundService;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.netWorkUtil.GetJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 15/8/26.
 *
 */
public class MainActivityDemo extends BaseActivity{
    private TextView info;
    private ImageView textimg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info_text);
        textimg = (ImageView)findViewById(R.id.imageView);
        Response();
        headerLayout.showTitle("主界面");
        headerLayout.showLeftBackButton();
        Intent serIntent = new Intent(this,BackgroundService.class);
        startService(serIntent);
        String str = "http://b.hiphotos.baidu.com/image/h%3D200/sign=1eed4d41dd33c895b97e9f7be1117397/0b7b02087bf40ad17310fd35532c11dfa8ecce6b.jpg";
        ImageLoader.getInstance().displayImage(str, textimg, AppBalala.options);
//            ActiveAndroid.beginTransaction();
//            try {
//                for(int i = 0;i<10 ;i++){
//                    ExampleDBModel  loginInfo = new ExampleDBModel();
//                    loginInfo.loginId = profile.getId();
//                    loginInfo.name = profile.getName()+i;
//                    loginInfo.save();
//                }
//                ActiveAndroid.setTransactionSuccessful();
//            }
//            finally {
//                ActiveAndroid.endTransaction();
//            }
//
        List<ExampleDBModel> exampleDBModel = new Select().from(ExampleDBModel.class).limit(10).orderBy("id ASC").execute();
        if (exampleDBModel.size() <= 0){
            return;
        }else{
            for (int i = 0;i<exampleDBModel.size(); i++) {
                DebugLogs.e("-----数据库读取到的数据 ---->"+exampleDBModel.get(i).name.toString());
            }
        }
    }
    public static ExampleDBModel getRandom() {
        return new Select().from(ExampleDBModel.class).limit(10).orderBy("id desc").executeSingle();//只查询一行
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取返回结果
     */
    private void Response() {
        Map params = new HashMap();
        params.put("indexParam", "0");
            params.put("userId", "22");
        GetJson.Callback callback = new GetJson.Callback() {
            @Override
            public void onFinish(String response) {
                info.setText("返回结果:"+response);
                //处理结果
            }

            @Override
            public void onError(VolleyError error) {
                //处理错误
            }
        };
        GetJson getJson = new GetJson(MainActivityDemo.this,callback,true,"正在努力加载中...");
        //getJson.setConnection(Request.Method.POST, CommonUrlConfig.texturl,params);
    }
}
