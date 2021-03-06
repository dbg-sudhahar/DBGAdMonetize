package com.dbg.gameplay;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.dbg.constants.ICommonConstants;
import com.dbg.manager.AdManager;
import com.dbg.manager.AdManager.ParseListener;
import com.dbg.manager.MenuManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class PlayFragmant extends Fragment {
	
	

	

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

	Thread myThread = null;
	Runnable runnable = null;

	int customAdTimeInterval = 0;

	private LinearLayout linContainer;

	VideoView videoHolder;
	RelativeLayout rl;

	RelativeLayout.LayoutParams lp1;

	MenuManager menuManager;
	AdManager adManager;

	Activity activity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		
		return getPlayView();
	}
	
	
	
	

	private View getPlayView() {
		// TODO Auto-generated method stub
		
		view = new MainView(activity, new PlayListener() {

			@Override
			public void undoClickListener() {
				// Toast.makeText(MainActivity.this, "Undo",
				// Toast.LENGTH_SHORT).show();

				adManager.adTypeValue = -1;
				adManager.parseLogin(false);
				
				adManager.getParseData(new ParseListener() {
					
					public void getAmountListener(float amount) {
//						view.game.estimateAmount=amount;
						
						setData(amount);
						
					}
				});
				
			}

			@Override
			public void resetGameListener() {
				// TODO Auto-generated method stub

				// Toast.makeText(MainActivity.this, "Reset",
				// Toast.LENGTH_SHORT).show();
				adManager.adTypeValue = -1;
				adManager.parseLogin(true);
				adManager.getParseData(new ParseListener() {	
					
					public void getAmountListener(float amount) {
//						view.game.estimateAmount=amount;
						
						setData(amount);
						
					}
				});

			}

			@Override
			public void gameOverListener() {

				// Toast.makeText(MainActivity.this, "Game Over",
				// Toast.LENGTH_SHORT).show();

			}
		});
		
		


		menuManager = new MenuManager(activity);

		

		// videoView=(VideoView)findViewById(R.id.videoView);

		rl = new RelativeLayout(activity);

		rl.addView(view);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.weight = 1.0f;
		params.gravity = Gravity.TOP;
		
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		linContainer = new LinearLayout(activity);

		rl.addView(linContainer, lp);

		adManager = new AdManager(activity, linContainer, rl);

		AdManager.getParseData(new ParseListener() {
			
			public void getAmountListener(float amount) {
				//view.game.estimateAmount=amount;
				//view.game.refresh();
				setData(amount);
			}
		});
	
		
		return rl;

	

	}
	
	public void setData(float amount){
		
		NumberFormat formatter = new DecimalFormat("###,###,###.##");
//	       System.out.println("The Decimal Value is:"+formatter.format(amount)
		
		
		String text="The Estimated Amount You Have Donated So Far: $"+formatter.format(amount);
		
		LinearLayout.LayoutParams lastTxtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lastTxtParams.setMargins(35, 0, 35, 35);
		lastTxtParams.gravity = Gravity.TOP;
		
		TextView textViewAmount=new TextView(activity);
		textViewAmount.setTextColor(Color.BLACK);

		textViewAmount.setTextAppearance(activity, android.R.attr.textAppearanceLarge);
		textViewAmount.setTypeface(null, Typeface.BOLD);
		textViewAmount.setTextSize(view.titleTextSize/2);
		textViewAmount.setSingleLine();
		textViewAmount.setEllipsize(TruncateAt.MARQUEE);
		textViewAmount.setMarqueeRepeatLimit(-1);
		textViewAmount.setHorizontallyScrolling(true);
		textViewAmount.setFocusableInTouchMode(true);
		textViewAmount.setText(text);
		textViewAmount.setLayoutParams(lastTxtParams);
		textViewAmount.setSelected(true);
		linContainer.removeAllViews();
		linContainer.addView(textViewAmount);
		
	}
	
	

	

	public boolean onCreateOptionsMenu(Menu menu) {

		return menuManager.onPrepareOptionsMenuConfig(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return menuManager.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// Do nothing
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
		// return super.onKeyDown(keyCode, event);
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("hasState", true);
		save();
	}

	public void onPause() {
		super.onPause();

		// stopThreadListner();
		save();
	}

	public void onResume() {
		super.onResume();
		// startThreadListner();
		load();
	}

	private void save() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
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
		// Stopping all animations
		view.game.aGrid.cancelAnimations();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
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

}
