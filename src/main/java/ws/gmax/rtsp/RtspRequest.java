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
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ws.gmax.rtsp.RtspProtocol.RTSP_PROTOCOL;

/**
 * RtspRequest
 *
 * @author Marius
 */
class RtspRequest {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(RtspRequest.class.getName());

    /* Request stream */
    private OutputStream out;

    /**
     * Construct a RTSP request.
     *
     * @param socket Network socket.
     * @throws IOException on error
     */
    RtspRequest(Socket socket) throws IOException {
        out = socket.getOutputStream();
    }

    /**
     * Submit request
     *
     * @param command request string.
     * @throws Exception on error
     */
    private void doRequest(String command) throws Exception {
        out.write(command.getBytes());
        out.flush();
        LOGGER.log(Level.INFO, command);
    }

    /**
     * Build and submit request.
     *
     * @param req Request part
     * @param hdr Request headers
     * @throws Exception on error
     */
    void doRequest(StringBuilder req, Map<String, String> hdr)
            throws Exception {
        req.append(" ").
                append(RTSP_PROTOCOL).
                append("\r\n");
        // hdr.entrySet().stream().forEach((item) -> {
        for (Map.Entry<String, String> item : hdr.entrySet()) {
            req.append(item.getKey()).
                    append(": ").
                    append(item.getValue()).
                    append("\r\n");
        }
        req.append("\r\n");
        doRequest(req.toString());
    }

    /**
     * Build and submit request.
     *
     * @param req  Request part
     * @param hdr  Request headers
     * @param body Request body
     * @throws Exception on error
     */
    void doRequest(StringBuilder req, Map<String, String> hdr, String body)
            throws Exception {
        req.append(" ").
                append(RTSP_PROTOCOL).
                append("\r\n");
        // hdr.entrySet().stream().forEach((item) -> {
        for (Map.Entry<String, String> item : hdr.entrySet()) {
            req.append(item.getKey()).
                    append(": ").
                    append(item.getValue()).
                    append("\r\n");
        }
        req.append("\r\n");
        req.append(body);
        doRequest(req.toString());
    }
}
