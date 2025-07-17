package uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateSubsidiaryCompany {

    private SubsidiaryCompanyCorsia subsidiaryCompany;

    private String registeredAddress;

}
