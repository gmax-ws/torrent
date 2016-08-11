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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marius
 *         <p>
 *         RTSP/1.0 200 OK CSeq: 2 Date: Thu, Apr 22 2010 20:38:14 GMT Transport:
 *         RTP/AVP;unicast;destination=86.123.151.79;source=93.89.112.125;client_port=9000-9001;server_port=6970-6971
 *         Session: 65
 */
@Deprecated
public class Transport {

    private Transport() {
    }

    /**
     * Get server port
     *
     * @param transport Transport line string.
     * @return server port as string
     */
    public static String getServerPort(String transport) {

        if (transport == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("server_port=(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(transport);
        return (matcher.find()) ? matcher.group(1) : null;
    }

    /**
     * Server address
     *
     * @param transport Transport line string.
     * @return Server address or name
     */
    public static String getServerAddress(String transport) {

        if (transport == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("source=(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3});");
        Matcher matcher = pattern.matcher(transport);
        return (matcher.find()) ? matcher.group(1) : null;
    }
}
