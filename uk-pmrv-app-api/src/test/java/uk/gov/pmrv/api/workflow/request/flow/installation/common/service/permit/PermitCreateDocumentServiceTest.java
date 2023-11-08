package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

@ExtendWith(MockitoExtension.class)
class PermitCreateDocumentServiceTest {

    @InjectMocks
    private PermitCreateDocumentService service;

    @Mock
    private DocumentTemplatePermitParamsProvider permitParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generateDocument() {

        final long accountId = 5L;
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .build();
        final Request request = Request.builder().id("1").accountId(accountId)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder().permitType(PermitType.GHGE).build();
        final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").consolidationNumber(1).permitContainer(permitContainer).build();
        final List<LocalDateTime> rfiResponseDates = List.of(
            LocalDateTime.of(2022, 1, 1, 1, 1),
            LocalDateTime.of(2022, 2, 2, 2, 2)
        );
        final String signatory = "signatory";
        final PermitIssuanceRequestMetadata issuanceRequestMetadata =
            PermitIssuanceRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build();
		final List<PermitVariationRequestInfo> variationRequestInfoList = List.of(PermitVariationRequestInfo.builder()
				.id("varRequestId1")
				.metadata(PermitVariationRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build()).build()

		);
        final DocumentTemplatePermitParamsSourceData sourceData = DocumentTemplatePermitParamsSourceData.builder()
            .request(request)
            .signatory(signatory)
            .permitContainer(permitContainer)
            .consolidationNumber(1)
            .issuanceRequestMetadata(issuanceRequestMetadata)
            .variationRequestInfoList(variationRequestInfoList)
            .build();
        final TemplateParams permitParams = TemplateParams.builder().build();

        when(permitParamsProvider.constructTemplateParams(sourceData)).thenReturn(permitParams);

        service.generateDocument(request, signatory, permitEntityDto, issuanceRequestMetadata, variationRequestInfoList);

        verify(permitParamsProvider, times(1)).constructTemplateParams(sourceData);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT,
            permitParams,
            "permitId v1.pdf"
        );
    }
    
    @Test
    void generateDocumentAsync() throws InterruptedException, ExecutionException {
        final long accountId = 5L;
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .build();
        final Request request = Request.builder().id("1").accountId(accountId)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(requestPayload)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder().permitType(PermitType.GHGE).build();
        final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").consolidationNumber(1).permitContainer(permitContainer).build();
        final List<LocalDateTime> rfiResponseDates = List.of(
            LocalDateTime.of(2022, 1, 1, 1, 1),
            LocalDateTime.of(2022, 2, 2, 2, 2)
        );
        final String signatory = "signatory";
        final PermitIssuanceRequestMetadata issuanceRequestMetadata =
            PermitIssuanceRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build();
		final List<PermitVariationRequestInfo> variationRequestInfoList = List.of(PermitVariationRequestInfo.builder()
				.id("varRequestId1")
				.metadata(PermitVariationRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build()).build()

		);
        final DocumentTemplatePermitParamsSourceData sourceData = DocumentTemplatePermitParamsSourceData.builder()
            .request(request)
            .signatory(signatory)
            .permitContainer(permitContainer)
            .consolidationNumber(1)
            .issuanceRequestMetadata(issuanceRequestMetadata)
            .variationRequestInfoList(variationRequestInfoList)
            .build();
        final TemplateParams permitParams = TemplateParams.builder().build();

        String fileUuid = UUID.randomUUID().toString();
        FileInfoDTO generatedDoc = FileInfoDTO.builder()
        		.name("genFile")
        		.uuid(fileUuid)
        		.build();
        
        when(permitParamsProvider.constructTemplateParams(sourceData)).thenReturn(permitParams);
        when(documentFileGeneratorService.generateFileDocumentAsync(DocumentTemplateType.PERMIT, permitParams, "permitId v1.pdf"))
        	.thenReturn(CompletableFuture.completedFuture(generatedDoc));

        CompletableFuture<FileInfoDTO> result = service.generateDocumentAsync(request, signatory, permitEntityDto, issuanceRequestMetadata, variationRequestInfoList);

        assertThat(result.get()).isEqualTo(generatedDoc);
        
        verify(permitParamsProvider, times(1)).constructTemplateParams(sourceData);
        verify(documentFileGeneratorService, times(1)).generateFileDocumentAsync(
            DocumentTemplateType.PERMIT,
            permitParams,
            "permitId v1.pdf"
        );
    }

}
