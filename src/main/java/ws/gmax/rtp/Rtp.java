/*
 * RTSP/RTP torrent
 * Copyright (c) 2016 Marius Gligor
 *
 * Author: Marius Gligor <marius.gligor@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111, USA.
 */
package ws.gmax.rtp;

import java.io.IOException;
import java.net.*;

/**
 * An abstract class implementing a RTP (UDP) client.
 *
 * @author Marius Gligor
 */
abstract class Rtp {

    /* Max packet size */
    private static final int PACKET_SIZE = 8192;

    /* UDP socket */
    private DatagramSocket socket;

    /* UDP datagram packet */
    private DatagramPacket packet;

    /* Client name or IP address */
    private final String host;

    /* Client port number */
    public final int port;

    /**
     * Constructor. Build a RtpPlayer instance.
     *
     * @param host Client name or IP address.
     * @param port Client port number.
     */
    Rtp(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Called on receive data.
     *
     * @param buffer Data bytes.
     * @param len    Data size.
     */
    public abstract void onReceiveData(final byte[] buffer, final int len);

    /**
     * Read data from UDP socket and fire <code>onReceiveData</code> event.
     *
     * @throws IOException On I/O errors
     */
    void receive() throws IOException {
        if (packet != null) {
            socket.receive(packet);
            onReceiveData(packet.getData(), packet.getLength());
        }
    }

    /**
     * Prepare UDP datagram.
     *
     * @throws SocketException      On error
     * @throws UnknownHostException On error
     */
    void openUdp() throws SocketException, UnknownHostException {
        if (host == null) {
            socket = new DatagramSocket(port);
        } else {
            InetAddress address = InetAddress.getByName(host);
            socket = new DatagramSocket(port, address);
        }

        packet = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
    }

    /**
     * Close UDP socket.
     */
    void closeUdp() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }
}
