package net.cheney.reactor;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

public abstract class AsyncSocketChannel extends AsyncByteChannel<SocketChannel> {
	private static final Logger LOG = Logger.getLogger(AsyncSocketChannel.class);
	
	private final ClientProtocolFactory factory;

	protected AsyncSocketChannel(@Nonnull Reactor reactor, @Nonnull SocketChannel channel, @Nonnull ClientProtocolFactory factory, @Nonnegative int ops) throws IOException {
		super(reactor, channel, ops);
		this.factory = factory;
	}
	
	public final void enableConnectInterest() {
		reactor().enableInterest(this, SelectionKey.OP_CONNECT);
	}
	
	public final void disableConnectInterest() {
		reactor().disableInterest(this, SelectionKey.OP_CONNECT);
	}

	public final SocketAddress peer() {
		return socket().getRemoteSocketAddress();
	}
	
	public final Socket socket() {
		return channel().socket();
	}
	
	protected final ClientProtocolFactory factory() {
		return this.factory;
	}

	public final void onConnect() throws IOException {
		if (channel().finishConnect()) {
			reactor().disableInterest(this, SelectionKey.OP_CONNECT);
			factory().completed(this);
		} else {
			LOG.warn(String.format("%s failed to finishConnect()", channel()));
		}
	}

	final void connect(@Nonnull SocketAddress addr) throws IOException {
		channel().connect(addr);
//		enableConnectInterest();
	}
}
