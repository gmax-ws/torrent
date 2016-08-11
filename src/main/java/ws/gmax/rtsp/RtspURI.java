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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Split RTSP URI into parts
 *
 * @author Marius
 */
class RtspURI {

    /* Protocol should be always rtsp */
    private static final String PROTOCOL = "rtsp";

    /* Host name or address */
    String host;

    /* Port number, -1 missing use default */
    int port = -1;

    /* Path part */
    String path;

    /**
     * Split URI string into parts.
     *
     * @param rtspUri URI string
     * @return This object
     * @throws URISyntaxException when invalid URI string
     */
    RtspURI split(String rtspUri) throws URISyntaxException {
        URI uri = new URI(rtspUri);

        if (PROTOCOL.equalsIgnoreCase(uri.getScheme())) {
            host = uri.getHost();
            port = uri.getPort();
            path = uri.getPath();
        } else {
            throw new RuntimeException(String.format("Protocol MUST be %s",
                    PROTOCOL));
        }

        return this;
    }
}
