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

import ws.gmax.rtsp.RtspSession;

//    Jul 24, 2016 10:10:58 PM ws.gmax.rtsp.RtspRequest doRequest
//    INFO: DESCRIBE rtsp://cuesco.ipcctvdns.com:554/cam0_1 RTSP/1.0
//    CSeq: 1
//
//
//    Jul 24, 2016 10:10:58 PM ws.gmax.rtsp.RtspResponse doResponse
//    INFO: RTSP/1.0 200 OK
//    CSeq: 1
//    Date: Sun, Jul 24 2016 19:11:57 GMT
//    Content-Base: rtsp://172.30.1.60/cam0_1/
//    Content-Type: application/sdp
//    Content-Length: 453
//
//    v=0
//    o=- 1468039952094900 1 IN IP4 172.30.1.60
//    s=RTSP/RTP stream from a VMFD encoder
//    i=cam0_1
//    t=0 0
//    a=tool:LIVE555 Streaming Media v2008.02.08
//    a=type:broadcast
//    a=control:*
//    a=range:npt=0-
//    a=x-qt-text-nam:RTSP/RTP stream from a VMFD encoder
//    a=x-qt-text-inf:cam0_1
//    m=video 0 RTP/AVP 96
//    c=IN IP4 0.0.0.0
//    a=rtpmap:96 H264/90000
//    a=fmtp:96 packetization-mode=1;profile-level-id=428028;sprop-parameter-sets=Z0KAKNoCwPRA,aM48gA==
//    a=control:track1
//
//    Jul 24, 2016 10:10:58 PM ws.gmax.rtsp.RtspRequest doRequest
//    INFO: SETUP rtsp://cuesco.ipcctvdns.com:554/cam0_1/track1  RTSP/1.0
//    CSeq: 2
//    Transport: RTP/AVP;unicast;client_port=6970-6971
//
//
//    Jul 24, 2016 10:10:59 PM ws.gmax.rtsp.RtspResponse doResponse
//    INFO: RTSP/1.0 200 OK
//    CSeq: 2
//    Date: Sun, Jul 24 2016 19:11:57 GMT
//    Transport: RTP/AVP;unicast;destination=81.181.140.217;source=172.30.1.60;client_port=6970-6971;server_port=6970-6971
//    Session: 359
//
//
//    Jul 24, 2016 10:10:59 PM ws.gmax.rtsp.RtspRequest doRequest
//    INFO: PLAY rtsp://cuesco.ipcctvdns.com:554/cam0_1 RTSP/1.0
//    CSeq: 3
//    Session: Session: 359
//
//
//    Jul 24, 2016 10:10:59 PM ws.gmax.rtsp.RtspResponse doResponse
//    INFO: RTSP/1.0 200 OK
//    CSeq: 3
//    Date: Sun, Jul 24 2016 19:11:57 GMT
//    Session: 359
//    RTP-Info: url=rtsp://172.30.1.60/cam0_1/track1;seq=28169;rtptime=981552495

/**
 * RTSP/RTP sample
 *
 * @author Marius
 */
public class Test {

    public static void main(String[] args) throws Exception {
        RtpVideoPlayer videoPlayer = new RtpVideoPlayer("0.0.0.0", 9000);
        RtpAudioPlayer audioPlayer = new RtpAudioPlayer("0.0.0.0", 9002);
        RtspSession session = new RtspSession(videoPlayer, audioPlayer);
        session.setUsername("admin");
        session.setPassword("admin");
        session.play("rtsp://cuesco.ipcctvdns.com/cam0_1");
        Thread.sleep(5000);
        session.stop();
    }
}
