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
package ws.gmax.rtp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * RTP Decoder.
 *
 * @author Marius
 */
public class RtpDecoder {

    // The RTP header has the following format:
    //
    //    0                   1                   2                   3
    //    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    //   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    //   |V=2|P|X|  CC   |M|     PT      |       sequence number         |
    //   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    //   |                           timestamp                           |
    //   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    //   |           synchronization source (SSRC) identifier            |
    //   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
    //   |            contributing source (CSRC) identifiers             |
    //   |                             ....                              |
    //   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    //
    //   The first twelve bytes are present in every RTP packet, while the
    //   list of CSRC identifiers is present only when inserted by a mixer.
    //   The fields have the following meaning:

    /* RTP standard header size */
    private static final int RTP_HEADER_SIZE = 12;

    /**
     * version (V): 2 bits This field identifies the version of RTP. The version
     * defined by this specification is two (2). (The value 1 is used by the
     * first draft version of RTP and the value 0 is used by the protocol
     * initially implemented in the "vat" audio tool.)
     */
    private int V = 2;

    /**
     * padding (P): 1 bit If the padding bit is set, the packet contains one or
     * more additional padding bytes at the end which are not part of the
     * payload. The last bytes of the padding contains a count of how many
     * padding bytes should be ignored, including itself. Padding may be needed
     * by some encryption algorithms with fixed block sizes or for carrying
     * several RTP packets in a lower-layer protocol data unit.
     */
    private boolean P;

    /**
     * extension (X): 1 bit If the extension bit is set, the fixed header MUST
     * be followed by exactly one header extension, with a format defined in
     * Section 5.3.1.
     */
    private boolean X;

    /**
     * CSRC count (CC): 4 bits The CSRC count contains the number of CSRC
     * identifiers that follow the fixed header.
     */
    private int CC;

    /**
     * marker (M): 1 bit The interpretation of the marker is defined by a
     * profile. It is intended to allow significant events such as frame
     * boundaries to be marked in the packet stream. A profile MAY define
     * additional marker bits or specify that there is no marker bit by changing
     * the number of bits in the payload type field (see Section 5.3).
     */
    private boolean M;

    /**
     * payload type (PT): 7 bits This field identifies the format of the RTP
     * payload and determines its interpretation by the application. A profile
     * MAY specify a default static mapping of payload type codes to payload
     * formats. Additional payload type codes MAY be defined dynamically through
     * non-RTP means (see Section 3). A set of default mappings for audio and
     * video is specified in the companion RFC 3551 [1]. An RTP source MAY
     * change the payload type during a session, but this field SHOULD NOT be
     * used for multiplexing separate media streams (see Section 5.2).
     * <p>
     * A receiver MUST ignore packets with payload types that it does not
     * understand.
     */
    private int PT;

    /**
     * sequence number: 16 bits The sequence number increments by one for each
     * RTP data packet sent, and may be used by the receiver to detect packet
     * loss and to restore packet sequence. The initial value of the sequence
     * number SHOULD be random (unpredictable) to make known-plaintext attacks
     * on encryption more difficult, even if the source itself does not encrypt
     * according to the method in Section 9.1, because the packets may flow
     * through a translator that does. Techniques for choosing unpredictable
     * numbers are discussed in [17].
     */
    private int SEQN;

    /**
     * timestamp: 32 bits The timestamp reflects the sampling instant of the
     * first octet in the RTP data packet. The sampling instant MUST be derived
     * from a clock that increments monotonically and linearly in time to allow
     * synchronization and jitter calculations (see Section 6.4.1). The
     * resolution of the clock MUST be sufficient for the desired
     * synchronization accuracy and for measuring packet arrival jitter (one
     * tick per video frame is typically not sufficient). The clock frequency is
     * dependent on the format of data carried as payload and is specified
     * statically in the profile or payload format specification that defines
     * the format, or MAY be specified dynamically for payload formats defined
     * through non-RTP means. If RTP packets are generated periodically, the
     * nominal sampling instant as determined from the sampling clock is to be
     * used, not a reading of the system clock. As an example, for fixed-rate
     * audio the timestamp clock would likely increment by one for each sampling
     * period. If an audio application reads blocks covering 160 sampling
     * periods from the input device, the timestamp would be increased by 160
     * for each such block, regardless of whether the block is transmitted in a
     * packet or dropped as silent.
     * <p>
     * The initial value of the timestamp SHOULD be random, as for the sequence
     * number. Several consecutive RTP packets will have equal timestamps if
     * they are (logically) generated at once, e.g., belong to the same video
     * frame. Consecutive RTP packets MAY contain timestamps that are not
     * monotonic if the data is not transmitted in the order it was sampled, as
     * in the case of MPEG interpolated video frames. (The sequence numbers of
     * the packets as transmitted will still be monotonic.)
     * <p>
     * RTP timestamps from different media streams may advance at different
     * rates and usually have independent, random offsets. Therefore, although
     * these timestamps are sufficient to reconstruct the timing of a single
     * stream, directly comparing RTP timestamps from different media is not
     * effective for synchronization. Instead, for each medium the RTP timestamp
     * is related to the sampling instant by pairing it with a timestamp from a
     * reference clock (wallclock) that represents the time when the data
     * corresponding to the RTP timestamp was sampled. The reference clock is
     * shared by all media to be synchronized. The timestamp pairs are not
     * transmitted in every data packet, but at a lower rate in RTCP SR packets
     * as described in Section 6.4.
     * <p>
     * The sampling instant is chosen as the point of reference for the RTP
     * timestamp because it is known to the transmitting endpoint and has a
     * common definition for all media, independent of encoding delays or other
     * processing. The purpose is to allow synchronized presentation of all
     * media sampled at the same time.
     * <p>
     * Applications transmitting stored data rather than data sampled in real
     * time typically use a virtual presentation timeline derived from wallclock
     * time to determine when the next frame or other unit of each medium in the
     * stored data should be presented. In this case, the RTP timestamp would
     * reflect the presentation time for each unit. That is, the RTP timestamp
     * for each unit would be related to the wallclock time at which the unit
     * becomes current on the virtual presentation timeline. Actual presentation
     * occurs some time later as determined by the receiver.
     * <p>
     * An example describing live audio narration of prerecorded video
     * illustrates the significance of choosing the sampling instant as the
     * reference point. In this scenario, the video would be presented locally
     * for the narrator to view and would be simultaneously transmitted using
     * RTP. The "sampling instant" of a video frame transmitted in RTP would be
     * established by referencing its timestamp to the wallclock time when that
     * video frame was presented to the narrator. The sampling instant for the
     * audio RTP packets containing the narrator's speech would be established
     * by referencing the same wallclock time when the audio was sampled. The
     * audio and video may even be transmitted by different hosts if the
     * reference clocks on the two hosts are synchronized by some means such as
     * NTP. A receiver can then synchronize presentation of the audio and video
     * packets by relating their RTP timestamps using the timestamp pairs in
     * RTCP SR packets.
     */
    private int TS;

