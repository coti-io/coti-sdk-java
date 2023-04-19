package io.coti.sdk.http;

import io.coti.basenode.data.Hash;
import io.coti.basenode.data.HealthState;
import lombok.Data;

@Data
public class NotifyNodeHealthStateChange {

    private Hash nodeHash;
    private HealthState reportedHealthState;

    public NotifyNodeHealthStateChange() {
    }

    public NotifyNodeHealthStateChange(Hash nodeHash, HealthState reportedHealthState) {
        this.nodeHash = nodeHash;
        this.reportedHealthState = reportedHealthState;
    }
}