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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RTSP Response
 *
 * @author Marius
 */
class RtspResponse {

    /* Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RtspResponse.class);

    /* Max buffer size */
    private static final int MAX_SIZE = 8192;

    /**
     * Structure to hold RTSP processed response.
     */
    private static class Response {

        /* RTSP response code */
        int code;

        /* RTSP response mesage */
        String message;

        /* RTSP response headers */
        Map<String, String> headers;

        /* RTSP response body */
        String body;

        /**
         * Construct a Response object.
         */
        Response() {
            headers = new HashMap<>();
        }
    }

    /* Stream buffer */
    private byte[] buffer = new byte[MAX_SIZE];

    /* Response input stream */
    private InputStream inp;

    /* Processed response */
    Response response;

    /**
     * Construct a RTSP response object.
     *
     * @param socket Client socket.
     * @throws IOException on error.
     */
    RtspResponse(Socket socket) throws IOException {
        inp = socket.getInputStream();
    }

    /**
     * Read and process RTSP response.
     *
     * @return RTSP response code.
     * @throws IOException on error.
     */
    int doResponse() throws IOException {
        response = new Response();
        int count = inp.read(buffer);
        String text = (count > 0) ? new String(buffer, 0, count) : null;
        LOGGER.info(text);
        process(text);
        return response.code;
    }

    /**
     * Decode RTSP response code.
     *
     * @param line Response line.
     */
    private void decodeRtspCode(String line) {
        String regex = "^" + RtspProtocol.RTSP_PROTOCOL + "\\s+(\\d{3})\\s+(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            throw new RuntimeException("Unknown RTSP response.");
        }
        response.code = Integer.parseInt(matcher.group(1));
        response.message = matcher.group(2);
    }

    /**
     * Decode RTSP header.
     *
     * @param line Header string.
     */
    private void decodeHeader(String line) {
        Pattern pattern = Pattern.compile("^(.+):\\s+(.+)$");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            throw new RuntimeException("Unknown RTSP header.");
        }
        response.headers.put(matcher.group(1), matcher.group(2));
    }

    /**
     * Get Session header.
     *
     * @return Session header value or <code>null</code> if not found.
     */
    String getSession() {
        return response.headers.get("Session");
    }

    /**
     * Process RTSP response.
     *
     * @param text response lines
     */
    private void process(String text) {
        StringBuilder sb = new StringBuilder();
        Scanner reader = new Scanner(text);
        boolean isHeader = true;

        if (reader.hasNext()) {
            // first line
            String line = reader.nextLine();
            if (line.startsWith(RtspProtocol.RTSP_PROTOCOL)) {
                decodeRtspCode(line);
            }
            // next lines
            while (reader.hasNext()) {
                line = reader.nextLine();
                if (line.isEmpty()) {
                    isHeader = false;
                } else if (isHeader) {
                    decodeHeader(line);
                } else {
                    sb.append(line).append("\r\n");
                }
            }
        }
        //
        response.body = sb.toString();
    }
}
