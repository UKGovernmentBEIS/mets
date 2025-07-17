package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider installationAuditSubmittedDocumentTemplateWorkflowParamsProviderTest;

    @Mock
    private InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;


    @Test
    void getContextActionType(){
        assertThat(installationAuditSubmittedDocumentTemplateWorkflowParamsProviderTest.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.INSTALLATION_AUDIT_SUBMITTED);
    }

    @Test
    void constructParams(){

        String requestId = "requestId";
        InstallationAuditRequestPayload payload = InstallationAuditRequestPayload.builder().auditYear(Year.of(2023)).build();

        Map<String, Object> params =installationAuditSubmittedDocumentTemplateWorkflowParamsProviderTest.constructParams(payload,requestId);

        verify(installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider, times(1)).constructParams(payload.getInstallationInspection());
        assertThat(params).containsEntry("inspectionDate","2023 Audit Report");
    }

    @Test
    void constructParamsFromAuditSubmitRequestTaskPayload(){

        String requestId = "requestId";
        InstallationAuditApplicationSubmitRequestTaskPayload payload = InstallationAuditApplicationSubmitRequestTaskPayload.builder().build();
        InstallationAuditRequestPayload requestPayload = InstallationAuditRequestPayload.builder().auditYear(Year.of(2023)).build();

        Map<String, Object> params = installationAuditSubmittedDocumentTemplateWorkflowParamsProviderTest.constructParamsFromAuditSubmitRequestTaskPayload(payload, requestPayload, requestId);

        verify(installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider, times(1)).constructParams(payload.getInstallationInspection());
        assertThat(params).containsEntry("inspectionDate","2023 Audit Report");

    }
}
