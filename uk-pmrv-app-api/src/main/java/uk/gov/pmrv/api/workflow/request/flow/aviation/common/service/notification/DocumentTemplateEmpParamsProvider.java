package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
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

    public TemplateParams constructTemplateParams(final DocumentTemplateEmpParamsSourceData sourceData) {
        final Request request = sourceData.getRequest();
        final String signatory = sourceData.getSignatory();
        final TemplateParams templateParams = commonParamsProvider.constructCommonTemplateParams(request, signatory);

        final EmissionsMonitoringPlanContainer empContainer = sourceData.getEmpContainer();

        final Map<String, Object> params = new HashMap<>();
        params.put("empContainer", empContainer);
        params.put("consolidationNumber", sourceData.getConsolidationNumber());
        params.put("documentIsDraft",signatory==null);
        params.putAll(buildCorsiaParams(empContainer));

        return templateParams.withParams(params);
    }

    private Map<String, Object> buildCorsiaParams(final EmissionsMonitoringPlanContainer empContainer) {
        if (empContainer == null || !EmissionTradingScheme.CORSIA.equals(empContainer.getScheme())) {
            return Map.of();
        }

        final EmissionsMonitoringPlanCorsia empCorsia =
                ((EmissionsMonitoringPlanCorsiaContainer) empContainer).getEmissionsMonitoringPlan();
        if (empCorsia == null || empCorsia.getOperatorDetails() == null) {
            return Map.of();
        }

        final var operatorDetails = empCorsia.getOperatorDetails();
        final Map<String, Object> params = new HashMap<>();

        addOrganisationLocationParams(operatorDetails.getOrganisationStructure(), params);
        addSubsidiaryCompaniesParam(operatorDetails.getSubsidiaryCompanies(), params);

        return params;
    }

    private void addOrganisationLocationParams(final OrganisationStructure organisationStructure,
                                               final Map<String, Object> params) {
        if (organisationStructure == null || organisationStructure.getOrganisationLocation() == null) {
            return;
        }

        params.put("organisationLocation",
                documentTemplateLocationInfoResolver.constructLocationInfo(organisationStructure.getOrganisationLocation()));

        if (OrganisationLegalStatusType.LIMITED_COMPANY.equals(organisationStructure.getLegalStatusType())
                && organisationStructure instanceof LimitedCompanyOrganisation limitedCompany
                && Boolean.TRUE.equals(limitedCompany.getDifferentContactLocationExist())
                && limitedCompany.getDifferentContactLocation() != null) {
            params.put("differentContactLocation",
                    documentTemplateLocationInfoResolver.constructLocationInfo(limitedCompany.getDifferentContactLocation()));
        }
    }

    private void addSubsidiaryCompaniesParam(final List<SubsidiaryCompanyCorsia> subsidiaryCompanies,
                                             final Map<String, Object> params) {
        if (subsidiaryCompanies != null && !subsidiaryCompanies.isEmpty()) {
            params.put("subsidiaryCompanies", createTemplateSubsidiaryCompanies(subsidiaryCompanies));
        }
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
