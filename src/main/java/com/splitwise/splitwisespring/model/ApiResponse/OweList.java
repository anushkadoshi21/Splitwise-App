package com.splitwise.splitwisespring.model.ApiResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic model for who owes what and by whom
 */
@Data
@NoArgsConstructor
public class OweList {
    Long OweId;
    Long OwedId;
    float TotalOwed;

    public OweList(Long OweId, Long OwedId, float TotalOwed) {
        this.OweId = OweId;
        this.OwedId = OwedId;
        this.TotalOwed = TotalOwed;
    }
}
