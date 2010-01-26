/*******************************************************************************
 * Copyright (c) 2008, 2009 Bug Labs, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Bug Labs, Inc. nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.buglabs.bug.base;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.midpath.sound.AudioFormat;
import org.thenesis.midpath.sound.Line;
import org.thenesis.midpath.sound.Mixer;
import org.thenesis.midpath.sound.SoundBackend;
import org.thenesis.midpath.sound.SoundToolkit;
import org.thenesis.midpath.sound.codec.AudioDecoder;
import org.thenesis.midpath.sound.codec.DecoderCallback;
import org.thenesis.midpath.sound.codec.WaveDecoder;

import com.buglabs.bug.base.pub.IBaseAudioPlayer;

public class SoundPlayer implements IBaseAudioPlayer {

	public class DecodingThread implements Runnable {

		private volatile Thread thread;
		private volatile boolean paused = false;
		private volatile boolean closed = false;
		private AudioDecoder decoder;
		private Line line;

		private DecoderCallback decoderCallback = new DecoderCallback() {
			public void write(byte[] buf, int offset, int length) {
				//System.out.println("[DEBUG] DecoderCallback.write(): start");
				line.write(buf, offset, length);
				//System.out.println("[DEBUG] DecoderCallback.write(): end");
			}
		};

		public DecodingThread(AudioDecoder decoder, Line line) {
			this.decoder = decoder;
			this.line = line;
		}

		public synchronized void close() {
			line.stop();
			thread = null;
			closed = true;
		}

		public boolean isClosed() {
			return closed;
		}

		public boolean isPaused() {
			return paused;
		}

		public synchronized void pause() {
			line.stop();
			paused = true;
		}

		public synchronized void resume() {
			line.start();
			paused = false;
			notify();
		}

		public void run() {

			try {

				while (Thread.currentThread() == thread) {

					synchronized (DecodingThread.this) {
						if (paused) {
							try {
								wait();
							} catch (InterruptedException e) {
							}
						}
					}

					if (DecodingThread.this.decoder.decodeStep(decoderCallback) < 0) {
						close();
						break;
					}

					//System.out.println("[DEBUG] DecodingThread.run(): 2");
				}
			} catch (IOException e) {
				close();
			} finally {
				endOfMediaReached();
			}

		}

		public synchronized void start() {
			if ((!closed) && (thread == null)) {
				line.start();
				thread = new Thread(this);
				thread.start();
			}
		}

	}
	private volatile DecodingThread decodingThread;
	private volatile AudioDecoder decoder;

	private SoundBackend backend;
	
	
	public SoundPlayer(String deviceName) {
		try {
			SoundToolkit.deviceName = deviceName;			
			backend = SoundToolkit.getBackend();
			backend.open();
		} catch (IOException e) {
			System.err.println("Exception in AudioPlayer implementation: cannot open alsa device");
			e.printStackTrace();
		}
	}

	public void endOfMediaReached() {
		//System.out.println("[DEBUG] VirtualPlayer.endOfMediaReached(): start");
		System.out.println("End of media reached");
		//System.out.println("[DEBUG] VirtualPlayer.endOfMediaReached(): end");
	}
	
	public void pause(){
		decodingThread.pause();
	}
	
	public void play(InputStream is) throws IOException {

		SoundBackend backend = SoundToolkit.getBackend();
		
		try {

			decoder = new WaveDecoder();
			decoder.initialize(is);
			Mixer mixer = backend.getMixer();
			final Line l = mixer.createLine(new AudioFormat(44100,
					AudioFormat.BITS_16, AudioFormat.STEREO, true, false));
			mixer.addLine(l);
			l.start();
			if ((decodingThread == null) || decodingThread.isClosed()) {
				decodingThread = new DecodingThread(decoder, l);
			}

			if (decodingThread.isPaused()) {
				decodingThread.resume();
			} else {
				decodingThread.start();
			}

			//System.out.println("[DEBUG] VirtualPlayer.doStart(): 2");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void resume(){
		decodingThread.resume();
	}

}
