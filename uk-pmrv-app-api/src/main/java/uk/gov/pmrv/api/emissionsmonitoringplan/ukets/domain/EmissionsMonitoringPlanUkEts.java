package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.applicationtimeframe.EmpApplicationTimeframeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim.EmpEmissionsReductionClaim;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsMonitoringPlanUkEts {

    @Valid
    @NotNull
    private EmpAbbreviations abbreviations;

    @Valid
    @NotNull
    private EmpAdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private EmpApplicationTimeframeInfo applicationTimeframeInfo;

    @Valid
    @NotNull
    private EmpFlightAndAircraftProcedures flightAndAircraftProcedures;

    @Valid
    @NotNull
    private EmpEmissionSources emissionSources;

    @Valid
    @NotNull
    private EmpEmissionsReductionClaim emissionsReductionClaim;

    @Valid
    @NotNull
    private EmpEmissionsMonitoringApproach emissionsMonitoringApproach;

    @Valid
    @NotNull
    private EmpManagementProcedures managementProcedures;

    @Valid
    @NotNull
    private EmpOperatorDetails operatorDetails;

    @Valid
    private EmpMethodAProcedures methodAProcedures;

    @Valid
    private EmpMethodBProcedures methodBProcedures;

    @Valid
    private EmpBlockOnBlockOffMethodProcedures blockOnBlockOffMethodProcedures;

    @Valid
    private EmpFuelUpliftMethodProcedures fuelUpliftMethodProcedures;

    @Valid
    private EmpDataGaps dataGaps;

    @Valid
    private EmpBlockHourMethodProcedures blockHourMethodProcedures;

    @JsonIgnore
    public Set<UUID> getEmpSectionAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        if (additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }

        if (emissionsMonitoringApproach != null) {
            attachments.addAll(emissionsMonitoringApproach.getAttachmentIds());
        }

        if(managementProcedures != null) {
            attachments.addAll(managementProcedures.getAttachmentIds());
        }

        if (operatorDetails != null) {
            attachments.addAll(operatorDetails.getAttachmentIds());
        }

        return Collections.unmodifiableSet(attachments);
    }

    @JsonIgnore
    public Set<EmpUkEtsDynamicSection> getNotEmptyDynamicSections() {
        Set<EmpUkEtsDynamicSection> dynamicSections = new HashSet<>();
        if(!ObjectUtils.isEmpty(methodAProcedures)) {
            dynamicSections.add(EmpUkEtsDynamicSection.METHOD_A_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(methodBProcedures)) {
            dynamicSections.add(EmpUkEtsDynamicSection.METHOD_B_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(blockOnBlockOffMethodProcedures)) {
            dynamicSections.add(EmpUkEtsDynamicSection.BLOCK_ON_OFF_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(fuelUpliftMethodProcedures)) {
            dynamicSections.add(EmpUkEtsDynamicSection.FUEL_UPLIFT_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(dataGaps)) {
            dynamicSections.add(EmpUkEtsDynamicSection.DATA_GAPS);
        }

        if(!ObjectUtils.isEmpty(blockHourMethodProcedures)) {
            dynamicSections.add(EmpUkEtsDynamicSection.BLOCK_HOUR_PROCEDURES);
        }

        return dynamicSections;
    }
}
