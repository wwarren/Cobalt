package fr.cobaltians.cobalt.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fr.cobaltians.cobalt.customviews.IGestureListener;
import fr.cobaltians.cobalt.customviews.GestureWebView;

/**
 * {@link HTMLFragment} that may have Swipe features if those are activated.
 * @extends HTMLFragment
 * @implements IScrollListener
 * @author Sebastien
 */
public class HTMLFlipperFragment extends HTMLFragment implements IGestureListener {
	
	private static String JSNameSwipeLeft = "swipeLeft";
	private static String JSNameSwipeRight = "swipeRight";
	private boolean swipeActive = false;
	
	@Override
	public void onStart() {
		super.onStart();
		//WebView has been added, set up its listener
		if(mWebView != null)
		{
			((GestureWebView) mWebView).setGestureListener(this);
		}
		
		setFeaturesWantedActive();
	}
	
	@Override
	protected void addWebview() {
		if(mWebView == null) {
			mWebView = new GestureWebView(mContext);
			setWebViewSettings(this);
		}

		if(webViewPlaceholder != null) {
			webViewPlaceholder.addView(mWebView);
		}
		else if(mDebug) {
			Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}		
	}
	
	@Override
	protected void removeWebviewFromPlaceholder() {
		if (webViewPlaceholder != null) {
			if(mWebView != null) {
				// Remove the WebView from the old placeholder
				webViewPlaceholder.removeView(mWebView);
			}
		}
		else if(mDebug) {
			Log.e(getClass().getSimpleName(), "ERROR : you must set up webViewPlaceholder in setUpViews !");
		}
	}
	
	/**
	 * Enable the Swipe feature
	 * The mSwipeWebView must have been set before 
	 */
	public void enableSwipe() {
		if(mWebView != null) {
			swipeActive = true;
		}
		else if(mDebug) {
			Log.e(getClass().getSimpleName(), "ERROR : impossible to enable Swipe feature while mSwipeWebView is null.");
		}
	}

	/**
	 * Disable the Swipe feature
	 * The mSwipeWebView must have been set before 
	 */
	public void disableSwipe() {
		if(mWebView != null) {
			swipeActive = false;
		}
		else if(mDebug) {
			Log.e(getClass().getSimpleName(), "ERROR : impossible to disable Swipe feature while mSwipeWebView is null.");
		}
	}
	
	/**
	 * This method returns a boolean to know if the Swipe feature is active
	 * @return true if Swipe is active, false otherwise
	 */
	public boolean isSwipeActive() {
		return swipeActive;
	}
	
	private void setFeaturesWantedActive()
	{
		if(mWebView != null) {
			if(getArguments() != null && getArguments().containsKey(kSwipe)) {
				if(getArguments().getBoolean(kSwipe)) {
					enableSwipe();
				}
				else {
					disableSwipe();
				}
			}
			else {
				disableSwipe();
			}
		}
	}
	
	private void SwipeWebView(final boolean direction) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				JSONObject obj = new JSONObject();
				try {
					obj.put(kJSType,JSTypeEvent);
					obj.put(kJSEvent, direction ? JSNameSwipeRight : JSNameSwipeLeft);
					executeScriptInWebView(obj);
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * ISwipeListener
	 */
	@Override
	public void onSwipeGesture(int direction) {
		if (direction == GESTURE_SWIPE_LEFT) {
			if (mDebug) Log.i(getClass().getSimpleName(), "next");
		}
		else if (direction == GESTURE_SWIPE_RIGHT){
			if (mDebug) Log.i(getClass().getSimpleName(), "previous");
		}
	}


	@Override
	protected void onUnhandledMessage(JSONObject message) {
		
	}
}
