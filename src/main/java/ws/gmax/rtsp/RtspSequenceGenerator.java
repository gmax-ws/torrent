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

/**
 * RTSP CSeq generator.
 * The generated values are returned as string.
 *
 * @author Marius
 */
class RtspSequenceGenerator {

    /* Initial value */
    private int sequence = 1;

    /**
     * Get current value and generate next value.
     *
     * @return Current value
     */
    String next() {
        return String.valueOf(sequence++);
    }

    /**
     * Get current value.
     *
     * @return Current value
     */
    String value() {
        return String.valueOf(sequence);
    }
}
