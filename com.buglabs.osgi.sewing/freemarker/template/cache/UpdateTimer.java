/*
 * FreeMarker: a tool that allows Java programs to generate HTML
 * output using templates.
 * Copyright (C) 1998-2004 Benjamin Geer
 * Email: beroul@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package freemarker.template.cache;

/**
 * <p>
 * A timer for objects that implement <tt>Updateable</tt>. Creates a new thread,
 * in which it periodically calls the <tt>Updateable</tt>'s <tt>update()</tt>
 * method.
 * </p>
 * 
 * <p>
 * Uses techniques from the document &quot;Why Are <code>Thread.stop</code>,
 * <code>Thread.suspend</code>, <code>Thread.resume</code> and
 * <code>Runtime.runFinalizersOnExit</code> Deprecated?&quot;, from the Java API
 * documentation.
 * </p>
 * 
 * @version $Id: UpdateTimer.java 987 2004-10-05 10:13:24Z run2000 $
 */
public final class UpdateTimer implements Runnable {
	private final Updateable target;
	private final Long delay;
	private volatile Thread timerThread;

	/**
	 * Constructs the timer with the update target and update interval.
	 * 
	 * @param target
	 *            the object to be updated.
	 * @param delay
	 *            the number of milliseconds between updates.
	 */
	public UpdateTimer(Updateable target, long delay) {
		this.target = target;
		this.delay = new Long(delay);
	}

	/**
	 * Begins periodic automatic updates of the target. Since it can't be
	 * determined if an existing thread is Runnable or not, create and start a
	 * new thread
	 * 
	 * @param niceness
	 *            How much to decrease the priority of the timer thread by. The
	 *            value is applied against the default priority of the new
	 *            thread. The value may be negative, to indicate that the thread
	 *            should have a greater priority than the default.
	 */
	public void startTiming(int niceness) {
		int priority;

		timerThread = new Thread(this);
		priority = timerThread.getPriority();

		// "Nice" the new thread, to run it at a lower priority than
		// the threads actually servicing requests. Note that we don't
		// want to set the thread to minimum priority, since this would
		// result in the thread only being able to run when no other threads
		// are active. Similarly, maximum priority would also be bad.
		if ((niceness != 0) && (priority - niceness > Thread.MIN_PRIORITY) && (priority - niceness < Thread.MAX_PRIORITY)) {
			timerThread.setPriority(priority - niceness);
		}

		timerThread.start();
	}

	/**
	 * Stops (immediately) automatically updating the target.
	 */
	public void stopTiming() {
		Thread oldThread = timerThread;

		timerThread = null;
		if (oldThread != null) {
			oldThread.interrupt();
		}
	}

	/**
	 * Waits for the given period, then calls <code>update()</code>, if
	 * required. If someone decides to kill this thread, exit immediately.
	 */
	public void run() {
		while (timerThread != null) {
			try {
				Thread.sleep(delay.longValue());
				if (timerThread != null) {
					target.update();
				}
			} catch (InterruptedException e) {
				// Do nothing. If the thread needs to be killed, then the
				// outer while loop will return false.
			}
		}
	}
}
