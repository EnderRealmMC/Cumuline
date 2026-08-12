package cn.enderrealm.cumuline.internal.codec;

/**
 * A Bedrock packet ID and payload ready for Floodgate's unsafe API.
 *
 * @param packetId packet ID without transport flags
 * @param payload payload without the packet ID
 */
public record EncodedPacket(int packetId, byte[] payload) {
}
