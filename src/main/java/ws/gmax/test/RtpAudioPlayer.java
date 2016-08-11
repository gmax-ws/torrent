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
package ws.gmax.test;

import ws.gmax.rtp.RtpPlayer;

/**
 * RtpAudioPlayer
 *
 * @author Marius
 */
class RtpAudioPlayer extends RtpPlayer {

    /**
     * Constructor
     *
     * @param host Client host
     * @param port Client port
     */
    RtpAudioPlayer(String host, int port) {
        super(host, port);
    }

    /**
     * Fired when receiving data
     *
     * @param buffer Data bytes
     * @param len    Data length
     */
    @Override
    public void onReceiveData(byte[] buffer, int len) {
        System.out.println("audio: " + len);
    }
}
