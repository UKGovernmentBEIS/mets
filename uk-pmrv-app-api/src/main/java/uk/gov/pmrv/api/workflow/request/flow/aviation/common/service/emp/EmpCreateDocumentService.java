package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsSourceData;

@Service
@RequiredArgsConstructor
public class EmpCreateDocumentService {

    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final DocumentTemplateEmpParamsProvider empParamsProvider;


    public FileDTO generateDocumentWithParams(final EmissionsMonitoringPlanDTO<?> empDto,
                                              final DocumentTemplateType type,
                                              final TemplateParams empParams) {

        final String fileName = constructFileName(empDto);
        return documentFileGeneratorService.generateFileDocument(type, empParams, fileName);
    }
    
	public CompletableFuture<FileInfoDTO> generateDocumentAsync(final Request request, final String signatory,
			final EmissionsMonitoringPlanDTO<?> empDto, final DocumentTemplateType documentTetmplateType) {
        final EmissionsMonitoringPlanContainer empContainer = empDto.getEmpContainer();
        final TemplateParams empParams = constructTemplateParams(request, signatory, empDto, empContainer);

        final String fileName = constructFileName(empDto);
        return documentFileGeneratorService.generateAndSaveFileDocumentAsync(
        		documentTetmplateType,
                empParams,
                fileName
        );
    }

    private TemplateParams constructTemplateParams(final Request request, final String signatory,
                                                   final EmissionsMonitoringPlanDTO<?> empDto,
                                                   final EmissionsMonitoringPlanContainer empContainer) {
        return empParamsProvider.constructTemplateParams(
                DocumentTemplateEmpParamsSourceData.builder()
                        .request(request)
                        .signatory(signatory)
                        .empContainer(empContainer)
                        .consolidationNumber(empDto.getConsolidationNumber())
                        .build());
    }

    private String constructFileName(final EmissionsMonitoringPlanDTO<?> empDto) {
        return empDto.getId() + " v" + empDto.getConsolidationNumber() + ".pdf";
    }
}
