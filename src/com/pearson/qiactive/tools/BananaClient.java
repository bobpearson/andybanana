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

	public BananaClient(MainActivity par, String serverUrl) {

		this.par = par;
		this.serverUrl = serverUrl;
		listeners = new ArrayList<BananaListener>();

	}

	public void addListener(BananaListener listener) {
		listeners.add(listener);
	}

	String getText(String command, String user) {

		String res = "";
		try {
			String urlStr = serverUrl + "/" + command + "?" + "username=" + user;
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			BufferedReader ins = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			res = ins.readLine();
			ins.close();
		} catch (MalformedURLException e) {
			par.status(e.toString());
		} catch (IOException e) {
			par.status(e.toString());
		}
		return res;
	}

	void dispatchEvent(int type, String msg) {
		for (BananaListener listener : listeners) {
			listener.processBanana(type, msg);
		}
	}

	public boolean start() {
		keepGoing = true;
		while (keepGoing) {
			String res = getText("status", "bob");
			if (res.length() > 0) {
				par.status(res);
			}

		}
		return true;
	}

	public void stop() {
		keepGoing = false;
	}





}
