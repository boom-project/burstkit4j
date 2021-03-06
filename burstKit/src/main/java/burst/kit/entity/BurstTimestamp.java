package burst.kit.entity;

import burst.kit.burst.BurstCrypto;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public final class BurstTimestamp {

    public static final JsonDeserializer<BurstTimestamp> DESERIALIZER = (json, typeOfT, context) -> new BurstTimestamp(json.getAsInt());
    public static final JsonSerializer<BurstTimestamp> SERIALIZER = (src, typeOfSrc, context) -> new JsonPrimitive(src.getTimestamp());

    private final int timestamp;
    private final Date date;

    /**
     * @param timestamp The burst timestamp (number of seconds since Burst epoch)
     */
    public BurstTimestamp(int timestamp) {
        this.timestamp = timestamp;
        this.date = BurstCrypto.getInstance().fromEpochTime(timestamp);
    }

    /**
     * @return The burst timestamp (number of seconds since Burst epoch)
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return The timestamp as a pre Java 8 Date object, mainly intended for Android usage as older versions of Android do not have the Java 8 Time API
     */
    public Date getAsDate() {
        return date;
    }

    /**
     * @return The timestamp as a Java 8 Instant
     */
    public Instant getAsInstant() {
        return date.toInstant();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BurstTimestamp && Objects.equals(timestamp, ((BurstTimestamp)obj).timestamp);
    }

    @Override
    public String toString() {
        return String.valueOf(getTimestamp());
    }
}
