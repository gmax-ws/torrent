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
package ws.gmax.rtsp.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encode username and password for HTTP Basic authentication
 */
public class BasicAuth {

    private BasicAuth() {
    }

    /**
     * Encode a username/password pair appropriate to use in an HTTP header for
     * Basic Authentication.
     *
     * @param username     the user's username
     * @param password the user's password
     * @return String the base64 encoded username:password
     */
    public static String encode(String username, String password) {
        String token = String.format("%s:%s", username, password);
        byte[] token_bytes = token.getBytes(StandardCharsets.UTF_8);
        return String.format("Basic %s", Base64.getEncoder().encodeToString(token_bytes));
    }
}
