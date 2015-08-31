
package com.dbg.samplegame;

import java.util.List;

import com.dbg.constants.IAppConstants;
import com.dbg.constants.ICommonConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	private WebView mWebView;
	private long mLastBackPress;
	private static final long mBackPressThreshold = 3500;
	private static final String IS_FULLSCREEN_PREF = "is_fullscreen_pref";
	private static boolean DEF_FULLSCREEN = true;
	private long mLastTouch;
	private static final long mTouchThreshold = 2000;
	private Toast pressBackToast;
	private LinearLayout linContainer;

	int adTypeValue = -1;

	Thread myThread = null;
	Runnable runnable = null;
	
	
	int customAdTimeInterval=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Don't show an action bar or title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// If on android 3.0+ activate hardware acceleration
		if (Build.VERSION.SDK_INT >= 11) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		// Apply previous setting about showing status bar or not
		applyFullScreen(isFullScreen());

		// Check if screen rotation is locked in settings
		boolean isOrientationEnabled = false;
		try {
			isOrientationEnabled = Settings.System.getInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION) == 1;
		} catch (SettingNotFoundException e) {
		}

		// If rotation isn't locked and it's a LARGE screen then add orientation
		// changes based on sensor
		int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		if (((screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE)
				|| (screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE)) && isOrientationEnabled) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		setContentView(R.layout.activity_main);

		// Load webview with game
		mWebView = (WebView) findViewById(R.id.mainWebView);
		WebSettings settings = mWebView.getSettings();
		String packageName = getPackageName();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setDatabasePath("/data/data/" + packageName + "/databases");

		// If there is a previous instance restore it in the webview
		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		} else {
			mWebView.loadUrl("file:///android_asset/2048/index.html");
		}

		
		// Set fullscreen toggle on webview LongClick
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Implement a long touch action by comparing
				// time between action up and action down
				long currentTime = System.currentTimeMillis();
				if ((event.getAction() == MotionEvent.ACTION_UP)
						&& (Math.abs(currentTime - mLastTouch) > mTouchThreshold)) {
					boolean toggledFullScreen = !isFullScreen();
					saveFullScreen(toggledFullScreen);
					applyFullScreen(toggledFullScreen);
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mLastTouch = currentTime;
				}
				// return so that the event isn't consumed but used
				// by the webview as well
				return false;
			}
		});

		pressBackToast = Toast.makeText(getApplicationContext(), R.string.press_back_again_to_exit, Toast.LENGTH_SHORT);

		linContainer = (LinearLayout) findViewById(R.id.linContainer);
		
		revmob = RevMob.start(this);
