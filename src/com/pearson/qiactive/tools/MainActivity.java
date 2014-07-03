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
import android.widget.TextView;



/**
 * VirtualBanana core client code
 */
public class MainActivity extends Activity {

	private EditText useridText;
	private TextView statusText;
	private String userid;
	private Button getButton;
	private Button releaseButton;
	private final static String APPNAME = "AndyBanana";
	private Bananimator bananimator;
	private AlertDialog userDialog;
	private AlertDialog errorDialog;
	private BananaClient client;


	/**
	 * Client states
	 */

	/**
	 * Client is unable to grab the banana (forgot to set name, etc)
	 */
	private final static int CANT_GET_BANANA = 0;

	/**
	 * Client can grab the banana but currently isn't holding it
	 */
	private final static int HAVE_NO_BANANA = 1;

	/**
	 * Client currently has the banana
	 */
	private final static int HAVE_BANANA = 2;

	/**
	 * Client still has the banana after a long time (20mins?).  Superstate of HAVE_BANANA
	 */
	private final static int BOGARTING_BANANA = 3;

	private int state;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		statusText = (TextView) findViewById(R.id.status_text);
		bananimator = new Bananimator();
		String url = getString(R.string.server_url);
		client = new BananaClient(this, url);
		client.addListener(new Listener());
		client.start();
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
		createDialogs();
		userDialog.show();
	}

	/**
	 * Generate the dialogs once, else you can have multi-parent problems
	 */
	private void createDialogs() {
		AlertDialog.Builder bldr1 = new AlertDialog.Builder(this);
		bldr1.setTitle("Enter user id");
		bldr1.setMessage("Who wants to take the banana?");
		bldr1.setView(useridText);
		bldr1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String id = useridText.getText().toString();
				setUserId(id);
			}
		});
		bldr1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		userDialog = bldr1.create();

		AlertDialog.Builder bldr2 = new AlertDialog.Builder(this);
		bldr2.setTitle("Bad banana");
		bldr2.setMessage("");
		errorDialog = bldr2.create();
	}

	public void status(String msg) {
		statusText.setText(msg);
	}

	/**
	 * Set the user id to the given string.  If ok, allow banana-grabbing
	 */
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
			case CANT_GET_BANANA:
				getButton.setEnabled(false);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case HAVE_NO_BANANA:
				getButton.setEnabled(true);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case HAVE_BANANA:
				getButton.setEnabled(false);
				releaseButton.setEnabled(true);
				bananimator.start();
				break;
			case BOGARTING_BANANA:
				getButton.setEnabled(false);
				releaseButton.setEnabled(true);
				break;
			default:
		}
		state = stateId;
	}

	/**
	 * Generate our menu
	 * @param menu
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Callback for selecting a menu item.
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menuitem_userid:
				if (state == CANT_GET_BANANA || state == HAVE_NO_BANANA) {
					userDialog.show();
				} else {
					showError("I won't let you change your name whilst holding the banana!");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	private void showError(String message) {
		errorDialog.setMessage(message);
		errorDialog.show();
	}

	class Listener implements BananaListener {

		@Override
		public void processBanana(int type, String msg) {

		}
	}

	/**
	 * Server commands!
	 */

	private void commandGetBanana() {
		setState(HAVE_BANANA);
	}

	private void commandReleaseBanana() {
		setState(HAVE_NO_BANANA);

	}

	private void commandContinueBogarting() {
		setState(BOGARTING_BANANA);

	}


	/**
	 *
	 */

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


