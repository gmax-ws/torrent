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
 * Sdp document storage
 *
 * @author Marius
 *         <p>
 *         RTSP/1.0 200 OK
 *         CSeq: 1
 *         Date: Thu, Apr 22 2010 20:38:14 GMT
 *         Content-Base: rtsp://93.89.112.125/channel1/
 *         Content-Type: application/sdp
 *         Content-Length: 357
 *         <p>
 *         v=0
 *         o=- 108060945163 1 IN IP4 93.89.112.125
 *         s=Session streamed by stream
 *         i=1
 *         t=0 0
 *         a=tool:LIVE555 Streaming Media v2009.01.26
 *         a=type:broadcast
 *         a=control:*
 *         a=range:npt=0-
 *         a=x-qt-text-nam:Session streamed by stream
 *         a=x-qt-text-inf:1
 *         m=video 0 RTP/AVP 26
 *         c=IN IP4 0.0.0.0
 *         a=control:track1
 *         m=audio 0 RTP/AVP 0
 *         c=IN IP4 0.0.0.0
 *         a=control:track2
 */
class Sdp {

    /* Stream control pattern */
    private static final String CONTROL = "a=control:";

    /* SDP document text */
    private String sdp;

    Sdp() {
    }

    String getSdp() {
        return sdp;
    }

    void setSdp(String sdp) {
        this.sdp = sdp;
    }

    /**
     * Check SDP if has video stream
     *
     * @return <code>true</code> if has video stream <code>false</code> if not
     */
    boolean hasVideo() {
        return (sdp != null && sdp.contains("m=video "));
    }

    /**
     * Check SDP if has audio stream
     *
     * @return <code>true</code> if has audio stream <code>false</code> if not
     */
    boolean hasAudio() {
        return (sdp != null && sdp.contains("m=audio "));
    }

    String getVideoTrack() {
        return getTrack("m=video ");
    }

    String getAudioTrack() {
        return getTrack("m=audio ");
    }

    /**
     * Get stream control ID
     *
     * @param stream Stream start (m=video or m=audio)
     * @return Stream control
     */
    private String getTrack(String stream) {
        if (sdp == null) {
            return null;
        }

        int beg = sdp.indexOf(stream);
        if (beg == -1) {
            return null;
        }

        beg = sdp.indexOf(CONTROL, beg);
        if (beg == -1) {
            return null;
        }
        beg += CONTROL.length();

        int end = sdp.indexOf("\r\n", beg);
        if (end == -1) {
            return null;
        }

        return sdp.substring(beg, end);
    }
}
