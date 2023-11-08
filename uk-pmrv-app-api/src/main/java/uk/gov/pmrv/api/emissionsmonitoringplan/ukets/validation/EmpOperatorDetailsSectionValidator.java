package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpOperatorDetailsSectionValidator implements EmpUkEtsContextValidator {

    private final EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanUkEtsContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();

        final AirOperatingCertificate airOperatingCertificate = empContainer.getEmissionsMonitoringPlan().getOperatorDetails().getAirOperatingCertificate();
        final OperatingLicense operatingLicense = empContainer.getEmissionsMonitoringPlan().getOperatorDetails().getOperatingLicense();
        if (checkIssuingAuthorityExistence(airOperatingCertificate.getCertificateExist(), airOperatingCertificate.getIssuingAuthority())) {
            empViolations.add(new EmissionsMonitoringPlanViolation(EmpOperatorDetails.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY, operatingLicense.getIssuingAuthority()));
        }
        if (checkIssuingAuthorityExistence(operatingLicense.getLicenseExist(), operatingLicense.getIssuingAuthority())) {
            empViolations.add(new EmissionsMonitoringPlanViolation(EmpOperatorDetails.class.getSimpleName(), EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ISSUING_AUTHORITY, operatingLicense.getIssuingAuthority()));
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
