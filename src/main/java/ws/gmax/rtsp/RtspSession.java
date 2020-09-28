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

import ws.gmax.rtp.RtpPlayer;

/**
 * RtspSession
 *
 * @author Marius
 */
public class RtspSession extends RtspProtocol {

    /* Video player */
    private final RtpPlayer videoPlayer;

    /* Audio player */
    private final RtpPlayer audioPlayer;

    /* Server name or address */
    private String server;

    /* Username and password if authentication is required */
    private String username, password;

    /**
     * Build a RTSP session.
     *
     * @param videoPlayer video player
     * @param audioPlayer audio player
     */
    public RtspSession(RtpPlayer videoPlayer, RtpPlayer audioPlayer) {
        this.videoPlayer = videoPlayer;
        this.audioPlayer = audioPlayer;
    }

    /**
     * If have video track start video player
     *
     * @throws Exception on error
     */
    private void startVideoPlayer() throws Exception {
        int code = setup("/" + getSdp().getVideoTrack() + " ", videoPlayer.port);
        if (RTSP_OK == code) {
            videoPlayer.start();
        }
    }

    /**
     * If have audio track start video player
     *
     * @throws Exception on error
     */
    private void startAudioPlayer() throws Exception {
        int code = setup("/" + getSdp().getAudioTrack() + " ", audioPlayer.port);
        if (RTSP_OK == code) {
            audioPlayer.start();
        }
    }

    /**
     * Open session
     *
     * @return RTSP response code.
     * @throws Exception on error
     */
    private int open() throws Exception {
        int code = describe(null);
        if (RTSP_UNAUTHORIZED == code) {
            String auth = RtspAuth.auth_token(getResponse().response.headers,
                    username, password, rtspUri, "DESCRIBE");
            code = (auth == null) ? -1 : describe(auth);
        }
        return code;
    }

    /**
     * Start playing
     *
     * @return <code>true</code> on success <code>false</code> otherwise.
     * @throws Exception on error
     */
    private boolean playRtp() throws Exception {
        options();
        if (RTSP_OK == open()) {
            if (getSdp().hasVideo()) {
                startVideoPlayer();
            }
            if (getSdp().hasAudio()) {
                startAudioPlayer();
            }
            return RTSP_OK == play();
        }
        return false;
    }

    /**
     * Play
     *
     * @param server name or address
     * @param port   port number
     * @param stream stream id
     * @return <code>true</code> on success <code>false</code> otherwise.
     * @throws Exception on error
     */
    private boolean play(String server, int port, String stream)
            throws Exception {
        this.server = server;
        connect(server, port, stream);
        return playRtp();
    }

    /**
     * Play. Use default RTSP port number
     *
     * @param server name or address
     * @param stream stream id
     * @return <code>true</code> on success <code>false</code> otherwise.
     * @throws Exception on error
     */
    public boolean play(String server, String stream) throws Exception {
        return play(server, DEFAULT_RTSP_PORT, stream);
    }

    /**
     * Play. Provide a URI string ad argument.
     *
     * @param uri URI string
     * @return <code>true</code> on success <code>false</code> otherwise.
     * @throws Exception on error
     */
    public boolean play(String uri) throws Exception {
        RtspURI url = new RtspURI().split(uri);
        if (url.port == -1) {
            return play(url.host, DEFAULT_RTSP_PORT, url.path);
        } else {
            return play(url.host, url.port, url.path);
        }
    }

    /**
     * Stop playing and close session.
     *
     * @throws Exception on error
     */
    public void stop() throws Exception {
        // RTP
        if (videoPlayer != null) {
            videoPlayer.stop();
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        // RTSP
        teardown();
        disconnect();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
