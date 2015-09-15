package net.dev.mylib.netWorkUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import net.dev.mylib.DebugLogs;
import net.dev.mylib.ToastUtils;
import net.dev.mylib.view.LoadingDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * @author x.j 
 * volley(网络请求)
 */
public class GetJson {
	private GetJson.Callback mCallback;
	private Context mContext;
	
	public GetJson(){
		
	}

	public GetJson(GetJson.Callback callback) {
		setCallback(callback);
	}


	public GetJson(Context context, GetJson.Callback callback) {
		this.mContext = context;
		setCallback(callback);
	}
	
	public GetJson(Context context, GetJson.Callback callback,boolean isLoading){
		this.mContext = context;
		setCallback(callback);
		if (isLoading) {
			LoadingDialog.showSysLoadingDialog(mContext,"");
		}
	}

    /**
     * 请求模型，
     * @param context  上下文
     * @param callback  回调
     * @param isLoading 是否需求加载界面
     * @param title  加载显示文字
     */
	public GetJson(Context context, GetJson.Callback callback,boolean isLoading,String title){
		this.mContext = context;
		setCallback(callback);
		if (isLoading) {
			LoadingDialog.showSysLoadingDialog(mContext, title);
		}
	}
	
	private void setCallback(GetJson.Callback callback){
		this.mCallback = callback;
	}

	@SuppressWarnings("rawtypes")
	public void setConnection(int Method, String url, final Map params) {
		url = restructureURL(Method,url,params);
		DebugLogs.i("url is "+url);
		RequestManager manager = RequestManager.getInstance(mContext);// 创建管理线程池
		StringRequest request = new StringRequest(Method, url,new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (mCallback != null) {
							mCallback.onFinish(response);
							LoadingDialog.cancelLoadingDialog();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (DebugLogs.isDebug) {
							ToastUtils.showToast(mContext, "Network error"+error.toString());
						}
						LoadingDialog.cancelLoadingDialog();
						if (mCallback != null) {
							mCallback.onError(error);
						}
					}
				}) {
			@SuppressWarnings("unchecked")
			protected Map getParams() throws AuthFailureError {
				return params;
			}
		};
		manager.addToRequestQueue(request, getClass().getSimpleName());
	}

	public interface Callback {
		/** 请求结果返回 **/
		public void onFinish(String response);
		/** 请求错误返回 **/
		public void onError(VolleyError error);
	}


	protected String restructureURL(int method,String url,Map<String,String> params){
		if(method == Request.Method.GET){
			url = url+"?"+encodeParameters(params);
		}
		return url;
	}

	private String encodeParameters(Map<String, String> params) {
		if(params == null){
			return "";
		}
		StringBuilder encodedParams = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			encodedParams.append(entry.getKey());
			encodedParams.append('=');
			encodedParams.append(entry.getValue());
			encodedParams.append('&');
		}
		String result = encodedParams.toString();
		return result.substring(0,result.length()-1);
}



}
