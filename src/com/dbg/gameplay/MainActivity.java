package com.dbg.gameplay;

import java.util.List;

import com.dbg.constants.IAppConstants;
import com.dbg.constants.ICommonConstants;
import com.dbg.samplegame.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this,new PlayListener() {
			
			@Override
			public void undoClickListener() {
				//Toast.makeText(MainActivity.this, "Undo", Toast.LENGTH_SHORT).show();
				
				
				 adTypeValue = -1;
				 parseLogin();
				
			}
			
			@Override
			public void resetGameListener() {
				// TODO Auto-generated method stub

//				Toast.makeText(MainActivity.this, "Reset", Toast.LENGTH_SHORT).show();
				 adTypeValue = -1;
				 parseLogin();
				
			}
			
			@Override
			public void gameOverListener() {

//				Toast.makeText(MainActivity.this, "Game Over", Toast.LENGTH_SHORT).show();
				
			}
		});

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        
        
        RelativeLayout rl = new RelativeLayout(this);
        
        rl.addView(view);
        
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linContainer=new LinearLayout(this);
        
        
        rl.addView(linContainer,lp);
        setContentView(rl);
        
        revmob = RevMob.start(this);
        
        
        adTypeValue = -1;
		 parseLogin();
        
        
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
					parseLogin();
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
	
private void parseLogin() {

	

	try{
	
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
				
				}catch(Exception exception){
					System.out.println("Err"+ exception.getMessage().toString());
				}

			}
		});
	}
}
