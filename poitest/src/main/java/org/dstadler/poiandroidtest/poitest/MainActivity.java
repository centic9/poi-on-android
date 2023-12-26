package org.dstadler.poiandroidtest.poitest;

import android.app.Activity;
import android.os.Bundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainActivity extends Activity {
	private static final Logger LOG = LogManager.getLogger(MainActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LOG.info("Testing...");
	}
}
