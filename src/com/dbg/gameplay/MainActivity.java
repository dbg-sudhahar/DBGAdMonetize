package com.dbg.gameplay;

import java.util.List;

import com.dbg.constants.IAppConstants;
import com.dbg.constants.ICommonConstants;
import com.dbg.manager.MenuManager;
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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String SCORE = "score";
    private static final String HIGH_SCORE = "high score temp";
    private static final String UNDO_SCORE = "undo score";
    private static final String CAN_UNDO = "can undo";
    private static final String UNDO_GRID = "undo";
    private static final String GAME_STATE = "game state";
    private static final String UNDO_GAME_STATE = "undo game state";
    private MainView view;
    
    
    
    
    
    
    RevMobBanner banner;
	RevMob revmob;
    
	
	int adTypeValue = -1;

	Thread myThread = null;
	Runnable runnable = null;
	
	
	int customAdTimeInterval=0;

	private LinearLayout linContainer;
	
	private InterstitialAd interstitialAd;
	
	
	VideoView videoHolder;
	  RelativeLayout rl;
	  
	  RelativeLayout.LayoutParams lp1;
	  
	  
	  MenuManager menuManager;
	  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this,new PlayListener() {
			
			@Override
			public void undoClickListener() {
				//Toast.makeText(MainActivity.this, "Undo", Toast.LENGTH_SHORT).show();
				
				
				 adTypeValue = -1;
				 parseLogin(true);
				
			}
			
			@Override
			public void resetGameListener() {
				// TODO Auto-generated method stub

//				Toast.makeText(MainActivity.this, "Reset", Toast.LENGTH_SHORT).show();
				 adTypeValue = -1;
				 parseLogin(true);
				 
				
			}
			
			@Override
			public void gameOverListener() {

//				Toast.makeText(MainActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
				
			}
		});
        
        menuManager=new MenuManager(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        
        
       // videoView=(VideoView)findViewById(R.id.videoView);
        
         rl = new RelativeLayout(this);
        
        rl.addView(view);
        
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linContainer=new LinearLayout(this);
        
        
        rl.addView(linContainer,lp);
        
        
        
        videoHolder= new VideoView(this);
        
        videoHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        
        lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp1.addRule(RelativeLayout.CENTER_VERTICAL);
        
     //   rl.addView(videoHolder);
        
        setContentView(rl);
        
        revmob = RevMob.start(MainActivity.this);
//        revmob = RevMob.startWithListener(MainActivity.this, revmobListener);
        
       
        adTypeValue = -1;
		 parseLogin(false);
        
      //  loadVideoAd(2);
        
    }

    
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		

		return menuManager.onPrepareOptionsMenuConfig(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	
		return menuManager.onOptionsItemSelected(item);
	}
    
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            view.game.move(2);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            view.game.move(0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            view.game.move(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            view.game.move(1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasState", true);
        save();
    }

    protected void onPause() {
        super.onPause();

		//stopThreadListner();
        save();
    }
    
    protected void onResume() {
        super.onResume();
       // startThreadListner();
        load();
    }

    private void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        Tile[][] field = view.game.grid.field;
        Tile[][] undoField = view.game.grid.undoField;
        editor.putInt(WIDTH, field.length);
        editor.putInt(HEIGHT, field.length);
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    editor.putInt(xx + " " + yy, field[xx][yy].getValue());
                } else {
                    editor.putInt(xx + " " + yy, 0);
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt(UNDO_GRID + xx + " " + yy, undoField[xx][yy].getValue());
                } else {
                    editor.putInt(UNDO_GRID + xx + " " + yy, 0);
                }
            }
        }
        editor.putLong(SCORE, view.game.score);
        editor.putLong(HIGH_SCORE, view.game.highScore);
        editor.putLong(UNDO_SCORE, view.game.lastScore);
        editor.putBoolean(CAN_UNDO, view.game.canUndo);
        editor.putInt(GAME_STATE, view.game.gameState);
        editor.putInt(UNDO_GAME_STATE, view.game.lastGameState);
        editor.commit();
    }

   

    private void load() {
        //Stopping all animations
        view.game.aGrid.cancelAnimations();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        for (int xx = 0; xx < view.game.grid.field.length; xx++) {
            for (int yy = 0; yy < view.game.grid.field[0].length; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                    view.game.grid.field[xx][yy] = new Tile(xx, yy, value);
                } else if (value == 0) {
                    view.game.grid.field[xx][yy] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + xx + " " + yy, -1);
                if (undoValue > 0) {
                    view.game.grid.undoField[xx][yy] = new Tile(xx, yy, undoValue);
                } else if (value == 0) {
                    view.game.grid.undoField[xx][yy] = null;
                }
            }
        }

        view.game.score = settings.getLong(SCORE, view.game.score);
        view.game.highScore = settings.getLong(HIGH_SCORE, view.game.highScore);
        view.game.lastScore = settings.getLong(UNDO_SCORE, view.game.lastScore);
        view.game.canUndo = settings.getBoolean(CAN_UNDO, view.game.canUndo);
        view.game.gameState = settings.getInt(GAME_STATE, view.game.gameState);
        view.game.lastGameState = settings.getInt(UNDO_GAME_STATE, view.game.lastGameState);
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
					parseLogin(false);
					Thread.sleep(5000);
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
	
