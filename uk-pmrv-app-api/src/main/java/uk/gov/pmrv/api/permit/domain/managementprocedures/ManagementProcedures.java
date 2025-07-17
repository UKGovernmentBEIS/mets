package uk.gov.pmrv.api.permit.domain.managementprocedures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagementProcedures implements PermitSection {

    @Valid
    @NotNull(message = "permit.managementProcedures.monitoringReporting")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private MonitoringReporting monitoringReporting;

    @Valid
    @NotNull(message = "permit.managementProcedures.assignmentOfResponsibilities")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition assignmentOfResponsibilities;

    @Valid
    @NotNull(message = "permit.managementProcedures.monitoringPlanAppropriateness")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition monitoringPlanAppropriateness;

    @Valid
    @NotNull(message = "permit.managementProcedures.dataFlowActivities")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private DataFlowActivities dataFlowActivities;

    @Valid
    @NotNull(message = "permit.managementProcedures.qaDataFlowActivities")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition qaDataFlowActivities;

    @Valid
    @NotNull(message = "permit.managementProcedures.reviewAndValidationOfData")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition reviewAndValidationOfData;

    @Valid
    @NotNull(message = "permit.managementProcedures.assessAndControlRisk")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private AssessAndControlRisk assessAndControlRisk;

    @Valid
    @NotNull(message = "permit.managementProcedures.qaMeteringAndMeasuringEquipment")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition qaMeteringAndMeasuringEquipment;

    @Valid
    @NotNull(message = "permit.managementProcedures.correctionsAndCorrectiveActions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition correctionsAndCorrectiveActions;

    @Valid
    @NotNull(message = "permit.managementProcedures.controlOfOutsourcedActivities")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition controlOfOutsourcedActivities;

    @Valid
    @NotNull(message = "permit.managementProcedures.recordKeepingAndDocumentation")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ManagementProceduresDefinition recordKeepingAndDocumentation;

    @Valid
    @NotNull(message = "permit.managementProcedures.environmentalManagementSystem")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EnvironmentalManagementSystem environmentalManagementSystem;

    @JsonIgnore
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        if (dataFlowActivities != null && dataFlowActivities.getDiagramAttachmentId() != null) {
            attachments.add(dataFlowActivities.getDiagramAttachmentId());
        }

        if (monitoringReporting != null && !ObjectUtils.isEmpty(monitoringReporting.getOrganisationCharts())) {
            attachments.addAll(monitoringReporting.getOrganisationCharts());
        }

        if (assessAndControlRisk != null && !ObjectUtils.isEmpty(assessAndControlRisk.getRiskAssessmentAttachments())) {
            attachments.addAll(assessAndControlRisk.getRiskAssessmentAttachments());
        }

        return Collections.unmodifiableSet(attachments);
    }
}
