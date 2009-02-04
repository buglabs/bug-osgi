package com.buglabs.bug.module.vonhippel.pub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IVonHippelSerialPort {
	
	/**
	 * Gets the input stream associated with the RS232 port on Von Hippel
	 * module. This implementation is based on the javax.microedition.commports
	 * API. The port is set up with the following parameters in
	 * VonHippelModuleControl: baudrate=9600 bitsperchar=8 stopbits=1
	 * parity=none autocts=off autorts=off blocking=off
	 * 
	 * @return stream associated with RS232 input (reading)
	 */
	public InputStream getSerialInputStream() throws IOException;
	
	/**
	 * Gets the output stream associated with the RS232 port on Von Hippel
	 * module. This implementation is based on the javax.microedition.commports
	 * API. The port is set up with the following parameters in
	 * VonHippelModuleControl: baudrate=9600 bitsperchar=8 stopbits=1
	 * parity=none autocts=off autorts=off blocking=off
	 * 
	 * @return stream associated with RS232 output (writing)
	 */
	public OutputStream getSerialOutputStream() throws IOException;
	
	/**
	 * @return true if a client has already opened the input stream.
	 */
	public boolean isInputStreamOpen();
	
	/**
	 * @return true if a client has already opened the output stream.
	 */
	public boolean isOutputStreamOpen();
	
	/**
	 * @return baud rate setting for serial port.
	 */
	public String getBaudrate();

	/**
	 * Set baud rate for serial port. 
	 * @param baudrate
	 * @throws IOException Is thrown if connection is already open.
	 */
	public void setBaudrate(String baudrate) throws IOException;

	/**
	 * @return Bits per char of serial connection.
	 */
	public int getBitsPerChar();

	/**
	 * @param bitsPerChar
	 * @throws IOException Is thrown if connection is already open.
	 */
	public void setBitsPerChar(int bitsPerChar) throws IOException;

	/**
	 * @return get stop bits of serial connection.
	 */
	public String getStopBits();

	/**
	 * @param stopBits
	 * @throws IOException Is thrown if connection is already open.
	 */
	public void setStopBits(String stopBits) throws IOException;

	/**
	 * @return
	 */
	public String getParity();

	/**
	 * @param parity
	 * @throws IOException Is thrown if connection is already open.
	 */
	public void setParity(String parity) throws IOException;

	/**
	 * @return
	 */
	public boolean getAutoCTS();

	/**
	 * @param autoCTS
	 * @throws IOException Is thrown if connection is already open.
	 */
	public void setAutoCTS(boolean autoCTS) throws IOException;

	/**
	 * @return Is thrown if connection is already open.
	 */
	public boolean getAutoRTS() ;

	/**
	 * @param autoRTS
	 * @throws IOException
	 */
	public void setAutoRTS(boolean autoRTS) throws IOException;

	/**
	 * @return
	 */
	public boolean getBlocking() ;

	/**
	 * @param blocking
	 * @throws IOException
	 */
	public void setBlocking(boolean blocking) throws IOException;
}
