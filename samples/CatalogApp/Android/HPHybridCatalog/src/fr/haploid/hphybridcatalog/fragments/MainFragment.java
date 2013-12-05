package fr.haploid.hphybridcatalog.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fr.haploid.androidnativebridge.customviews.OverScrollingWebView;
import fr.haploid.androidnativebridge.fragments.HTMLFragment;
import fr.haploid.hphybridcatalog.R;

public class MainFragment extends HTMLFragment {

	protected static String JSNameTestCallback = "nameTestCallback" ;
	protected static String JSNameTestCallbackAsync = "nameTestCallbackAsync";

	private Button callBackButton,callbackAsyncButton,validButton;
	private EditText nativeEditText;



	@Override
	protected int getLayoutToInflate() {
		return R.layout.main_fragment;
	}

	@Override
	protected void setUpViews(View rootView) {
		//webView = (OverScrollingWebView) rootView.findViewById(R.id.webView);
		super.setUpViews(rootView);
		callBackButton = (Button) rootView.findViewById(R.id.callBackButton);
		callbackAsyncButton = (Button) rootView.findViewById(R.id.callBackAsyncButton);
		validButton = (Button) rootView.findViewById(R.id.validButton);
		nativeEditText = (EditText) rootView.findViewById(R.id.nativeEditText);
	}

	@Override
	protected void setUpListeners() {
		callBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				JSONObject j = new JSONObject();
				try {
					ArrayList<Integer> l = new ArrayList<Integer>();
					l.add(51);
					l.add(42);
					j.put(kJSType, JSTypeEvent);
					j.put(kJSName, JSNameTestCallback);
					j.put(kJSCallbackID, JSNameTestCallback);
					j.put(kJSValue,new JSONArray(l));
					executeScriptInWebView(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		callbackAsyncButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				JSONObject j = new JSONObject();
				try {
					ArrayList<String> l = new ArrayList<String>();
					l.add("Bonjour");
					l.add("Guillaume");
					j.put(kJSType, JSTypeEvent);
					j.put(kJSName, JSNameTestCallbackAsync);
					j.put(kJSCallbackID, JSNameTestCallbackAsync);
					j.put(kJSValue,new JSONArray(l));
					executeScriptInWebView(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		validButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				JSONObject j = new JSONObject();
				try {
					j.put(kJSType, JSTypeLog);
					j.put(kJSValue,nativeEditText.getText().toString());
					executeScriptInWebView(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	@JavascriptInterface
	public boolean handleMessageSentByJavaScript(String messageJS)
	{	
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(messageJS);
			if(jsonObj != null)
			{
				String type = jsonObj.optString(kJSType);

				//TYPE = EVENT
				if(type != null && type.length() >0 && type.equals(JSTypeEvent))
				{
					String name = jsonObj.optString(kJSName);
					if(name != null && name.length() >0 && name.equals(JSNameTestCallback))
					{

						final String value = jsonObj.optString(kJSValue);
						if(value != null)
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									nativeEditText.setText(value);
								}
							});
						String callbackId = jsonObj.optString(kJSCallbackID);

						if(callbackId != null && callbackId.length() >0)
						{
							changeNameForWebCallBack(callbackId,value);
						}
						return true;
					}
					else if(name != null && name.length() >0 && name.equals(JSNameTestCallbackAsync))
					{
						final String value = jsonObj.optString(kJSValue);
						if(value != null)
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									nativeEditText.setText(value);
								}
							});

						String callbackId = jsonObj.optString(kJSCallbackID);
						if(callbackId != null && callbackId.length() >0)
						{
							changeNameForWebCallBackAsync(callbackId,value);
						}
						return true;

					}
				}
				//CALLBACKS
				else if(type != null && type.length () >0 && type.equals(JSTypeCallBack))
				{
					String callbackID = jsonObj.optString(kJSCallbackID);
					if(callbackID != null && callbackID.length() >0 && callbackID.equals(JSNameTestCallback))
					{
						String value = jsonObj.optString(kJSParams);
						if(value != null)
							Toast.makeText(mContext, "Callback with value "+value, Toast.LENGTH_SHORT).show();
						return true;
					}
					else if(callbackID != null && callbackID.length() >0 && callbackID.equals(JSNameTestCallbackAsync))
					{
						String value = jsonObj.optString(kJSParams);
						if(value != null)
							Toast.makeText(mContext, "Callback ASYNC with value "+value, Toast.LENGTH_SHORT).show();
						return true;
					}
				}
			}
		} catch (JSONException e1) {
			Log.e(getClass().getName(),"JSON EXCEPTION FOR JSON : "+messageJS);
			e1.printStackTrace();
		}
		return super.handleMessageSentByJavaScript(messageJS);
	}

	private void changeNameForWebCallBack(String callbackId, String value) {
		String nValue = "Je m'appelle "+value;
		sendCallbackResponse(callbackId, nValue);
	}


	private void changeNameForWebCallBackAsync(String callbackId, String value) {
		String nValue = "Je m'appelle "+value;

		new GoogleAsyncTask(this,callbackId,nValue).execute("http://google.fr");
	}
}