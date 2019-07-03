package burst.kit.entity.response;

import burst.kit.entity.BurstAddress;

@SuppressWarnings("unused")
public final class AtIDsResponse extends BRSResponse {
    private BurstAddress[] atIds;

    private AtIDsResponse() {}

    public BurstAddress[] getAtIds() {
        return atIds;
    }
}
