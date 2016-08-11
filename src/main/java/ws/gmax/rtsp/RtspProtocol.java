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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements RTSP commands
 *
 * @author Marius
 */
class RtspProtocol extends Rtsp {

    /* OK code */
    static final int RTSP_OK = 200;

    /* Unauthorized code */
    static final int RTSP_UNAUTHORIZED = 401;

    /* SDP */
    private final Sdp sdp;

    /* Stream name */
    private String stream;

    /* Session ID */
    private String session;

    /* Sequence generator */
    private RtspSequenceGenerator seq;

    RtspProtocol() {
        sdp = new Sdp();
        seq = new RtspSequenceGenerator();
    }

    /**
     * Connect to RTSP server using standard port
     *
     * @param ip     Server address
     * @param stream Stream name
     * @throws Exception on error
     */
    public void connect(String ip, String stream) throws Exception {
        this.stream = stream;
        connect(ip);
    }

    /**
     * Connect to RTSP server using specific port
     *
     * @param ip     Server address
     * @param port   Server port
     * @param stream Stream name
     * @throws Exception on error
     */
    void connect(String ip, int port, String stream) throws Exception {
        this.stream = stream;
        connect(ip, port);
    }

    /**
     * DESCRIBE (C->S)
     * <p>
     * A DESCRIBE request includes an RTSP URL (rtsp://...), and the type of
     * reply data that can be handled. This reply includes the presentation
     * description, typically in Session Description Protocol (SDP) format.
     * Among other things, the presentation description lists the media streams
     * controlled with the aggregate URL. In the typical case, there is one
     * media stream each for audio and video.
     *
     * @param token Authentication token
     * @return Response code
     * @throws Exception on error
     */
    int describe(String token) throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("DESCRIBE ").
                append(rtspUri).
                append(stream);
        if (token == null) {
            hdr.put("CSeq", seq.next());
        } else {
            hdr.put("CSeq", seq.value());
            hdr.put("Authorization", token);
        }
        getReqest().doRequest(req, hdr);
        int code = getResponse().doResponse();
        if (RTSP_OK == code) {
            sdp.setSdp(getResponse().response.body);
        }
        return code;
    }

    /**
     * OPTIONS (C->S)
     * <p>
     * An OPTIONS request returns the request types the server will accept.
     *
     * @return Response code
     * @throws Exception on error
     */
    int options() throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("OPTIONS ").append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    /**
     * SETUP (C->S)
     * <p>
     * A SETUP request specifies how a single media stream must be transported.
     * This must be done before a PLAY request is sent. The request contains the
     * media stream URL and a transport specifier. This specifier typically
     * includes a local port for receiving RTP data (audio or video), and
     * another for RTCP data (meta information). The server reply usually
     * confirms the chosen parameters, and fills in the missing parts, such as
     * the server's chosen ports. Each media stream must be configured using
     * SETUP before an aggregate play request may be sent.
     *
     * @param control Stream control
     * @param port    Client port
     * @return Response code
     * @throws Exception on error
     */
    int setup(String control, int port) throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("SETUP ").append(rtspUri).
                append(stream).
                append(control);
        hdr.put("CSeq", seq.next());
        hdr.put("Transport", String.format("RTP/AVP;unicast;client_port=%d-%d",
                port, port + 1));
        getReqest().doRequest(req, hdr);
        int code = getResponse().doResponse();
        if (RTSP_OK == code) {
            session = getResponse().getSession();
        }
        return code;
    }

    /**
     * PLAY (C->S)
     * <p>
     * A PLAY request will cause one or all media streams to be played. Play
     * requests can be stacked by sending multiple PLAY requests. The URL may be
     * the aggregate URL (to play all media streams), or a single media stream
     * URL (to play only that stream). A range can be specified. If no range is
     * specified, the stream is played from the beginning and plays to the end,
     * or, if the stream is paused, it is resumed at the point it was paused.
     *
     * @return Response code
     * @throws Exception on error
     */
    int play() throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("PLAY ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    /**
     * PAUSE (C->S)
     * <p>
     * A PAUSE request temporarily halts one or all media streams, so it can
     * later be resumed with a PLAY request. The request contains an aggregate
     * or media stream URL. A range parameter on a PAUSE request specifies when
     * to pause. When the range parameter is omitted, the pause occurs
     * immediately and indefinitely.
     *
     * @return Response code
     * @throws Exception on error
     */
    int pause() throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("PAUSE ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    /**
     * TEARDOWN (C->S)
     * <p>
     * A TEARDOWN request is used to terminate the session. It stops all media
     * streams and frees all session related data on the server.
     *
     * @return Response code
     * @throws Exception on error
     */
    int teardown() throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("TEARDOWN ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    /**
     * SET_PARAMETER (C->S)
     * <p>
     * This method requests to set the value of a parameter for a presentation
     * or stream specified by the URI.
     *
     * @param paramName  Parameter name
     * @param paramValue Parameter value
     * @return Response code
     * @throws Exception on error
     */
    public int setParameter(String paramName, String paramValue) throws Exception {
        String body = String.format("%s: %s\r\n", paramName, paramValue);
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("SET_PARAMETER ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Content-Type", "text/parameters");
        hdr.put("Content-Length", String.valueOf(body.length()));
        getReqest().doRequest(req, hdr, body);
        return getResponse().doResponse();
    }

    /**
     * GET_PARAMETER (S->C)
     * <p>
     * The GET_PARAMETER request retrieves the value of a parameter of a
     * presentation or stream specified in the URI. The content of the reply and
     * response is left to the implementation. GET_PARAMETER with no entity body
     * may be used to test client or server liveness ("ping").
     *
     * @param paramName Parameter name
     * @return Response code
     * @throws Exception on error
     */
    public int getParameter(String paramName) throws Exception {
        String body = String.format("%s\r\n", paramName);
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("SET_PARAMETER ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        hdr.put("Content-Type", "text/parameters");
        hdr.put("Content-Length", String.valueOf(body.length()));
        getReqest().doRequest(req, hdr, body);
        return getResponse().doResponse();
    }

    /**
     * ANNOUNCE (C->S)
     * <p>
     * The ANNOUNCE method serves two purposes: When sent from client to server,
     * ANNOUNCE posts the description of a presentation or media object
     * identified by the request URL to a server. When sent from server to
     * client, ANNOUNCE updates the session description in real-time. If a new
     * media stream is added to a presentation (e.g., during a live
     * presentation), the whole presentation description should be sent again,
     * rather than just the additional components, so that components can be
     * deleted.
     *
     * @param sdp SDP document
     * @return Response code
     * @throws Exception on error
     */
    public int announce(String sdp) throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("ANNOUNCE ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        hdr.put("Content-Type", "application/sdp");
        hdr.put("Content-Length", String.valueOf(sdp.length()));
        getReqest().doRequest(req, hdr, sdp);
        return getResponse().doResponse();
    }

    /**
     * RECORD (C->S)
     * <p>
     * This method initiates recording a range of media data according to the
     * presentation description. The time stamp reflects start and end
     * time(UTC). If no time range is given, use the start or end time provided
     * in the presentation description. If the session has already started,
     * commence recording immediately. The server decides whether to store the
     * recorded data under the request URI or another URI. If the server does
     * not use the request URI, the response should be 201 and contain an entity
     * which describes the states of the request and refers to the new resource,
     * and a Location header.
     *
     * @return Response code
     * @throws Exception on error
     */
    public int record() throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("RECORD ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Session", session);
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    /**
     * REDIRECT (S->C)
     * <p>
     * A REDIRECT request informs the client that it must connect to another
     * server location. It contains the mandatory header Location, which
     * indicates that the client should issue requests for that URL. It may
     * contain the parameter Range, which indicates when the redirection takes
     * effect. If the client wants to continue to send or receive media for this
     * URI, the client MUST issue a TEARDOWN request for the current session and
     * a SETUP for the new session at the designated host.
     *
     * @param redirectUri New server URI
     * @param range       UTC timestamp indicates when the redirection takes effect.
     *                    (19960213T143205Z-)
     * @return Response code
     * @throws Exception on error
     */
    public int redirect(String redirectUri, String range) throws Exception {
        StringBuilder req = new StringBuilder();
        Map<String, String> hdr = new LinkedHashMap<>();
        req.append("REDIRECT ").
                append(rtspUri).
                append(stream);
        hdr.put("CSeq", seq.next());
        hdr.put("Location", redirectUri);
        if (range != null) {
            hdr.put("Range", "clock=" + range);
        }
        getReqest().doRequest(req, hdr);
        return getResponse().doResponse();
    }

    Sdp getSdp() {
        return sdp;
    }
}
