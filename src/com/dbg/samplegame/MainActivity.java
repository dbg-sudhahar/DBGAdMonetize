
package com.dbg.samplegame;



import java.util.List;

import com.dbg.constants.IAppConstants;
import com.dbg.constants.ICommonConstants;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
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
    
    private Handler adHandler  = new Handler();
    public Runnable adUpdater = new Runnable() {
        @Override
        public void run() {

            updateAd();

            adHandler.postDelayed(this, 1000);

        }
    };
    @SuppressLint({ "SetJavaScriptEnabled", "NewApi", "ShowToast" })
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
        } catch (SettingNotFoundException e) { }

        // If rotation isn't locked and it's a LARGE screen then add orientation changes based on sensor
        int screenLayout = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (((screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE)
                || (screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE))
                    && isOrientationEnabled) {
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

        Toast.makeText(getApplication(), R.string.toggle_fullscreen, Toast.LENGTH_SHORT).show();
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

        pressBackToast = Toast.makeText(getApplicationContext(), R.string.press_back_again_to_exit,
                Toast.LENGTH_SHORT);

        
        adHandler.postDelayed(adUpdater,1000);
        
        
       
    }

    private void parseLogin() {
		ParseUser.logInInBackground("dbg", "dbg", new LogInCallback() {
			
			@Override
			public void done(ParseUser parseUser, ParseException arg1) {
				
				int adType=parseUser.getInt(ICommonConstants.ParseAdType);
				
				Toast.makeText(MainActivity.this, "Login Sucess : Ad Type=" +adType, Toast.LENGTH_SHORT).show();
				
				switch (adType) {
				case 0:
					loadAdMob();
					break;
				case 1:
					flurryAd();
					break;
				case 2:
	
					break;

				default:
					break;
				}
			}
		});
		
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
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IS_FULLSCREEN_PREF,
                DEF_FULLSCREEN);
    }

    /**
     * Toggles the activitys fullscreen mode by setting the corresponding window flag
     * @param isFullScreen
     */
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

    public void updateAd()
    {
        // code to update add



    }
    public void loadAdMob(){

       // AdView mAdView = (AdView) findViewById(R.id.adView);
        
       AdView mAdView=new AdView(this);
       mAdView.setAdUnitId(IAppConstants.ADMOB_ID);
       mAdView.setAdSize(AdSize.BANNER);
       
        AdRequest adRequest = new AdRequest.Builder().build();
        
        
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                super.onAdOpened();

              //If ad click
                
                Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();
                updateParseClickCount(0);
            }
            
            @Override
            public void onAdLoaded() {
            	// TODO Auto-generated method stub
            	super.onAdLoaded();
            	
            	Toast.makeText(MainActivity.this, "Load", Toast.LENGTH_SHORT).show();
            	
            	updateParseDisplayCount(0);
            }
            
            
        });
       
        
        mAdView.loadAd(adRequest);
      	
		LinearLayout	layout = new LinearLayout(this);
        layout.setGravity(Gravity.BOTTOM);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(mAdView);

        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(layout, lllp);
    }
    
    private FlurryAdBanner adBanner;
    public void flurryAd(){
    	
    	
    	adBanner=new FlurryAdBanner(this, mWebView, ICommonConstants.FlurryAdSpaceName);
    	adBanner.fetchAndDisplayAd();
//    	 mFlurryAdInterstitial = new FlurryAdInterstitial(this, ICommonConstants.FlurryAdSpaceName);
//
//         // allow us to get callbacks for ad events
//         mFlurryAdInterstitial.setListener(interstitialAdListener);
//    	  mFlurryAdInterstitial.fetchAd();
    }
    
    FlurryAdInterstitialListener interstitialAdListener = new FlurryAdInterstitialListener() {

        @Override
        public void onFetched(FlurryAdInterstitial adInterstitial) {
            adInterstitial.displayAd();
            
        	Toast.makeText(MainActivity.this, "Display", Toast.LENGTH_SHORT).show();
			
        }

        @Override
        public void onError(FlurryAdInterstitial adInterstitial, FlurryAdErrorType adErrorType, int errorCode) {
            adInterstitial.destroy();
        }
        //..
        //the remainder of listener callbacks 

		@Override
		public void onAppExit(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClicked(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClose(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDisplay(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRendered(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onVideoCompleted(FlurryAdInterstitial arg0) {
			// TODO Auto-generated method stub
			
		}
    };
    
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, IAppConstants.FLURRY_KEY);
        // fetch and prepare ad for this ad space. won’t render one yet
      
        
        parseLogin();
    }

    public void onStop() {
        FlurryAgent.onEndSession(this);
        //do NOT call mFlurryAdInterstitial.destroy() here.  
        //it will destroy the object prematurely and prevent certain listener callbacks form fireing
        super.onStop();
    }

    public void onDestroy() {
       // mFlurryAdInterstitial.destroy();
    }
    
    
    public void updateParseDisplayCount(int type){
    	ParseQuery<ParseObject> advertisments=ParseQuery.getQuery(ICommonConstants.ParseAdvertismentTable);
    	advertisments.whereEqualTo(ICommonConstants.ParseAdType, type);
    	advertisments.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				
				if(arg0.size()>0){
					ParseObject parseObject=arg0.get(0);
					
					int displayCount=parseObject.getInt(ICommonConstants.ParseDisplayCount);
					
					parseObject.put(ICommonConstants.ParseDisplayCount,(displayCount+1));
					parseObject.saveInBackground();
				}
				
			}
		});
    }
    
    
    public void updateParseClickCount(int type){
    	ParseQuery<ParseObject> advertisments=ParseQuery.getQuery(ICommonConstants.ParseAdvertismentTable);
    	advertisments.whereEqualTo(ICommonConstants.ParseAdType, type);
    	advertisments.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				
				if(arg0.size()>0){
					ParseObject parseObject=arg0.get(0);
					
					int clickCount=parseObject.getInt(ICommonConstants.ParseClickCount);
					
					parseObject.put(ICommonConstants.ParseClickCount,(clickCount+1));
					parseObject.saveInBackground();
				}
				
			}
		});
    }
}
