package com.pearson.qiactive.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
public class MainActivity extends Activity implements BananaListener {

	private TextView statusText;
	private String userid;
	private Button getButton;
	private Button releaseButton;
	private Button bogartButton;
	private Button stealButton;
	private final static String APPNAME = "AndyBanana";
	private Bananimator bananimator;
	private AlertDialog userDialog;
	private AlertDialog takeDialog;
	private AlertDialog stealDialog;
	private AlertDialog bogartDialog;
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
	private final static int NOBODY_HAS_BANANA = 1;

	/**
	 * I currently have the banana
	 */
	private final static int HAVE_BANANA = 2;

	/**
	 *
	 */
	private final static int SOMEONE_ELSE_BANANA = 3;

	/**
	 * Client still has the banana after a long time (20mins?).  Superstate of HAVE_BANANA
	 */
	private final static int BOGARTING_BANANA = 4;

	private int state;
	String url;
	String devurl;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		statusText = (TextView) findViewById(R.id.status_text);
		bananimator = new Bananimator();
		url = getResources().getString(R.string.server_url);
		devurl = getResources().getString(R.string.dev_server_url);
		createDialogs();
		getButton = (Button) findViewById(R.id.banana_get);
		getButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				takeDialog.show();
			}
		});
		releaseButton = (Button) findViewById(R.id.banana_release);
		releaseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				commandReleaseBanana();
			}
		});
		bogartButton = (Button) findViewById(R.id.banana_bogart);
		bogartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				bogartDialog.show();
			}
		});
		stealButton = (Button) findViewById(R.id.banana_steal);
		stealButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				stealDialog.show();
			}
		});
		setUserId("");
		client = new BananaClient(this, devurl, userid);
		client.addListener(this);
		userDialog.show();
	}


	/**
	 * Generate the dialogs once, else you can have multi-parent problems
	 */
	private void createDialogs() {
		AlertDialog.Builder bldr = new AlertDialog.Builder(this);
		bldr.setTitle("Enter user id");
		bldr.setMessage("Who wants to take the banana?");
		final EditText useridText = new EditText(this);
		useridText.setMaxLines(1);
		useridText.setLines(1);
		useridText.setSingleLine(true);
		useridText.setHint("set your userid here");
		bldr.setView(useridText);
		bldr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String id = useridText.getText().toString();
				setUserId(id);
			}
		});
		bldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		userDialog = bldr.create();

		bldr = new AlertDialog.Builder(this);
		bldr.setTitle("Bad banana");
		bldr.setMessage("");
		errorDialog = bldr.create();

		bldr = new AlertDialog.Builder(this);
		bldr.setTitle("Take!");
		bldr.setMessage("Do you want to take the banana?");
		bldr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				commandTakeBanana();
			}
		});
		bldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		takeDialog = bldr.create();

		bldr = new AlertDialog.Builder(this);
		bldr.setTitle("Steal!");
		bldr.setMessage("Do you want to steal the banana?");
		bldr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				commandStealBanana();
			}
		});
		bldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		stealDialog = bldr.create();

		bldr = new AlertDialog.Builder(this);
		bldr.setTitle("Bogart!");
		bldr.setMessage("Keep bogarting the banana?");
		AlertDialog.Builder ok = bldr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				commandContinueBogarting();
			}
		});
		bldr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		bogartDialog = bldr.create();

	}

	void error(String msg) {
		Log.e("banana", msg);
	}

	void trace(String msg) {
		Log.i("banana", msg);
	}


	public void status(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {
				statusText.setText(msg);
			}
		});
	}

	/**
	 * Set the user id to the given string.  If ok, allow banana-grabbing
	 */
	private void setUserId(String id) {
		id = id.trim();
		if (id.matches("^[A-Za-z][A-Za-z0-9]{2,10}$")) {
			setState(NOBODY_HAS_BANANA);
			userid = id;
			setTitle(APPNAME + " - " + id);
			client.stop();
			client = new BananaClient(this, devurl, userid);
			client.addListener(this);
			client.start();
		} else {
			setState(CANT_GET_BANANA);
		}
	}

	private void adjustButtons() {
		switch (state) {
			case CANT_GET_BANANA:
				getButton.setEnabled(false);
				bogartButton.setEnabled(false);
				stealButton.setEnabled(false);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case NOBODY_HAS_BANANA:
				getButton.setEnabled(true);
				bogartButton.setEnabled(false);
				stealButton.setEnabled(false);
				releaseButton.setEnabled(false);
				bananimator.stop();
				break;
			case HAVE_BANANA:
				getButton.setEnabled(false);
				bogartButton.setEnabled(true);
				stealButton.setEnabled(false);
				releaseButton.setEnabled(true);
				bananimator.start();
				break;
			case SOMEONE_ELSE_BANANA:
				getButton.setEnabled(false);
				bogartButton.setEnabled(false);
				stealButton.setEnabled(true);
				releaseButton.setEnabled(false);
				bananimator.stop();
			case BOGARTING_BANANA:
				getButton.setEnabled(false);
				bogartButton.setEnabled(false);
				stealButton.setEnabled(false);
				releaseButton.setEnabled(true);
				bananimator.stop();
				break;
			default:
		}

	}

	private void setState(int stateId) {
		Log.i("state:", ""+stateId);
		state = stateId;
		runOnUiThread(new Runnable() {
			public void run() {
				adjustButtons();
			}
		});
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
				if (state == CANT_GET_BANANA || state == NOBODY_HAS_BANANA) {
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

	public void processBanana(BananaEvent evt) {
		String msg = evt.getMessage().trim();
		switch (evt.getType()) {
			case BananaEvent.ERROR :
				error(msg);
				break;
			case BananaEvent.STATUS:
				status(msg);
				if (state == CANT_GET_BANANA)
					return;
				if (msg.startsWith("Nobody")) {
					setState(NOBODY_HAS_BANANA);
				} else if (msg.startsWith(userid)) {
					setState(HAVE_BANANA);
				} else if (msg.indexOf(" has ") >= 0) {
					setState(SOMEONE_ELSE_BANANA);
				}
                break;
			case BananaEvent.TAKE:
				String id = msg;
				if (msg.equalsIgnoreCase(userid)) {
					status("You took the banana!");
					setState(HAVE_BANANA);
				} else {
					status(id + " took the banana!");
					setState(SOMEONE_ELSE_BANANA);
				}
				break;
			case BananaEvent.RELEASE:
				status("You released the banana!");
				setState(NOBODY_HAS_BANANA);
				break;
			default:

		}
	}

	/**
	 * Server commands!
	 */

	private void commandTakeBanana() {
		client.commandTakeBanana();
	}

	private void commandStealBanana() {
		client.commandStealBanana();
	}

	private void commandReleaseBanana() {
		client.commandReleaseBanana();
	}

	private void commandContinueBogarting() {
		client.commandContinueBogarting();
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


