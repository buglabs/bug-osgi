package com.buglabs.bug.module.gps.pub;

import org.osgi.util.position.Position;

/**
 * Register this service to receive position change events.
 * @author kgilmer
 *
 */
public interface IPositionSubscriber {
	
	/**
	 * @param Position
	 */
	public void positionUpdate(Position position);
}
