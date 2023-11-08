package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpDataFlowActivities;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmissionsMonitoringPlanUkEtsTest {

    @Test
    void getEmpSectionAttachmentIds() {
        UUID additionalDocument = UUID.randomUUID();
        EmpAdditionalDocuments additionalDocuments = EmpAdditionalDocuments.builder()
            .exist(true)
            .documents(Set.of(additionalDocument))
            .build();

        UUID diagramAttachment = UUID.randomUUID();
        EmpManagementProcedures managementProcedures = EmpManagementProcedures.builder()
            .dataFlowActivities(EmpDataFlowActivities.builder()
                .procedureDescription("procedure description")
                .procedureDocumentName("procedure document name")
                .procedureReference("procedure reference")
                .responsibleDepartmentOrRole("responsible department")
                .locationOfRecords("location of records")
                .itSystemUsed("IT system")
                .primaryDataSources("primary data sources")
                .processingSteps("processing steps")
                .diagramAttachmentId(diagramAttachment)
                .build())
            .build();

        FuelMonitoringApproach emissionsMonitoringApproach = FuelMonitoringApproach.builder()
            .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
            .build();

        EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
            .additionalDocuments(additionalDocuments)
            .emissionsMonitoringApproach(emissionsMonitoringApproach)
            .managementProcedures(managementProcedures)
            .build();

        assertThat(emp.getEmpSectionAttachmentIds()).containsExactlyInAnyOrder(additionalDocument, diagramAttachment);
    }
}