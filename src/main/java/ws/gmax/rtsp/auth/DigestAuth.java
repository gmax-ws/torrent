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

/**
 * Digest Authentication encoder
 *
 * @author Marius
 */
public class DigestAuth {

    private static String getToken(String text, String token) {
        int beg = text.indexOf(token, 0);
        if (beg != -1) {
            beg += token.length();
            int end = text.indexOf("\"", beg);
            if (end != -1) {
                return text.substring(beg, end);
            }
        }
        return null;
    }

    static public String encode(String u, String p, String a, String uri, String m) {
        String realm = getToken(a, "realm=\"");
        String nonce = getToken(a, "nonce=\"");
        String a1 = new MD5(u + ":" + realm + ":" + p).asHex();
        String a2 = new MD5(m + ":" + uri).asHex();
        String hs = new MD5(a1 + ":" + nonce + ":" + a2).asHex();

        return String.join("Digest username=\"", u,
                "\", realm=\"", realm,
                "\", nonce=\"", nonce,
                "\", uri=\"", uri,
                "\", response=\"", hs,
                "\"");
    }
}
