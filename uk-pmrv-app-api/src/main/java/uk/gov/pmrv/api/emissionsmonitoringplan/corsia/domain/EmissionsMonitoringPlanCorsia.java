package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockonblockoff.EmpBlockOnBlockOffMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.fueluplift.EmpFuelUpliftMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.datagaps.EmpDataGapsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methoda.EmpMethodAProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures.EmpManagementProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsMonitoringPlanCorsia {

    @Valid
    @NotNull
    private EmpAbbreviations abbreviations;

    @Valid
    @NotNull
    private EmpAdditionalDocuments additionalDocuments;
    
    @Valid
    @NotNull
    private EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures;
    
    @Valid
    @NotNull
    private EmpEmissionSourcesCorsia emissionSources;

    @Valid
    @NotNull
    private EmpEmissionsMonitoringApproachCorsia emissionsMonitoringApproach;

    @Valid
    @NotNull
    private EmpManagementProceduresCorsia managementProcedures;
    
    @Valid
    @NotNull
    private EmpCorsiaOperatorDetails operatorDetails;

    @Valid
    private EmpMethodAProcedures methodAProcedures;

    @Valid
    private EmpMethodBProcedures methodBProcedures;

    @Valid
    private EmpBlockOnBlockOffMethodProcedures blockOnBlockOffMethodProcedures;

    @Valid
    private EmpFuelUpliftMethodProcedures fuelUpliftMethodProcedures;
    
    @Valid
    @NotNull
    private EmpDataGapsCorsia dataGaps;

    @Valid
    private EmpBlockHourMethodProcedures blockHourMethodProcedures;

    @JsonIgnore
    public Set<UUID> getEmpSectionAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        if(additionalDocuments != null && !ObjectUtils.isEmpty(additionalDocuments.getDocuments())) {
            attachments.addAll(additionalDocuments.getDocuments());
        }
        if(operatorDetails != null && !ObjectUtils.isEmpty(operatorDetails.getAttachmentIds())) {
            attachments.addAll(operatorDetails.getAttachmentIds());
        }
        if(emissionsMonitoringApproach != null && !ObjectUtils.isEmpty(emissionsMonitoringApproach.getAttachmentIds())) {
            attachments.addAll(emissionsMonitoringApproach.getAttachmentIds());
        }
        if(managementProcedures != null && !ObjectUtils.isEmpty(managementProcedures.getAttachmentIds())) {
            attachments.addAll(managementProcedures.getAttachmentIds());
        }
        return Collections.unmodifiableSet(attachments);
    }

    @JsonIgnore
    public Set<EmpCorsiaDynamicSection> getNotEmptyDynamicSections() {
        Set<EmpCorsiaDynamicSection> dynamicSections = new HashSet<>();
        if(!ObjectUtils.isEmpty(methodAProcedures)) {
            dynamicSections.add(EmpCorsiaDynamicSection.METHOD_A_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(methodBProcedures)) {
            dynamicSections.add(EmpCorsiaDynamicSection.METHOD_B_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(blockOnBlockOffMethodProcedures)) {
            dynamicSections.add(EmpCorsiaDynamicSection.BLOCK_ON_OFF_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(fuelUpliftMethodProcedures)) {
            dynamicSections.add(EmpCorsiaDynamicSection.FUEL_UPLIFT_PROCEDURES);
        }

        if(!ObjectUtils.isEmpty(blockHourMethodProcedures)) {
            dynamicSections.add(EmpCorsiaDynamicSection.BLOCK_HOUR_PROCEDURES);
        }
        return dynamicSections;
    }
}
