package com.pearson.qiactive.tools;

/**
 * Implemented by all clients who wish to receive BananaClient events
 */
public interface BananaListener {

	public void processBanana(int type, String msg);

}
