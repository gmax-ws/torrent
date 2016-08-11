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
 * This class encodes a user name and a password in format
 * (Base64) required by HTTP Basic authentication.
 */
class BasicAuth2 {

    private BasicAuth2() {
    }

    /* Conversion table */
    private static final byte[] ALPHABET = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
            (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
            (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
            (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
            (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y',
            (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
            (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j',
            (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
            (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't',
            (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y',
            (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
            (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
            (byte) '+', (byte) '/', (byte) '='
    };

    /**
     * Encode a name/password pair appropriate to
     * use in an HTTP header for Basic Authentication.
     *
     * @param name     the user's name
     * @param password the user's password
     * @return The base64 encoded string for name:password
     */
    public static String encode(String name, String password) {
        // Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8))
        return "Basic " + new String(encode((name + ":" + password).getBytes()));
    }

    /**
     * Base64 encoder
     *
     * @param data Data to be encoded.
     * @return Data Base64 encoded
     */
    private static byte[] encode(byte[] data) {
        byte[] out = new byte[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = ((int) data[i] & 0xff);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= ((int) data[i + 1] & 0xff);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= ((int) data[i + 2] & 0xff);
                quad = true;
            }
            out[index + 3] = ALPHABET[quad ? (val & 0x3f) : 64];
            val >>= 6;
            out[index + 2] = ALPHABET[trip ? (val & 0x3f) : 64];
            val >>= 6;
            out[index + 1] = ALPHABET[val & 0x3f];
            val >>= 6;
            out[index] = ALPHABET[val & 0x3f];
        }
        return out;
    }
}
