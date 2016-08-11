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
package ws.gmax.rtsp;

import java.io.IOException;
import java.net.Socket;

/**
 * RTSP client
 *
 * @author Marius
 */
class Rtsp {

    /* RTSP protocol */
    static final String RTSP_PROTOCOL = "RTSP/1.0";

    /* RTSP default port */
    static final int DEFAULT_RTSP_PORT = 554;

    /* Socket timeout */
    private static final int TIMEOUT = 10000;

    /* Network socket */
    private Socket socket;

    /* Request object */
    private RtspRequest request;

    /* Response object */
    private RtspResponse response;

    /* RTSP base uri */
    String rtspUri;

    Rtsp() {
    }

    /**
     * Connect to RTSP server
     *
     * @param host Server address or name
     * @param port Server port
     * @throws IOException on error
     */
    void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setKeepAlive(true);
        socket.setReuseAddress(true);
        socket.setSoTimeout(TIMEOUT);

        request = new RtspRequest(socket);
        response = new RtspResponse(socket);

        rtspUri = String.format("rtsp://%s:%d", host, port);
    }

    /**
     * Connect to server using default RTSP port.
     *
     * @param ip Server address
     * @throws Exception on error
     */
    void connect(String ip) throws Exception {
        connect(ip, DEFAULT_RTSP_PORT);
    }

    /**
     * Close connection.
     *
     * @throws Exception on error
     */
    void disconnect() throws Exception {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    /**
     * Check if connected.
     *
     * @return <code>true</code> if connected <code>false</code> if not
     * @throws Exception on error
     */
    public boolean isConnected() throws Exception {
        return (socket != null) && socket.isConnected();
    }

    RtspRequest getReqest() {
        return request;
    }

    RtspResponse getResponse() {
        return response;
    }
}
