package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private InstallationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider installationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider;

    @Mock
    private InstallationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider;


    @Test
    void getContextActionType(){

        assertThat(installationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED);
    }

    @Test
    void constructParams(){

        String requestId = "requestId";
        InstallationOnsiteInspectionRequestPayload payload = InstallationOnsiteInspectionRequestPayload
                .builder()
                .installationInspection(InstallationInspection
                        .builder()
                        .details(InstallationInspectionDetails
                                .builder()
                                .date(LocalDate.of(2024,12,5))
                                .build())
                        .build())
                .build();

        Map<String, Object> params = installationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider.constructParams(payload,requestId);

        verify(installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider, times(1)).constructParams(payload.getInstallationInspection());
        assertThat(params).containsEntry("inspectionDate","5 December 2024");
    }

    @Test
    void constructParamsFromAuditSubmitRequestTaskPayload(){

        String requestId = "requestId";
        InstallationInspection installationInspection = InstallationInspection
                        .builder()
                        .details(InstallationInspectionDetails
                                .builder()
                                .date(LocalDate.of(2024,12,5))
                                .build())
                        .build();
        InstallationAuditApplicationSubmitRequestTaskPayload payload = InstallationAuditApplicationSubmitRequestTaskPayload
                .builder()
                .installationInspection(installationInspection)
                .build();

        InstallationOnsiteInspectionRequestPayload requestPayload = InstallationOnsiteInspectionRequestPayload
                .builder()
                .installationInspection(installationInspection)
                .build();

        Map<String, Object> params = installationOnsiteInspectionSubmittedDocumentTemplateWorkflowParamsProvider.constructParamsFromOnsiteInspectionSubmitRequestTaskPayload(payload, requestPayload, requestId);

        verify(installationInspectionSubmittedDocumentTemplateWorkflowCommonParamsProvider, times(1)).constructParams(payload.getInstallationInspection());
        assertThat(params).containsEntry("inspectionDate","5 December 2024");

    }

}
