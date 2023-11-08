package uk.gov.pmrv.api.migration.emp.ukets.managementprocedures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpManagementProcedures {

    private String etsAccountId;
    //Monitoring and reporting roles
    private List<String> jobDetails;
    //Record keeping and documentation
    private String controlActivitiesRecordingProceduresProcedureTitle;
    private String controlActivitiesRecordingProceduresProcedureReference;
    private String controlActivitiesRecordingProceduresProcedureDescription;
    private String controlActivitiesRecordingProceduresDataMaintenancePost;
    private String controlActivitiesRecordingProceduresRecordsLocation;
    private String controlActivitiesRecordingProceduresSystemName;
    //Assignment of Responsibilities
    private String managementDetailsProcedureTitle;
    private String managementDetailsProcedureReference;
    private String managementDetailsProcedureDescription;
    private String managementDetailsDataMaintenancePost;
    private String managementDetailsRecordsLocation;
    private String managementDetailsSystemName;
    //Monitoring plan appropriateness
    private String managementDetailsProcedureRegularTitle;
    private String managementDetailsProcedureRegularReference;
    private String managementDetailsProcedureRegularDescription;
    private String managementDetailsRegularDataMaintenancePost;
    private String managementDetailsRegularRecordsLocation;
    private String managementDetailsRegularSystemName;
    //Quality assurance of metering and measuring equipment
    private String controlActivitiesQualityProceduresProcedureTitle;
    private String controlActivitiesQualityProceduresProcedureReference;
    private String controlActivitiesQualityProceduresProcedureDescription;
    private String controlActivitiesQualityProceduresDataMaintenancePost;
    private String controlActivitiesQualityProceduresRecordsLocation;
    private String controlActivitiesQualityProceduresSystemName;
    //Data validation
    private String controlActivitiesIntervalProceduresProcedureTitle;
    private String controlActivitiesIntervalProceduresProcedureReference;
    private String controlActivitiesIntervalProceduresProcedureDescription;
    private String controlActivitiesIntervalProceduresDataMaintenancePost;
    private String controlActivitiesIntervalProceduresRecordsLocation;
    private String controlActivitiesIntervalProceduresSystemName;
    //Corrections and corrective actions
    private String controlActivitiesCorrectiveProceduresProcedureTitle;
    private String controlActivitiesCorrectiveProceduresProcedureReference;
    private String controlActivitiesCorrectiveProceduresProcedureDescription;
    private String controlActivitiesCorrectiveProceduresDataMaintenancePost;
    private String controlActivitiesCorrectiveProceduresRecordsLocation;
    private String controlActivitiesCorrectiveProceduresSystemName;
    //Control of outsourced activities
    private String controlOutsourcedActivitiesProceduresProcedureTitle;
    private String controlOutsourcedActivitiesProceduresProcedureReference;
    private String controlOutsourcedActivitiesProceduresProcedureDescription;
    private String controlOutsourcedActivitiesProceduresDataMaintenancePost;
    private String controlOutsourcedActivitiesProceduresRecordsLocation;
    private String controlOutsourcedActivitiesProceduresSystemName;
    //Assessing and controlling risks
    private String controlActivitiesRiskProceduresProcedureTitle;
    private String controlActivitiesRiskProceduresProcedureReference;
    private String controlActivitiesRiskProceduresProcedureDescription;
    private String controlActivitiesRiskProceduresDataMaintenancePost;
    private String controlActivitiesRiskProceduresRecordsLocation;
    private String controlActivitiesRiskProceduresSystemName;
    //Data flow activities
    private String dataFlowActivitiesProceduresProcedureTitle;
    private String dataFlowActivitiesProceduresProcedureReference;
    private String dataFlowActivitiesProceduresProcedureDescription;
    private String dataFlowActivitiesProceduresDataMaintenancePost;
    private String dataFlowActivitiesProceduresRecordsLocation;
    private String dataFlowActivitiesProceduresSystemName;
    private String dataFlowActivitiesProceduresDiagramReference;
    private String dataFlowActivitiesProceduresOtherStandardsApplied;
    private String dataFlowActivitiesProceduresPrimaryDataSources;
    private String dataFlowActivitiesProceduresProcessingSteps;
    private String dataFlowAttachmentStoredFileName;
    private String dataFlowAttachmentUploadedFileName;
    //Environmental management system
    private boolean isEmsCertified;
    private String emsDocumentation;
    private String standard;
    //Risk assessment file
    private String riskAssessmentStoredFileName;
    private String riskAssessmentUploadedFileName;
    //Uplift quantity cross-checks
    private String crossChecksProcedureDetailsProcedureTitle;
    private String crossChecksProcedureDetailsProcedureReference;
    private String crossChecksProcedureDetailsProcedureDescription;
    private String crossChecksProcedureDetailsDataMaintenancePost;
    private String crossChecksProcedureDetailsRecordsLocation;
    private String crossChecksProcedureDetailsSystemName;
}
