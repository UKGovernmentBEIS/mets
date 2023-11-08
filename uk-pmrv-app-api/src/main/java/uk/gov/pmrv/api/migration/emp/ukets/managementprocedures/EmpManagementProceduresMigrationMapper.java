package uk.gov.pmrv.api.migration.emp.ukets.managementprocedures;

import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpDataFlowActivities;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpEnvironmentalManagementSystem;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRole;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRoles;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EmpManagementProceduresMigrationMapper {

    public static EmpMigrationManagementProcedures toManagementProcedures(EtsEmpManagementProcedures etsManagementProcedures) {

        EtsFileAttachment dataFlowFileAttachment =
            createEtsFileAttachment(etsManagementProcedures.getDataFlowAttachmentStoredFileName(), etsManagementProcedures.getDataFlowAttachmentUploadedFileName(), etsManagementProcedures.getEtsAccountId());

        EtsFileAttachment riskAssessmentFileAttachment =
            createEtsFileAttachment(etsManagementProcedures.getRiskAssessmentStoredFileName(), etsManagementProcedures.getRiskAssessmentUploadedFileName(), etsManagementProcedures.getEtsAccountId());

        return EmpMigrationManagementProcedures.builder()
                .managementProcedures(createManagementProcedures(etsManagementProcedures, dataFlowFileAttachment, riskAssessmentFileAttachment))
                .dataFlowFileAttachment(dataFlowFileAttachment)
                .riskAssessmentFileAttachment(riskAssessmentFileAttachment)
                .build();
    }

    private static EmpManagementProcedures createManagementProcedures(EtsEmpManagementProcedures etsManagementProcedures,
                                                                      EtsFileAttachment dataFlowFileAttachment,
                                                                      EtsFileAttachment riskAssessmentFileAttachment) {

        return EmpManagementProcedures.builder()
                .monitoringReportingRoles(createMonitoringReportingRoles(etsManagementProcedures.getJobDetails()))
                .recordKeepingAndDocumentation(createProcedureForm(etsManagementProcedures.getControlActivitiesRecordingProceduresProcedureTitle(),
                        etsManagementProcedures.getControlActivitiesRecordingProceduresProcedureReference(),
                        etsManagementProcedures.getControlActivitiesRecordingProceduresProcedureDescription(),
                        etsManagementProcedures.getControlActivitiesRecordingProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlActivitiesRecordingProceduresRecordsLocation(),
                        etsManagementProcedures.getControlActivitiesRecordingProceduresSystemName()))
                .assignmentOfResponsibilities(createProcedureForm(etsManagementProcedures.getManagementDetailsProcedureTitle(),
                        etsManagementProcedures.getManagementDetailsProcedureReference(),
                        etsManagementProcedures.getManagementDetailsProcedureDescription(),
                        etsManagementProcedures.getManagementDetailsDataMaintenancePost(),
                        etsManagementProcedures.getManagementDetailsRecordsLocation(),
                        etsManagementProcedures.getManagementDetailsSystemName()))
                .monitoringPlanAppropriateness(createProcedureForm(etsManagementProcedures.getManagementDetailsProcedureRegularTitle(),
                        etsManagementProcedures.getManagementDetailsProcedureRegularReference(),
                        etsManagementProcedures.getManagementDetailsProcedureRegularDescription(),
                        etsManagementProcedures.getManagementDetailsRegularDataMaintenancePost(),
                        etsManagementProcedures.getManagementDetailsRegularRecordsLocation(),
                        etsManagementProcedures.getManagementDetailsRegularSystemName()))
                .qaMeteringAndMeasuringEquipment(createProcedureForm(etsManagementProcedures.getControlActivitiesQualityProceduresProcedureTitle(),
                        etsManagementProcedures.getControlActivitiesQualityProceduresProcedureReference(),
                        etsManagementProcedures.getControlActivitiesQualityProceduresProcedureDescription(),
                        etsManagementProcedures.getControlActivitiesQualityProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlActivitiesQualityProceduresRecordsLocation(),
                        etsManagementProcedures.getControlActivitiesQualityProceduresSystemName()))
                .dataValidation(createProcedureForm(etsManagementProcedures.getControlActivitiesIntervalProceduresProcedureTitle(),
                        etsManagementProcedures.getControlActivitiesIntervalProceduresProcedureReference(),
                        etsManagementProcedures.getControlActivitiesIntervalProceduresProcedureDescription(),
                        etsManagementProcedures.getControlActivitiesIntervalProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlActivitiesIntervalProceduresRecordsLocation(),
                        etsManagementProcedures.getControlActivitiesIntervalProceduresSystemName()))
                .correctionsAndCorrectiveActions(createProcedureForm(etsManagementProcedures.getControlActivitiesCorrectiveProceduresProcedureTitle(),
                        etsManagementProcedures.getControlActivitiesCorrectiveProceduresProcedureReference(),
                        etsManagementProcedures.getControlActivitiesCorrectiveProceduresProcedureDescription(),
                        etsManagementProcedures.getControlActivitiesCorrectiveProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlActivitiesCorrectiveProceduresRecordsLocation(),
                        etsManagementProcedures.getControlActivitiesCorrectiveProceduresSystemName()))
                .controlOfOutsourcedActivities(createProcedureForm(etsManagementProcedures.getControlOutsourcedActivitiesProceduresProcedureTitle(),
                        etsManagementProcedures.getControlOutsourcedActivitiesProceduresProcedureReference(),
                        etsManagementProcedures.getControlOutsourcedActivitiesProceduresProcedureDescription(),
                        etsManagementProcedures.getControlOutsourcedActivitiesProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlOutsourcedActivitiesProceduresRecordsLocation(),
                        etsManagementProcedures.getControlOutsourcedActivitiesProceduresSystemName()))
                .assessAndControlRisks(createProcedureForm(etsManagementProcedures.getControlActivitiesRiskProceduresProcedureTitle(),
                        etsManagementProcedures.getControlActivitiesRiskProceduresProcedureReference(),
                        etsManagementProcedures.getControlActivitiesRiskProceduresProcedureDescription(),
                        etsManagementProcedures.getControlActivitiesRiskProceduresDataMaintenancePost(),
                        etsManagementProcedures.getControlActivitiesRiskProceduresRecordsLocation(),
                        etsManagementProcedures.getControlActivitiesRiskProceduresSystemName()))
                .dataFlowActivities(createDataFlowActivities(etsManagementProcedures, dataFlowFileAttachment))
                .environmentalManagementSystem(createEmpEnvironmentalManagementSystem(etsManagementProcedures))
                .upliftQuantityCrossChecks(createProcedureForm(etsManagementProcedures.getCrossChecksProcedureDetailsProcedureTitle(),
                        etsManagementProcedures.getCrossChecksProcedureDetailsProcedureReference(),
                        etsManagementProcedures.getCrossChecksProcedureDetailsProcedureDescription(),
                        etsManagementProcedures.getCrossChecksProcedureDetailsDataMaintenancePost(),
                        etsManagementProcedures.getCrossChecksProcedureDetailsRecordsLocation(),
                        etsManagementProcedures.getCrossChecksProcedureDetailsSystemName()))
                .riskAssessmentFile(riskAssessmentFileAttachment != null ? riskAssessmentFileAttachment.getUuid() : null)
                .build();
    }

    private static EmpMonitoringReportingRoles createMonitoringReportingRoles(List<String> jobDetails) {
        List<EmpMonitoringReportingRole> monitoringReportingRoles =
            jobDetails.stream().map(EmpManagementProceduresMigrationMapper::createEmpReportingRole).filter(Objects::nonNull).toList();

        return ObjectUtils.isNotEmpty(monitoringReportingRoles) ? EmpMonitoringReportingRoles.builder().monitoringReportingRoles(monitoringReportingRoles).build() : null;
    }

    private static EmpMonitoringReportingRole createEmpReportingRole(String jobDetail) {
        final List<String> jobDetailsList = Arrays.stream(jobDetail.split("\\|col\\|"))
                .filter(s -> !s.isEmpty())
                .map(String::trim).toList();
        return jobDetailsList.size() > 1 ? EmpMonitoringReportingRole.builder()
                .jobTitle(jobDetailsList.get(0))
                .mainDuties(jobDetailsList.get(1))
                .build() : null;
    }

    private static EmpProcedureForm createProcedureForm(String name, String reference, String description, String departmentRole,
                                                        String recordsLocation, String itSystem) {

        if(ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(reference) && ObjectUtils.isEmpty(description) &&
            ObjectUtils.isEmpty(departmentRole) && ObjectUtils.isEmpty(recordsLocation) && ObjectUtils.isEmpty(itSystem)) {
            return null;
        }

        return EmpProcedureForm.builder()
            .procedureDocumentName(name)
            .procedureReference(reference)
            .procedureDescription(description)
            .responsibleDepartmentOrRole(departmentRole)
            .locationOfRecords(recordsLocation)
            .itSystemUsed(itSystem)
            .build();

    }

    private static EmpDataFlowActivities createDataFlowActivities(EtsEmpManagementProcedures etsManagementProcedures,
                                                                  EtsFileAttachment dataFlowFileAttachment) {

        if(ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureTitle()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureReference()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureDescription()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresDataMaintenancePost()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresRecordsLocation()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresSystemName()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresDiagramReference()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresOtherStandardsApplied()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresPrimaryDataSources()) &&
            ObjectUtils.isEmpty(etsManagementProcedures.getDataFlowActivitiesProceduresProcessingSteps()) &&
            ObjectUtils.isEmpty(dataFlowFileAttachment)) {
            return null;
        }

        return EmpDataFlowActivities.builder()
                .procedureDocumentName(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureTitle())
                .procedureReference(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureReference())
                .procedureDescription(etsManagementProcedures.getDataFlowActivitiesProceduresProcedureDescription())
                .responsibleDepartmentOrRole(etsManagementProcedures.getDataFlowActivitiesProceduresDataMaintenancePost())
                .locationOfRecords(etsManagementProcedures.getDataFlowActivitiesProceduresRecordsLocation())
                .itSystemUsed(etsManagementProcedures.getDataFlowActivitiesProceduresSystemName())
                .diagramReference(etsManagementProcedures.getDataFlowActivitiesProceduresDiagramReference())
                .otherStandardsApplied(etsManagementProcedures.getDataFlowActivitiesProceduresOtherStandardsApplied())
                .primaryDataSources(etsManagementProcedures.getDataFlowActivitiesProceduresPrimaryDataSources())
                .processingSteps(etsManagementProcedures.getDataFlowActivitiesProceduresProcessingSteps())
                .diagramAttachmentId(dataFlowFileAttachment != null ? dataFlowFileAttachment.getUuid() : null)
                .build();
    }

    private static EtsFileAttachment createEtsFileAttachment(String storedFileName, String uploadedFileName, String etsAccountId) {

        return storedFileName != null && uploadedFileName != null &&
                MigrationConstants.ALLOWED_FILE_TYPES.contains(uploadedFileName.substring(uploadedFileName.lastIndexOf(".")).toLowerCase()) ?
                EtsFileAttachment.builder()
                        .etsAccountId(etsAccountId)
                        .uploadedFileName(uploadedFileName)
                        .storedFileName(storedFileName)
                        .uuid(UUID.randomUUID())
                        .build() : null;
    }

    private static EmpEnvironmentalManagementSystem createEmpEnvironmentalManagementSystem(EtsEmpManagementProcedures etsManagementProcedures) {
        boolean exist = !"No documented environmental management system in place".equals(etsManagementProcedures.getEmsDocumentation());
        return EmpEnvironmentalManagementSystem.builder()
            .exist(exist)
            .certified(exist ? etsManagementProcedures.isEmsCertified() : null)
            .certificationStandard(etsManagementProcedures.getStandard())
            .build();
    }
}
