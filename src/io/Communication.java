package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

public class Communication {

	private ObjectInputStream objin;
	private ObjectOutputStream objout;

	public Communication() {
		this.objin = null;
		this.objout = null;
	}

	public Communication(InputStream in, OutputStream out) {

		try {
			this.objout = new ObjectOutputStream(out);
			this.objout.flush();
			this.objin = new ObjectInputStream(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Communication(Socket sock) throws IOException {
		this(sock.getInputStream(), sock.getOutputStream());
	}

	public Communication connectLocally() {
		try {
			PipedOutputStream thisout = new PipedOutputStream();
			PipedInputStream thisin = new PipedInputStream();

			PipedOutputStream otherout = new PipedOutputStream();
			PipedInputStream otherin = new PipedInputStream();

			otherout.connect(thisin);
			thisout.connect(otherin);

			this.objout = new ObjectOutputStream(thisout);
			this.flush();
			Communication othercomm = new Communication(otherin, otherout);
			othercomm.flush();

			this.objin = new ObjectInputStream(thisin);

			return othercomm;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void sendObject(Object obj) {
		this.sendObject(obj, true);
	}

	public void sendObject(Object obj, boolean flush) {
		try {
			this.objout.writeObject(obj);
			if (flush) {
				this.objout.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Object recieveObject() {
		try {
			return this.objin.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void flush() {
		try {
			this.objout.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