private void parseLogin(final boolean isVideoLoad) {

	

	try{
	
		ParseUser.logInInBackground("dbg", "dbg", new LogInCallback() {

			@Override
			public void done(ParseUser parseUser, ParseException arg1) {
				
				if((parseUser!=null)&&(arg1==null)){

				int adType = parseUser.getInt(ICommonConstants.ParseAdType);

				loadAd(adType);
				
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
		 private RevMobFullscreen video;
		    private boolean videoIsLoaded = false;
		    
		    void runOnAnotherThread(Runnable action) {
				new Thread(action).start();
			}
		private void loadVideoAd(int adType){
			
				switch (adType) {
				case 0:
					loadAdMobVideo();
					break;
				case 1:
					
					
					 video = revmob.createVideo(MainActivity.this, revmobListener);; 
					
					
					
					break;
				case 2:
					
					updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseVideoDisplayCount);
					if(videoHolder!=null){
					 rl.addView(videoHolder,lp1);
					videoHolder.setVisibility(View.VISIBLE);
					}
					MediaController controller=new MediaController(MainActivity.this);
					//if you want the controls to appear
					//videoHolder.setMediaController(controller);
					Uri video = Uri.parse("android.resource://" + getPackageName() + "/" 
					+ R.raw.sample); //do not add any extension
					//if your file is named sherif.mp4 and placed in /raw
					//use R.raw.sherif
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
	
	private void loadCustomAd() {
			
		linContainer.removeAllViews();
		
		
		ImageView customAd=new ImageView(this);
		customAd.setBackgroundResource(R.drawable.ad);
		customAd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				updateParseClickCount(ICommonConstants.DBGAd);
				
				updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseClickCount);
				
			}
		});
		

		linContainer.addView(customAd);
		
//		updateParseDisplayCount(ICommonConstants.DBGAd);
		
		updateParseCount(ICommonConstants.DBGAd, ICommonConstants.ParseDisplayCount);
		
		}


	
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
		public void onRevMobSessionIsStarted() {
			 video = revmob.createVideo(MainActivity.this, revmobListener);; 
		       
		}
		@Override
		public void onRevMobAdClicked() {
		
			updateParseCount(ICommonConstants.RevMob, ICommonConstants.ParseClickCount);
			
			
//			updateParseClickCount(ICommonConstants.RevMob);
		}
		@Override
		public void onRevMobAdDisplayed() {
			// TODO Auto-generated method stub
			super.onRevMobAdDisplayed();
//			updateParseDisplayCount(ICommonConstants.RevMob);
			
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
	public void loadAdMob() {

		

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

				
//				updateParseClickCount(ICommonConstants.AdMob);
				
				updateParseCount(ICommonConstants.AdMob, ICommonConstants.ParseClickCount);
			}

			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				super.onAdLoaded();

				

//				updateParseDisplayCount(ICommonConstants.AdMob);
				
				updateParseCount(ICommonConstants.AdMob, ICommonConstants.ParseDisplayCount);
			}

		});

		mAdView.loadAd(adRequest);

		linContainer.removeAllViews();

		linContainer.addView(mAdView);

		
//		this.addContentView(layout, lllp);
	}
	
	

	
	public void loadAdMobVideo() {
	interstitialAd=new InterstitialAd(MainActivity.this);
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

	

	
	public void updateParseCount(int type,final String col) {
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

}
