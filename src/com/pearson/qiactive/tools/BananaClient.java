package com.pearson.qiactive.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by vjamiro on 7/3/14.
 */
public class BananaClient {

	private String serverUrl;
	private MainActivity par;
	private ArrayList<BananaListener> listeners;
	private boolean keepGoing;
	private String user;

	public BananaClient(MainActivity par, String serverUrl, String user) {

		this.par = par;
		this.serverUrl = serverUrl;
		this.user = user;
		listeners = new ArrayList<BananaListener>();

	}

	private void error(String msg) {
		dispatchEvent(new BananaEvent(BananaEvent.ERROR, msg));
	}

	private void status(String msg) {
		dispatchEvent(new BananaEvent(BananaEvent.STATUS, msg));
	}

	void dispatchEvent(BananaEvent evt) {
		for (BananaListener listener : listeners) {
			listener.processBanana(evt);
		}
	}

	public void addListener(BananaListener listener) {
		listeners.add(listener);
	}

	private void parse(String command, String result) {

		if ("status".equals(command)) {
			dispatchEvent(new BananaEvent(BananaEvent.STATUS, result));
		} else if ("take".equals(command)) {

		} else if ("steal".equals(command)) {

		} else if ("release".equals(command)) {

		} else if ("bogart".equals(command)) {

		} else {
			//duh!
		}

	}

	private  void doCommand(String command) {
		try {
			String urlStr = serverUrl + "/" + command + "?" + "username=" + user;
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			BufferedReader ins = new BufferedReader(new InputStreamReader(conn.getInputStream()), 1024);
			String res = ins.readLine();
			ins.close();
			parse(command, res);
		} catch (MalformedURLException e) {
			error(e.toString());
		} catch (IOException e) {
			error(e.toString());
		}
	}


	void commandTakeBanana() {
		doCommand("take");
	}

	void commandStealBanana() {
		doCommand("steal");
	}

	void commandReleaseBanana() {
		doCommand("release");
	}

	void commandContinueBogarting() {
		doCommand("bogart");
	}



	public boolean start() {
		Thread t = new Thread("bananaclient-thread") {
			@Override
			public void run() {
				keepGoing = true;
				while (keepGoing) {
					doCommand("status");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						error("poll thread");
					}

				}
			}
		};
		t.start();
		return true;
	}

	public void stop() {
		keepGoing = false;
	}





}
