package burst.kit.entity;

import burst.kit.burst.BurstCrypto;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class BurstAddress {

    /**
     * GSON Serializer.
     */
    public static final JsonSerializer<BurstAddress> SERIALIZER = (src, typeOfSrc, context) -> new JsonPrimitive(src.getID());

    /**
     * GSON Deserializer
     */
    public static final JsonDeserializer<BurstAddress> DESERIALIZER = (json, typeOfT, context) -> fromEither(json.getAsString());

    /**
     * Stored without "BURST-" prefix.
     */
    private final String address;
    private final BurstID numericID;

    private BurstAddress(BurstID burstID) {
        this.numericID = burstID;
        this.address = BurstCrypto.getInstance().rsEncode(numericID);
    }

    /**
     * @param burstID The numeric id that represents this Burst Address
     * @return A BurstAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static BurstAddress fromId(BurstID burstID) {
        return new BurstAddress(burstID);
    }

    /**
     * @param unsignedLongId The numeric id that represents this Burst Address
     * @return A BurstAddress object that represents the specified numericId
     * @throws NumberFormatException if the numericId is not a valid number
     * @throws IllegalArgumentException if the numericId is outside the range of accepted numbers (less than 0 or greater than / equal to 2^64)
     */
    public static BurstAddress fromId(String unsignedLongId) {
        return new BurstAddress(new BurstID(unsignedLongId));
    }

    public static BurstAddress fromRs(String RS) throws IllegalArgumentException {
        if (RS.startsWith("BOOM-")) {
            RS = RS.substring(5);
        }
        return new BurstAddress(BurstCrypto.getInstance().rsDecode(RS));
    }

    /**
     * Try to parse an input as either a numeric ID or an RS address.
     *
     * @param input the numeric ID or RS address of the Burst address
     * @return a BurstAddress if one could be parsed from the input, null otherwise
     */
    public static BurstAddress fromEither(String input) {
        if (input == null) return null;
        try {
            return BurstAddress.fromId(new BurstID(input));
        } catch (IllegalArgumentException e1) {
            try {
                return BurstAddress.fromRs(input);
            } catch (IllegalArgumentException e2) {
                return null;
            }
        }
    }

    /**
     * @return The BurstID of this address
     */
    public BurstID getBurstID() {
        return numericID;
    }

    /**
     * @return The unsigned long numeric ID this BurstAddress points to
     */
    public String getID() {
        return numericID.getID();
    }

    /**
     * @return The ReedSolomon encoded address, without the "BURST-" prefix
     */
    public String getRawAddress() {
        return address;
    }

    /**
     * @return The ReedSolomon encoded address, with the "BURST-" prefix
     */
    public String getFullAddress() {
        if (address == null || address.length() == 0) {
            return "";
        } else {
            return "BOOM-" + address;
        }
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BurstAddress && Objects.equals(numericID, ((BurstAddress) obj).numericID);
    }

    @Override
    public int hashCode() {
        return numericID.hashCode();
    }
}
