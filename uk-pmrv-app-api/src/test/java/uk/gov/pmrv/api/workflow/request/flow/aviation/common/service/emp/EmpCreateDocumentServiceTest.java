package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@ExtendWith(MockitoExtension.class)
class EmpCreateDocumentServiceTest {

    @InjectMocks
    private EmpCreateDocumentService cut;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private DocumentTemplateEmpParamsProvider empParamsProvider;
    
    @Test
    void generateDocumentWithParams() {

        final EmissionsMonitoringPlanUkEtsDTO empEntityDto = EmissionsMonitoringPlanUkEtsDTO.builder()
            .id("empId").consolidationNumber(1).build();
        final TemplateParams permitParams = TemplateParams.builder().build();

        cut.generateDocumentWithParams(empEntityDto, DocumentTemplateType.EMP_UKETS, permitParams);

        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.EMP_UKETS,
            permitParams,
            "empId v1.pdf"
        );
    }
    
    @Test
    void generateDocumentAsync() throws InterruptedException, ExecutionException {
        final long accountId = 5L;
        final EmpIssuanceUkEtsRequestPayload requestPayload = EmpIssuanceUkEtsRequestPayload.builder()
                .build();
        final Request request = Request.builder().id("1").accountId(accountId)
                .type(RequestType.EMP_ISSUANCE_UKETS)
                .payload(requestPayload)
                .build();
        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder().build();
        final EmissionsMonitoringPlanUkEtsDTO empDto =
                EmissionsMonitoringPlanUkEtsDTO.builder().id("empId").consolidationNumber(1).empContainer(empContainer).build();

        final String signatory = "signatory";
        final DocumentTemplateEmpParamsSourceData sourceData = DocumentTemplateEmpParamsSourceData
            .builder()
                .request(request)
                .signatory(signatory)
                .empContainer(empContainer)
                .consolidationNumber(1)
                .build();
        final TemplateParams empParams = TemplateParams.builder().build();

        String fileUuid = UUID.randomUUID().toString();
        FileInfoDTO generatedDoc = FileInfoDTO.builder()
                .name("genFile")
                .uuid(fileUuid)
                .build();

        when(empParamsProvider.constructTemplateParams(sourceData)).thenReturn(empParams);
        when(documentFileGeneratorService.generateAndSaveFileDocumentAsync(DocumentTemplateType.EMP_UKETS, empParams, "empId v1.pdf"))
                .thenReturn(CompletableFuture.completedFuture(generatedDoc));

        CompletableFuture<FileInfoDTO> result =
        		cut.generateDocumentAsync(request, signatory, empDto, DocumentTemplateType.EMP_UKETS);

        assertThat(result.get()).isEqualTo(generatedDoc);

        verify(empParamsProvider, times(1)).constructTemplateParams(sourceData);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocumentAsync(
                DocumentTemplateType.EMP_UKETS,
                empParams,
                "empId v1.pdf"
        );
    }
}