    /**
     * SSRC: 32 bits The SSRC field identifies the synchronization source. This
     * identifier SHOULD be chosen randomly, with the intent that no two
     * synchronization sources within the same RTP session will have the same
     * SSRC identifier. An example algorithm for generating a random identifier
     * is presented in Appendix A.6. Although the probability of multiple
     * sources choosing the same identifier is low, all RTP implementations must
     * be prepared to detect and resolve collisions. Section 8 describes the
     * probability of collision along with a mechanism for resolving collisions
     * and detecting RTP-level forwarding loops based on the uniqueness of the
     * SSRC identifier. If a source changes its source transport address, it
     * must also choose a new SSRC identifier to avoid being interpreted as a
     * looped source (see Section 8.2).
     */
    private int SSRC;

    /**
     * CSRC list: 0 to 15 items, 32 bits each The CSRC list identifies the
     * contributing sources for the payload contained in this packet. The number
     * of identifiers is given by the CC field. If there are more than 15
     * contributing sources, only 15 can be identified. CSRC identifiers are
     * inserted by mixers (see Section 7.1), using the SSRC identifiers of
     * contributing sources. For example, for audio packets the SSRC identifiers
     * of all sources that were mixed together to create a packet are listed,
     * allowing correct talker indication at the receiver.
     */
    private int[] CSRC;

    /* Payload offset or header size */
    private int offset;

    /* RTP header bytes */
    public byte[] header;

    /* RTP payload bytes */
    public byte[] payload;

    /* Byte order */
    private final ByteOrder order = ByteOrder.LITTLE_ENDIAN;

    /**
     * Decode RTP packet.
     *
     * @param packet RTP packet bytes
     * @param len    RTP packet size
     * @return Decoded RTP packet.
     */
    RtpDecoder decode(byte[] packet, int len) {
        // decode first byte
        V = (packet[0] & 0b11000000) >>> 6;
        P = (packet[0] & 0b00100000) != 0;
        X = (packet[0] & 0b00010000) != 0;
        CC = packet[0] & 0b00001111;
        offset = RTP_HEADER_SIZE + CC * 4;
        // check for header extension
        if (X) {
            int extension = 4 + converter(packet, offset + 2, 2).getShort();
            offset += extension;
        }
        // decode second byte
        M = (packet[1] & 0b10000000) != 0;
        PT = packet[1] & 0b01111111;
        // decode the rest
        SEQN = converter(packet, 2, 2).getShort();
        TS = converter(packet, 4, 4).getInt();
        SSRC = converter(packet, 8, 4).getInt();
        // build the list of CSRC
        if (CC > 0) {
            CSRC = new int[CC];
            for (int i = 0, k = RTP_HEADER_SIZE; i < CC; i++, k += 4) {
                CSRC[i] = converter(packet, k, 4).getInt();
            }
        }
        // check for padding   
        int padding = P ? packet[len - 1] : 0;
        // split packet into header and payload
        header = Arrays.copyOf(packet, offset);
        payload = Arrays.copyOf(packet, len - padding - offset);
        //
        return this;
    }

    /**
     * Bytes converter.
     *
     * @param buffer Bytes array
     * @param off    Bytes offset
     * @return Byte converter.
     */
    private ByteBuffer converter(byte[] buffer, int off, int len) {
        ByteBuffer conv = ByteBuffer.wrap(buffer, off, len);
        conv.order(order);
        return conv;
    }
}
