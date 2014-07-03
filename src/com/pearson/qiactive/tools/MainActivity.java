package com.pearson.qiactive.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private EditText useridText;
	private String userid;
	private Button getButton;
	private Button releaseButton;
	private final static String APPNAME = "AndyBanana";
	Bananimator bananimator;

	private final static int CANT_GET_BANANA = 0;
	private final static int HAVE_NO_BANANA = 1;
	private final static int HAVE_BANANA = 2;
	private final static int BOGARTING_BANANA = 3;

	private int state;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bananimator = new Bananimator();
		getButton = (Button) findViewById(R.id.banana_get);
		getButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				commandGetBanana();
			}
		});
		releaseButton = (Button) findViewById(R.id.banana_release);
		releaseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				commandReleaseBanana();
			}
		});
		useridText = new EditText(this);
		useridText.setHint("set your userid here");
		setUserId("");
		showUseridDialog();
	}

	private void setUserId(String id) {
		id = id.trim();
		if (id.length() > 0) {
			setState(HAVE_NO_BANANA);
			userid = id;
			setTitle(APPNAME + " - " + id);
		} else {
			setState(CANT_GET_BANANA);
		}
	}

	private void setState(int stateId) {
		switch (stateId) {
			case CANT_GET_BANANA :
				getButton.setEnabled(true);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case HAVE_NO_BANANA :
				getButton.setEnabled(true);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case HAVE_BANANA :
				getButton.setEnabled(false);
				releaseButton.setEnabled(true);
				bananimator.start();
				break;
			case BOGARTING_BANANA :
				getButton.setEnabled(false);
				releaseButton.setEnabled(true);
				break;
			default :
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menuitem_userid:
				if (state == CANT_GET_BANANA || state == HAVE_NO_BANANA) {
					showUseridDialog();
				} else {
					showErrorDialog("I won't let you change your name whilst holding the banana!");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	void showUseridDialog() {
		useridText.setText("");
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Enter user id");
		dlg.setMessage("Who wants to take the banana?");
		dlg.setView(useridText);
		dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String id = useridText.getText().toString();
				setUserId(id);
			}
		});
		dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		dlg.show();
	}


	void showErrorDialog(String message) {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle("Bad banana");
		dlg.setMessage(message);
		dlg.show();
	}



	private void commandGetBanana() {
		setState(HAVE_BANANA);
	}

	private void commandReleaseBanana() {
		setState(HAVE_NO_BANANA);

	}


class Bananimator {


	private Handler handler;
	private final int MILLIS = 100;
	private int imageIndex;
    private boolean continueAnimating;
	private final int imgs[] = {
			R.drawable.ab0,
			R.drawable.ab1,
			R.drawable.ab2,
			R.drawable.ab3,
			R.drawable.ab4,
			R.drawable.ab5,
			R.drawable.ab6,
			R.drawable.ab7
	};
	ImageView imageView;

	public Bananimator() {
		handler = new Handler();
		imageView = (ImageView) findViewById(R.id.banana_image);
		continueAnimating = false;

	}

	public void start() {

		if (continueAnimating) {
			return;
		}

		continueAnimating = true;
		imageIndex = 0;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				imageView.setImageResource(imgs[imageIndex]);
				if (++imageIndex >= 8) {
					imageIndex = 0;
				}
				if (continueAnimating) {
					handler.postDelayed(this, MILLIS);
				}
			}
		}, MILLIS);
	}


	public void stop() {
		continueAnimating = false;
	}


}//Bananimator


}//MainActivity


