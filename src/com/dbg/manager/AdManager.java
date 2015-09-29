package com.dbg.manager;

import java.util.List;

import com.dbg.constants.IAppConstants;
import com.dbg.constants.ICommonConstants;
import com.dbg.samplegame.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;
import com.revmob.ads.interstitial.RevMobFullscreen;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
public class AdManager {

	public static String PARSE_APP_ID="Li0RlkopvS2f58KUqUcyfFtqro0sRpS0GpOF3CP7";
	public static String PARSE_CLIENT_KEY="oFIGiYqXvQdU0jpASSnYZMzVdHWAuevOsaXvDflc";
	

	public static String ADMOB_ID="ca-app-pub-8572551537746831/8190297904";
	public static String ADMOB_VIDEO_ID="ca-app-pub-8572551537746831/1284846305";
	
	public static String REVMOB_VIDEO_KEY="55db700f0b2cb12b75d8112c";
	

	
	public static String ParseAdType="AdType";
	public static String ParseClickCount="ClickCount";
	public static String ParseDisplayCount="DisplayCount";
	public static String ParseVideoDisplayCount="VideoDisplayCount";

	public static String ParseUsername="Username";
	public static String Parsepassword="password";
	public static String ParseLoginTable="Login";
	public static String ParseAdvertismentTable="Advertisment";

	
	
	public static int AdMob_TYPE=0;

	public static int RevMob_TYPE=1;
	public static int DBGAd_TYPE=2;
	

	public static int BANNER=0;
	
	public static int VIDEO=1;
	
	
	Activity activity;
	
	

public	 int adTypeValue = -1;
RelativeLayout.LayoutParams lp1;
	private LinearLayout linContainer;
	 private RevMobFullscreen video;

	 VideoView videoHolder;
	    RevMobBanner banner;
		RevMob revmob;

		private InterstitialAd interstitialAd;
		RelativeLayout rl;
	
		
		
		
		
		public static  float total=0;
		public static float videoAdVal=0.50f;

		public static float adDisplayVal=0.10f;

		public static float adClickVal=0.25f;
		
		
		
	
	public AdManager(Activity activity,LinearLayout linContainer,RelativeLayout rl) {
		this.activity=activity;
		this.linContainer=linContainer; 
		
		 revmob = RevMob.start(activity);
		 
		 videoHolder= new VideoView(activity);
	        
		 this.rl=rl;
	        videoHolder.setLayoutParams(new LinearLayout.LayoutParams(
	                LinearLayout.LayoutParams.WRAP_CONTENT,
	                LinearLayout.LayoutParams.WRAP_CONTENT));
	        
	        lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
	        lp1.addRule(RelativeLayout.CENTER_VERTICAL);
	}
	
	public  void parseLogin(final boolean isVideoLoad) {

		

		try{
		
			ParseUser.logInInBackground("dbg", "dbg", new LogInCallback() {

				@Override
				public void done(ParseUser parseUser, ParseException arg1) {
					
					if((parseUser!=null)&&(arg1==null)){

					int adType = parseUser.getInt(ICommonConstants.ParseAdType);

					//loadAd(adType);
					
					if(isVideoLoad){
						loadVideoAd(adType);
					}

					}
					else{
						
					}
					
				}

				
			});
		}catch(Exception e){
			
		}
		

			

		}

public interface ParseListener {

	
	void getAmountListener(float amount);
	
}


public  static void  getParseData(final ParseListener parseListener){
		
		total=0;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Advertisment");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
			
				if(arg0!=null && arg0.size()>0){
				for (int i = 0; i < arg0.size(); i++) {
					total=total+(arg0.get(i).getInt(ICommonConstants.ParseDisplayCount) *adDisplayVal);
					System.out.println("VAL 1=="+total);
					total=total+(arg0.get(i).getInt(ICommonConstants.ParseClickCount) *adClickVal);
					System.out.println("VAL 2=="+total);
					total=total+(arg0.get(i).getInt(ICommonConstants.ParseVideoDisplayCount) *videoAdVal);
					System.out.println("VAL 3=="+total);
				}
				
				parseListener.getAmountListener(total);
				
			
				}
				
			
				
				
			}
		});
		
	}
	public  void updateParseCount(int type,final String col) {
		ParseQuery<ParseObject> advertisments = ParseQuery.getQuery(ICommonConstants.ParseAdvertismentTable);
		advertisments.whereEqualTo(ICommonConstants.ParseAdType, type);
		advertisments.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				
				

				if ((arg0!=null)&&(arg1==null)) {
					ParseObject parseObject = arg0.get(0);

					int displayCount = parseObject.getInt(col);

					parseObject.put(col, (displayCount + 1));
					parseObject.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				else{
					System.out.println("Err"+ arg1.getMessage().toString());
				}
				

			}
		});
	}
	
	
	public  void loadAd(int adType){
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
	
	
	
public void loadAdMob() {

		

		System.out.println("Admob ");
		AdView mAdView = new AdView(activity);
		mAdView.setAdUnitId(IAppConstants.ADMOB_ID);
		mAdView.setAdSize(AdSize.BANNER);
		

		AdRequest adRequest = new AdRequest.Builder().build();

		mAdView.setAdListener(new AdListener() {
			@Override
			public void onAdOpened() {
				super.onAdOpened();

		
				updateParseCount(ICommonConstants.AdMob, ICommonConstants.ParseClickCount);
			}

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();

				
		updateParseCount(ICommonConstants.AdMob, ICommonConstants.ParseDisplayCount);
			}

		});

		mAdView.loadAd(adRequest);

		linContainer.removeAllViews();

		linContainer.addView(mAdView);

		
	}