//		layout = new LinearLayout(this);
//		layout.setGravity(Gravity.BOTTOM);
//		layout.setOrientation(LinearLayout.VERTICAL);
//		
//		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		startThreadListner();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		stopThreadListner();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mWebView.saveState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void saveFullScreen(boolean isFullScreen) {
		// save in preferences
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putBoolean(IS_FULLSCREEN_PREF, isFullScreen);
		editor.commit();
	}

	private boolean isFullScreen() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IS_FULLSCREEN_PREF, DEF_FULLSCREEN);
	}

	private void applyFullScreen(boolean isFullScreen) {
		if (isFullScreen) {
			getWindow().clearFlags(LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (Math.abs(currentTime - mLastBackPress) > mBackPressThreshold) {
			pressBackToast.show();
			mLastBackPress = currentTime;
		} else {
			pressBackToast.cancel();
			super.onBackPressed();
		}
	}

	private void parseLogin() {

	
		

		ParseUser.logInInBackground("dbg", "dbg", new LogInCallback() {

			@Override
			public void done(ParseUser parseUser, ParseException arg1) {
				
				if((parseUser!=null)&&(arg1==null)){

				int adType = parseUser.getInt(ICommonConstants.ParseAdType);

				loadAd(adType);

				}
				else{
					
				}
				
			}

			
		});

		

	}
		
		private void loadAd(int adType){
			if (adTypeValue != adType) {
				adTypeValue = adType;
				switch (adType) {
				case 0:
					loadAdMob();
					break;
				case 1:
					loadRevMob();
					break;
				case 2:
					loadCustomAd();
					break;

				default:
					break;
				}

			
			}
		}
	
	private void loadCustomAd() {
			
		linContainer.removeAllViews();
		
		
		ImageView customAd=new ImageView(this);
		customAd.setBackgroundResource(R.drawable.ad);
		customAd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateParseClickCount(ICommonConstants.DBGAd);
				
			}
		});
		

		linContainer.addView(customAd);
		
		updateParseDisplayCount(ICommonConstants.DBGAd);
		
		}

	RevMobBanner banner;
	RevMob revmob;
	
	private void loadRevMob() {
	
		
		
		banner = revmob.createBanner(this, revmobListener);
		
		
		runOnUiThread(new Runnable() {
            @Override
            public void run() {

        		linContainer.removeAllViews();
            	linContainer.addView(banner);
            	
            
            }
        });
		
		
//		
		
		
	}
	RevMobAdsListener revmobListener = new RevMobAdsListener(){
		@Override
		public void onRevMobAdClicked() {
			// TODO Auto-generated method stub
			super.onRevMobAdClicked();
			
			
			updateParseClickCount(ICommonConstants.RevMob);
		}
		@Override
		public void onRevMobAdDisplayed() {
			// TODO Auto-generated method stub
			super.onRevMobAdDisplayed();
			updateParseDisplayCount(ICommonConstants.RevMob);
			
		}
		
		@Override
		public void onRevMobAdDismissed() {
			// TODO Auto-generated method stub
			super.onRevMobAdDismissed();
			
			Toast.makeText(MainActivity.this, "DISS", Toast.LENGTH_SHORT).show();
		}
		
	};
	public void loadAdMob() {

		
//		layout = new LinearLayout(this);
//		layout.setGravity(Gravity.BOTTOM);
//		layout.setOrientation(LinearLayout.VERTICAL);
//		
//		lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
		
		// AdView mAdView = (AdView) findViewById(R.id.adView);
		System.out.println("Admob ");
		AdView mAdView = new AdView(this);
		mAdView.setAdUnitId(IAppConstants.ADMOB_ID);
		mAdView.setAdSize(AdSize.BANNER);

		AdRequest adRequest = new AdRequest.Builder().build();

		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdOpened() {
				super.onAdOpened();

				// If ad click

				
				updateParseClickCount(ICommonConstants.AdMob);
			}

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();

				

				updateParseDisplayCount(ICommonConstants.AdMob);
			}

		});

		mAdView.loadAd(adRequest);

		linContainer.removeAllViews();

		linContainer.addView(mAdView);

		
//		this.addContentView(layout, lllp);
	}

	

	public void updateParseDisplayCount(int type) {
		ParseQuery<ParseObject> advertisments = ParseQuery.getQuery(ICommonConstants.ParseAdvertismentTable);
		advertisments.whereEqualTo(ICommonConstants.ParseAdType, type);
		advertisments.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				
				

				if ((arg0!=null)&&(arg1==null)) {
					ParseObject parseObject = arg0.get(0);

					int displayCount = parseObject.getInt(ICommonConstants.ParseDisplayCount);

					parseObject.put(ICommonConstants.ParseDisplayCount, (displayCount + 1));
					parseObject.saveInBackground();
				}
				else{
					System.out.println("Err"+ arg1.getMessage().toString());
				}
				

			}
		});
	}

	public void updateParseClickCount(int type) {
		ParseQuery<ParseObject> advertisments = ParseQuery.getQuery(ICommonConstants.ParseAdvertismentTable);
		advertisments.whereEqualTo(ICommonConstants.ParseAdType, type);
		advertisments.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				try{
				if ( (arg0!=null)&&(arg1==null)) {
					ParseObject parseObject = arg0.get(0);

					int clickCount = parseObject.getInt(ICommonConstants.ParseClickCount);

					parseObject.put(ICommonConstants.ParseClickCount, (clickCount + 1));
					parseObject.saveInBackground();
				}
				else{
					System.out.println("Err"+ arg1.getMessage().toString());
				}
				
				}catch(Exception exception){
					System.out.println("Err"+ exception.getMessage().toString());
				}

			}
		});
	}

	public void startThreadListner() {
		myThread = null;
		runnable = null;

		runnable = new CountDownRunner();
		myThread = new Thread(runnable);
		myThread.start();
	}

	public void stopThreadListner() {
		myThread.interrupt();

	}

	class CountDownRunner implements Runnable {

		public CountDownRunner() {

		}

		// @Override
		public void run() {
			while (true) {
				try {
					parseLogin();
					Thread.sleep(10000);
					if(adTypeValue==2){
						customAdTimeInterval++;
						
					}
					
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
				}

			}
		}

	}
}
