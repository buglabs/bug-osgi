/*
 * Copyright ThinkTank Maths Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package thinktank.j2me;

import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 * Wrapper that turns the asynchronous Bluetooth Service Discovery process into a
 * synchronous one. Makes programming easier but potentially makes the end user process
 * less responsive as this will lock up the device for about 10 seconds. Best do this in a
 * background thread.
 * 
 * @author Samuel Halliday, ThinkTank Maths Limited
 * @see BluetoothDeviceDiscover
 * @see http://developers.sun.com/mobility/apis/articles/bluetoothobex/
 */
public class BluetoothServiceDiscover {
	/**
	 * @author Samuel Halliday, ThinkTank Maths Limited
	 */
	private final class BluetoothHelper implements DiscoveryListener {
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			// not looking for devices. Dumb interface! grr...
		}

		public void inquiryCompleted(int discType) {
			// not looking for devices. Dumb interface! grr...
		}

		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			System.out.println("serviceDiscovered " + transID + " " +
				servRecord.length);
			for (int i = 0; i < servRecord.length; i++) {
				serviceRecords.addElement(servRecord[i]);
			}
		}

		public void serviceSearchCompleted(int transID, int respCode) {
			System.out.println("serviceSearchCompleted " + transID + " " +
				respCode);
			finish();
		}
	}

	private final RemoteDevice device;

	private boolean finished;

	/** Can only run once per instance */
	private volatile boolean ran = false;

	private final Runnable runner = new Runnable() {
		public void run() {
			ran = true;
			LocalDevice localDevice;
			try {
				localDevice = LocalDevice.getLocalDevice();
			} catch (BluetoothStateException e) {
				synchronized (waiter) {
					waiter.notify();
				}
				return;
			}
			DiscoveryAgent discoveryAgent = localDevice.getDiscoveryAgent();
			try {
				discoveryAgent.searchServices(null, uuidSet, device,
					new BluetoothHelper());
			} catch (Exception e) {
				synchronized (waiter) {
					waiter.notify();
				}
				return;
			}
		}
	};

	private final Vector serviceRecords = new Vector();

	private final UUID[] uuidSet;

	private final Object waiter = new Object();

	/**
	 * Convenience method for searching a single service with a single attribute.
	 * TODO: update so that uuids not taken on construction, but on discover
	 * 
	 * @param device
	 * @param uuid
	 */
	public BluetoothServiceDiscover(RemoteDevice device, int uuid) {
		this.device = device;
		this.uuidSet = new UUID[]{new UUID(uuid)};
	}

	/**
	 * TODO: update so that uuids not taken on construction, but on discover
	 * @param device
	 * @param uuidSet
	 */
	public BluetoothServiceDiscover(RemoteDevice device, int[] uuidSet) {
		this.device = device;
		this.uuidSet = new UUID[uuidSet.length];
		for (int i = 0; i < uuidSet.length; i++) {
			this.uuidSet[i] = new UUID(uuidSet[i]);
		}
	}

	/**
	 * @return the {@link ServiceRecord}s for this device
	 */
	public Vector discover() {
		if (ran)
			throw new RuntimeException(
				"Invalid state... already started lookup.");
		Thread thread = new Thread(runner);
		thread.start();
		synchronized (waiter) {
			while (!finished) {
				try {
					waiter.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return serviceRecords;
	}

	private void finish() {
		synchronized (waiter) {
			finished = true;
			waiter.notify();
		}
	}
}