private void loadRevMob() {
	
	
	
	banner = revmob.createBanner(activity, revmobListener);
	
	
	activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {

    		linContainer.removeAllViews();
        	linContainer.addView(banner);
        	
        
        }
    });
	

	
	
}


RevMobAdsListener revmobListener = new RevMobAdsListener(){
	
	@Override
	public void onRevMobSessionIsStarted() {
		 video = revmob.createVideo(activity, revmobListener);; 
	       
	}
	@Override
	public void onRevMobAdClicked() {
	
		updateParseCount(ICommonConstants.RevMob, ICommonConstants.ParseClickCount);

	}
	@Override
	public void onRevMobAdDisplayed() {
		// TODO Auto-generated method stub
		super.onRevMobAdDisplayed();
		updateParseCount(ICommonConstants.RevMob, ICommonConstants.ParseDisplayCount);
		
	}
	
	@Override
	public void onRevMobAdDismissed() {
		// TODO Auto-generated method stub
		super.onRevMobAdDismissed();
		
		
	}
	
	@Override
	public void onRevMobRewardedVideoLoaded() {
		// TODO Auto-generated method stub
		super.onRevMobRewardedVideoLoaded();
		
		
	}
	
	
	
	public void onRevMobVideoLoaded(){
		
		video.showVideo();
		
		updateParseCount(ICommonConstants.RevMob, ICommonConstants.ParseVideoDisplayCount);
	}		
	
};

private void loadCustomAd() {
	
	linContainer.removeAllViews();
	
	
	ImageView customAd=new ImageView(activity);
	customAd.setBackgroundResource(R.drawable.ad);
	customAd.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
	updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseClickCount);
			
		}
	});
	

	linContainer.addView(customAd);
	

	updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseDisplayCount);
	
	}

private void loadVideoAd(int adType){
	
	switch (adType) {
	case 0:
		loadAdMobVideo();
		break;
	case 1:
	
		
		 video = revmob.createVideo(activity, revmobListener);; 
		
		
		
		break;
	case 2:
		
		updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseVideoDisplayCount);
		if(videoHolder!=null){
		 rl.addView(videoHolder,lp1);
		videoHolder.setVisibility(View.VISIBLE);
		}
		MediaController controller=new MediaController(activity);
	
		Uri video = Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.raw.sample); 
		videoHolder.setVideoURI(video);
		
		videoHolder.start();
		videoHolder.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				
				if(videoHolder!=null){
				videoHolder.setVisibility(View.INVISIBLE);
				 rl.removeView(videoHolder);
			}
			}
		});
		
		
		
		break;

	default:
		break;
	}



}

public void loadAdMobVideo() {
	interstitialAd=new InterstitialAd(activity);
	interstitialAd.setAdUnitId(IAppConstants.ADMOB_VIDEO_ID);
	interstitialAd.setAdListener(new AdListener() {
		@Override
		public void onAdLoaded() {
			// TODO Auto-generated method stub
			super.onAdLoaded();
			
			if(interstitialAd.isLoaded()){
				interstitialAd.show();
			}
			updateParseCount(ICommonConstants.AdMob, ICommonConstants.ParseVideoDisplayCount);
		}
		
		
		
	});
	AdRequest adRequest = new AdRequest.Builder().build();
	interstitialAd.loadAd(adRequest);
		

	}
	

}
