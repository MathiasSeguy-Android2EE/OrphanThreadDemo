/**<ul>
 * <li>OrphanThreadDemo</li>
 * <li>com.android2ee.tuto.thread.orphan</li>
 * <li>15 déc. 2011</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.tuto.thread.orphan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to show the bad behavior of an handler that leaves orphan thread when
 *        activity's life cycle is not managed:
 *        !!!WARNING :This code is an example to not follow !!!
 *        <ul>
 *        <li></li>
 *        </ul>
 */
public class OrphanThreadDemoActivity extends Activity {
	/******************************************************************************************/
	/** Managing the Handler and the Thread *************************************************/
	/******************************************************************************************/
	/**
	 * The Handler
	 */
	private Handler handler;
	/**
	 * The thread that update the progressbar
	 */
	Thread backgroundThread;
	/******************************************************************************************/
	/** Others attributes **************************************************************************/
	/******************************************************************************************/
	/**
	 * The string for the log
	 */
	private final static String TAG = "OrphanThreadDemoActivity";
	/**
	 * The ProgressBar
	 */
	private ProgressBar progressBar;
	/**
	 * The way the progress bar increment
	 */
	private boolean reverse = false;	
	/**
	 * The activity name
	 */
	private  String activityName;

	/******************************************************************************************/
	/** Managing the activity **************************************************************************/
	/******************************************************************************************/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Instantiate the progress bar
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setMax(100);
		// use a random double to give a name to the thread, the handler and the activity
		double randomD = Math.random();
		final int randomName = (int) (randomD * 100);
		activityName="Activity"+randomName;
		// handler definition
		handler = new Handler() {
			/**
			 * The handler name
			 */
			String handlerName="HandlerName"+randomName;
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// retrieve the calling thread's name
				int threadId = (Integer) msg.getData().get("ThreadId");
				Log.e(TAG, "The handler,"+handlerName+" receives a message from the thread n°" + threadId);
				// Launch treatment
				updateProgress();
			}
		};
		// use a random double to give a name to the thread
		// Define the Thread and the link with the handler
		backgroundThread = new Thread(new Runnable() {
			/**
			 * The message exchanged between this thread and the handler
			 */
			Message myMessage;
			/**
			 * A bound to stop the Thread... else it runs for ever
			 */
			int bound = 0;

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					Log.e(TAG, "NewThread " + randomName);
					while (bound < 1000) {
						bound++;
						Log.e(TAG, "Thread is true " + randomName);
						// For example sleep 1 second
						Thread.sleep(100);
						// Send the message to the handler (the
						// handler.obtainMessage is more
						// efficient that creating a message from scratch)
						// create a message, the best way is to use that
						// method:
						myMessage = handler.obtainMessage();
						// put the thread id in the message to show which handler take it:
						Bundle data = new Bundle();
						data.putInt("ThreadId", randomName);
						myMessage.setData(data);
						// then send the message to the handler
						handler.sendMessage(myMessage);
					}
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		});
		backgroundThread.setName("HandlerTutoActivity " + randomName);
		// start the thread
		backgroundThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		Log.w(TAG, "onDestroy called");
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		Log.w(TAG, "onPause called");
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		Log.w(TAG, "onResume called");
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		// Save the state of the reverse boolean
		outState.putBoolean("reverse", reverse);
		// then save the others GUI elements state
		super.onSaveInstanceState(outState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the state of the reverse boolean
		reverse = savedInstanceState.getBoolean("reverse");
		// then restore the others GUI elements state
		super.onRestoreInstanceState(savedInstanceState);
	}

	/******************************************************************************************/
	/** Private methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * The method that update the progressBar
	 */
	private void updateProgress() {
		Log.e(TAG, "updateProgress called  (on activity n°"+activityName+")");
		
		// get the current value of the progress bar
		int progress = progressBar.getProgress();
		// if the max is reached then reverse the progressbar's progress
		// if the 0 is reached then set the progressbar's progress normal
		if (progress == progressBar.getMax()) {
			reverse = true;
		} else if (progress == 0) {
			reverse = false;
		}
		// increment the progress bar according to the reverse boolean
		if (reverse) {
			progressBar.incrementProgressBy(-1);
		} else {
			progressBar.incrementProgressBy(1);
		}
	}
}