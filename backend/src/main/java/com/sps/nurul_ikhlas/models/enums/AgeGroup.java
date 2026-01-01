package com.sps.nurul_ikhlas.models.enums;

import lombok.Getter;

@Getter
public enum AgeGroup {
    TWO_TO_THREE("2-3 Tahun"),
    THREE_TO_FOUR("3-4 Tahun"),
    FOUR_TO_FIVE("4-5 Tahun"),
    FIVE_TO_SIX("5-6 Tahun");

    private final String label;

    AgeGroup(String label) {
        this.label = label;
    }
}
