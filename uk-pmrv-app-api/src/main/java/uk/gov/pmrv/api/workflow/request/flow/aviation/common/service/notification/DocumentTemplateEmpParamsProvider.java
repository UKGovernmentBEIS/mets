package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentTemplateEmpParamsProvider {

    private final AviationDocumentTemplateCommonParamsProvider commonParamsProvider;

    public TemplateParams constructTemplateParams(
        final DocumentTemplateEmpParamsSourceData sourceData) {

        final Request request = sourceData.getRequest();
        final String signatory = sourceData.getSignatory();
        final TemplateParams templateParams = commonParamsProvider.constructCommonTemplateParams(request, signatory);

        final EmissionsMonitoringPlanContainer empContainer = sourceData.getEmpContainer();
        final int consolidationNumber = sourceData.getConsolidationNumber();

        return templateParams.withParams(Map.of(
                "empContainer", empContainer,
                "consolidationNumber", consolidationNumber
            )
        );
    }
}
