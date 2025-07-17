package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsSourceData;

@Service
public abstract class EmpPreviewCreateEmpDocumentService<T extends EmissionsMonitoringPlanContainer> {

    private final DocumentTemplateEmpParamsProvider empParamsProvider;

    public EmpPreviewCreateEmpDocumentService(DocumentTemplateEmpParamsProvider empParamsProvider) {
        this.empParamsProvider = empParamsProvider;
    }

    protected TemplateParams constructTemplateParams(final Request request,
                                                   final String signatory,
                                                   final EmissionsMonitoringPlanDTO<T> emp,
                                                   final int consolidationNumber) {

        final TemplateParams templateParams = empParamsProvider.constructTemplateParams(
                DocumentTemplateEmpParamsSourceData.builder()
                        .request(request)
                        .signatory(signatory)
                        .empContainer(emp.getEmpContainer())
                        .consolidationNumber(consolidationNumber)
                        .build()
        );

        // emp id has to be set explicitly as it might not exist in the database yet (e.g. variation)
        templateParams.setPermitId(emp.getId());

        // the following information may be invalid as it is filled by the account entity which has not
        // been updated yet at the time of the preview. Thus, it is set to null.
        // The document template gets this information from the emp container, so it is up-to-date.
        templateParams.getAccountParams().setName(null);
        templateParams.getAccountParams().setLocation(null);

        return templateParams;
    }
}
