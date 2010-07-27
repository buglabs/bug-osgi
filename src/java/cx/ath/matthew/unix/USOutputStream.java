/*
 * Java Unix Sockets Library
 *
 * Copyright (c) Matthew Johnson 2004
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, version 2 only.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * To Contact the author, please email src@matthew.ath.cx
 *
 */
package cx.ath.matthew.unix;

import java.io.IOException;
import java.io.OutputStream;

public class USOutputStream extends OutputStream
{
   private native int native_send(int sock, byte[] b, int off, int len) throws IOException;
   private native int native_send(int sock, byte[][] b) throws IOException;
   private int sock;
   boolean closed = false;
   private byte[] onebuf = new byte[1];
   private UnixSocket us;
   public USOutputStream(int sock, UnixSocket us)
   {
      this.sock = sock;
      this.us = us;
   }
   public void close() throws IOException
   {
      closed = true;
      us.close();
   }
   public void flush() {} // no-op, we do not buffer
   public void write(byte[][] b) throws IOException
   {
      if (closed) throw new NotConnectedException();
      native_send(sock, b);
   }
   public void write(byte[] b, int off, int len) throws IOException
   {
      if (closed) throw new NotConnectedException();
      native_send(sock, b, off, len);
   }
   public void write(int b) throws IOException
   {
      onebuf[0] = (byte) (b % 0x7F);
      if (1 == (b % 0x80)) onebuf[0] = (byte) -onebuf[0];
      write(onebuf);
   }
   public boolean isClosed() { return closed; }
   public UnixSocket getSocket() { return us; }
}
