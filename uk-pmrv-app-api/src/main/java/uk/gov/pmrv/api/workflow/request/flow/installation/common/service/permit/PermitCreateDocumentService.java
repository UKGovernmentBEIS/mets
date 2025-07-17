package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PermitCreateDocumentService {

    private final DocumentTemplatePermitParamsProvider permitParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;


	public FileDTO generateDocumentWithParams(final PermitEntityDto permitEntityDto, final TemplateParams permitParams) {

		final String fileName = constructFileName(permitEntityDto);
		return documentFileGeneratorService.generateFileDocument(DocumentTemplateType.PERMIT, permitParams, fileName);
	}
	
	public FileInfoDTO generateDocument(final Request request, 
			final String signatory,
			final PermitEntityDto permitEntityDto, 
			final PermitIssuanceRequestMetadata issuanceRequestMetadata,
			final List<PermitVariationRequestInfo> variationRequestInfoList) {
		
		final TemplateParams permitParams = constructTemplateParams(request, signatory, permitEntityDto,
				issuanceRequestMetadata, variationRequestInfoList);
		final String fileName = constructFileName(permitEntityDto);
		return documentFileGeneratorService.generateAndSaveFileDocument(DocumentTemplateType.PERMIT, permitParams, fileName);
	}

    public CompletableFuture<FileInfoDTO> generateDocumentAsync(final Request request,
                                        final String signatory,
                                        final PermitEntityDto permitEntityDto,
                                        final PermitIssuanceRequestMetadata issuanceRequestMetadata,
                                        final List<PermitVariationRequestInfo> variationRequestInfoList) {
		
        final TemplateParams permitParams = constructTemplateParams(request, signatory, permitEntityDto,
				issuanceRequestMetadata, variationRequestInfoList);
        
        final String fileName = constructFileName(permitEntityDto);
        return documentFileGeneratorService.generateAndSaveFileDocumentAsync(
            DocumentTemplateType.PERMIT,
            permitParams,
            fileName
        );
    }
    
    private TemplateParams constructTemplateParams(final Request request, final String signatory,
			final PermitEntityDto permitEntityDto, final PermitIssuanceRequestMetadata issuanceRequestMetadata,
			final List<PermitVariationRequestInfo> variationRequestInfoList) {

		final PermitContainer permitContainer = permitEntityDto.getPermitContainer();
		return permitParamsProvider.constructTemplateParams(
				DocumentTemplatePermitParamsSourceData.builder()
				.request(request)
				.signatory(signatory)
				.permitContainer(permitContainer)
				.consolidationNumber(permitEntityDto.getConsolidationNumber())
				.issuanceRequestMetadata(issuanceRequestMetadata)
				.variationRequestInfoList(variationRequestInfoList).build());
	}
    
    private String constructFileName(final PermitEntityDto permitEntityDto) {
		return permitEntityDto.getId() + " v" + permitEntityDto.getConsolidationNumber() + ".pdf";
	}
}
