package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;

@Service
@RequiredArgsConstructor
public class EmpCorsiaOperatorDetailsSectionValidator implements EmpCorsiaContextValidator {

    private final EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanCorsiaContainer empContainer) {
        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();

        final EmpCorsiaOperatorDetails mainCompanyDetails = empContainer.getEmissionsMonitoringPlan().getOperatorDetails();
        final AirOperatingCertificateCorsia airOperatingCertificate = mainCompanyDetails.getAirOperatingCertificate();
        if (checkIssuingAuthorityExistence(airOperatingCertificate.getCertificateExist(), airOperatingCertificate.getIssuingAuthority())) {
            empViolations.add(new EmissionsMonitoringPlanViolation(EmpCorsiaOperatorDetails.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY));
        }

        if (Boolean.TRUE.equals(mainCompanyDetails.getSubsidiaryCompanyExist())) {
            for (SubsidiaryCompanyCorsia subsidiary : mainCompanyDetails.getSubsidiaryCompanies()) {
                final AirOperatingCertificateCorsia subsidiaryCertificate = subsidiary.getAirOperatingCertificate();
                if (checkIssuingAuthorityExistence(subsidiaryCertificate.getCertificateExist(), subsidiaryCertificate.getIssuingAuthority())) {
                    empViolations.add(new EmissionsMonitoringPlanViolation(SubsidiaryCompanyCorsia.class.getSimpleName(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY));
                }
            }
        }

        return EmissionsMonitoringPlanValidationResult.builder()
            .valid(empViolations.isEmpty())
            .empViolations(empViolations)
            .build();
    }

    private boolean checkIssuingAuthorityExistence(Boolean shouldExist, String issuingAuthority) {
        return Boolean.TRUE.equals(shouldExist) && !issuingAuthorityQueryService.existsByName(issuingAuthority);
    }
}
