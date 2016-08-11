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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An abstract class implementing an RTP (UDP) client.
 *
 * @author Marius
 */
public abstract class RtpPlayer extends Rtp implements Runnable {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(RtpPlayer.class.getName());

    /* Exit run loop condition */
    private volatile boolean terminated = false;

    /**
     * Constructor. Build a RtpPlayer instance.
     *
     * @param host Client name or IP address.
     * @param port Client port number.
     */
    public RtpPlayer(final String host, final int port) {
        super(host, port);
    }

    /**
     * Stop player thread.
     */
    public void stop() {
        terminated = true;
        // closeUdp();
    }

    /**
     * Start player thread.
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Read data from UDP socket and fire <code>onReceiveData</code> event.
     */
    @Override
    public void run() {
        try {
            openUdp();
            while (!terminated) {
                receive();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            closeUdp();
        }
    }

    /**
     * Decode RTP packet.
     *
     * @param packet RTP packet bytes.
     * @param len    Size of packets.
     * @return Decoded packet.
     */
    protected RtpDecoder decode(byte[] packet, int len) {
        return new RtpDecoder().decode(packet, len);
    }
}
