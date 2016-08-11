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

import ws.gmax.rtsp.auth.BasicAuth;
import ws.gmax.rtsp.auth.DigestAuth;

import java.util.Map;
import java.util.logging.Logger;

/**
 * RTSP Authentication
 *
 * @author Marius
 */
class RtspAuth {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(RtspAuth.class.getName());

    /* Authentication header */
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /* Basic authentication */
    private static final String BASIC = "Basic";

    /* Digest authentication */
    private static final String DIGEST = "Digest";

    /**
     * Build authentication token.
     *
     * @param headers  RTSP headers
     * @param username User name
     * @param password Password
     * @param uri      URI
     * @param method   RTSP method
     * @return Authentication token
     */
    static String auth_token(Map<String, String> headers,
                             String username, String password, String uri, String method) {

        // TODO use lower or upper line
        String authSchema = headers.get(WWW_AUTHENTICATE);
        if (authSchema != null) {
            if (authSchema.startsWith(BASIC)) {
                return BasicAuth.encode(username, password);
            } else if (authSchema.startsWith(DIGEST)) {
                return DigestAuth.encode(username, password,
                        authSchema, uri, method);
            } else {
                String msg = String.format("Unsupported authorization schema: %s",
                        authSchema);
                throw new RuntimeException(msg);
            }
        }

        LOGGER.info("Missing WWW-Authenticate header");
        return null;
    }
}
