package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpManagementProcedures implements EmpUkEtsSection {

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpMonitoringReportingRoles monitoringReportingRoles;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm recordKeepingAndDocumentation;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm assignmentOfResponsibilities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm monitoringPlanAppropriateness;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm qaMeteringAndMeasuringEquipment;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm dataValidation;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm correctionsAndCorrectiveActions;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm controlOfOutsourcedActivities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm assessAndControlRisks;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpDataFlowActivities dataFlowActivities;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpEnvironmentalManagementSystem environmentalManagementSystem;

    private UUID riskAssessmentFile;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureForm upliftQuantityCrossChecks;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();
        if(dataFlowActivities != null && dataFlowActivities.getDiagramAttachmentId() != null) {
            attachments.add(dataFlowActivities.getDiagramAttachmentId());
        }
        if (riskAssessmentFile != null) {
            attachments.add(riskAssessmentFile);
        }
        return Collections.unmodifiableSet(attachments);
    }
}
