package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.TemplateSubsidiaryCompany;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentTemplateEmpParamsProvider {

    private final AviationDocumentTemplateCommonParamsProvider commonParamsProvider;

    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;

    public TemplateParams constructTemplateParams(
        final DocumentTemplateEmpParamsSourceData sourceData) {

        final Request request = sourceData.getRequest();
        final String signatory = sourceData.getSignatory();
        final TemplateParams templateParams = commonParamsProvider.constructCommonTemplateParams(request, signatory);

        final EmissionsMonitoringPlanContainer empContainer = sourceData.getEmpContainer();
        final int consolidationNumber = sourceData.getConsolidationNumber();

        Map<String, Object> params = new HashMap<>();
        params.put("empContainer", empContainer);
        params.put("consolidationNumber", consolidationNumber);

        if (EmissionTradingScheme.CORSIA.equals(empContainer.getScheme())) {
            final EmissionsMonitoringPlanCorsia empCorsia = ((EmissionsMonitoringPlanCorsiaContainer) empContainer).getEmissionsMonitoringPlan();
            String organisationLocation = documentTemplateLocationInfoResolver.constructLocationInfo(empCorsia.getOperatorDetails().getOrganisationStructure().getOrganisationLocation());
            if (OrganisationLegalStatusType.LIMITED_COMPANY.equals(empCorsia.getOperatorDetails().getOrganisationStructure().getLegalStatusType())) {
                final LimitedCompanyOrganisation limitedCompanyOrganisation = (LimitedCompanyOrganisation) empCorsia.getOperatorDetails().getOrganisationStructure();
                if (Boolean.TRUE.equals(limitedCompanyOrganisation.getDifferentContactLocationExist())) {
                    final String differentContactLocation = documentTemplateLocationInfoResolver.constructLocationInfo(limitedCompanyOrganisation.getDifferentContactLocation());
                    params.put("differentContactLocation", differentContactLocation);
                }
            }
            final List<TemplateSubsidiaryCompany> subsidiaryCompanies = createTemplateSubsidiaryCompanies(empCorsia.getOperatorDetails().getSubsidiaryCompanies());
            params.put("organisationLocation", organisationLocation);
            params.put("subsidiaryCompanies", subsidiaryCompanies);
        }

        return templateParams.withParams(params);
    }

    private List<TemplateSubsidiaryCompany> createTemplateSubsidiaryCompanies(List<SubsidiaryCompanyCorsia> subsidiaryCompanies) {
        return subsidiaryCompanies.stream()
                .map(this::createSubsidiaryCompany)
                .toList();
    }

    private TemplateSubsidiaryCompany createSubsidiaryCompany(SubsidiaryCompanyCorsia subsidiaryCompany) {
        return TemplateSubsidiaryCompany.builder()
                .subsidiaryCompany(subsidiaryCompany)
                .registeredAddress(documentTemplateLocationInfoResolver.constructLocationInfo(subsidiaryCompany.getRegisteredLocation()))
                .build();
    }
}
