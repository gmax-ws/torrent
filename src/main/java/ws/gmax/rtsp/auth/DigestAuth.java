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

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest Authentication encoder
 *
 * @author Marius
 */
public class DigestAuth {

    private static String getToken(String text, String token) {
        int beg = text.indexOf(token);
        if (beg != -1) {
            beg += token.length();
            int end = text.indexOf("\"", beg);
            if (end != -1) {
                return text.substring(beg, end);
            }
        }
        return null;
    }

    public static String md5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    public static String encode(String u, String p, String a, String uri, String m) {
        String realm = getToken(a, "realm=\"");
        String nonce = getToken(a, "nonce=\"");
        try {
            String a1 = md5(u + ":" + realm + ":" + p);
            String a2 = md5(m + ":" + uri);
            String hs = md5(a1 + ":" + nonce + ":" + a2);

            String format = "Digest username=\"%s\", realm=\"%s\", nonce=\"%s\", uri=\"%s\", response=\"%s\"";
            return String.format(format, u, realm, nonce, uri, hs);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
