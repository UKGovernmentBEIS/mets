package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmpManagementProceduresTest {

    @Test
    void getAttachmentIds() {
        UUID diagramAttachmentId = UUID.randomUUID();
        EmpManagementProcedures procedures = EmpManagementProcedures.builder()
            .dataFlowActivities(EmpDataFlowActivities.builder()
                .procedureDescription("procedure description")
                .procedureDocumentName("procedure document name")
                .procedureReference("procedure reference")
                .responsibleDepartmentOrRole("responsible department")
                .locationOfRecords("location of records")
                .itSystemUsed("IT system")
                .primaryDataSources("primary data sources")
                .processingSteps("processing steps")
                .diagramAttachmentId(diagramAttachmentId)
                .build())
            .build();

        assertThat(procedures.getAttachmentIds()).containsOnly(diagramAttachmentId);
    }
}