package uk.gov.pmrv.api.migration.aviationaccount.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationEmitter {

	private String ca;
    private String scope;
    private String fldEmitterId;
    private String emitterDisplayId;
    private String emitterStatus;
    private String fldName;
    private Long fldNapBenchmarkAllowances;
    private Integer fldRegistration;
    private String fldCrcoCode;
    private LocalDate fldFirstFlyDate;
    private LocalDateTime fldDateCreated;
    private String createdBy;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvinceRegion;
    private String postCodeZip;
    private String country;
    private String reportingStatus;
    private String reportingStatusReason;
    private LocalDateTime reportingStatusSubmissionDate;
    private String reportingStatusSubmittedBy;
    private String vbId;
    private String vbName;
}
