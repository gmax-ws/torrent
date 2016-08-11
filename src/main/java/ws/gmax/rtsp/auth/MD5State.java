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
 * Fast implementation of RSA's MD5 hash generator in Java JDK Beta-2 or higher<br>
 * Originally written by Santeri Paavolainen, Helsinki Finland 1996 <br>
 * (c) Santeri Paavolainen, Helsinki Finland 1996 <br>
 * Some changes Copyright (c) 2002 Timothy W Macinta <br>
 *
 * @author Santeri Paavolainen <sjpaavol@cc.helsinki.fi>
 * @author Timothy W Macinta (twm@alum.mit.edu) (optimizations and bug fixes)
 **/
class MD5State {

    /**
     * 128-bit state
     */
    int state[] = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};
    /**
     * 64-bit character count
     */
    long count = 0;
    /**
     * 64-byte buffer (512 bits) for storing to-be-hashed characters
     */
    byte[] buffer = new byte[64];

    MD5State() {
    }

    /**
     * Create this State as a copy of another state
     */
    MD5State(MD5State from) {

        state[0] = from.state[0];
        state[1] = from.state[1];
        state[2] = from.state[2];
        state[3] = from.state[3];

        System.arraycopy(from.buffer, 0, buffer, 0, buffer.length);

        count = from.count;
    }
}
