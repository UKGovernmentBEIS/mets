package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.Getter;

@Getter
public enum BDRS2Violation {

    INVALID_FREE_ALLOCATION_SECTION("Free allocation opinion is required"),
    INVALID_COVID_ADJUSTMENTS_SECTION("COVID adjustments review data is invalid based on guard question selection"),
    INVALID_INSTALLATION_SECTOR_SECTION("Installation sector review data is invalid based on guard question selection"),
    INVALID_CBAM_SPLIT_SECTION("CBAM split review data is invalid based on guard question selection"),
    INVALID_FILE_SECTION("Stage 2 baseline data report file is invalid based on guard question selection");

    private final String message;

    BDRS2Violation(String message) {this.message = message;}
}
