package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AirOperatingCertificate;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.OperatingLicense;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsOperatorDetailsSectionValidator implements AviationAerUkEtsContextValidator {

    private final EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Override
    public AviationAerValidationResult validate(AviationAerUkEtsContainer aerContainer) {

        List<AviationAerViolation> aerViolations = new ArrayList<>();

        final AirOperatingCertificate airOperatingCertificate = aerContainer.getAer().getOperatorDetails().getAirOperatingCertificate();
        final OperatingLicense operatingLicense = aerContainer.getAer().getOperatorDetails().getOperatingLicense();
        if (checkIssuingAuthorityExistence(airOperatingCertificate.getCertificateExist(), airOperatingCertificate.getIssuingAuthority())) {
            aerViolations.add(new AviationAerViolation(AviationOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY));
        }
        if (checkIssuingAuthorityExistence(operatingLicense.getLicenseExist(), operatingLicense.getIssuingAuthority())) {
            aerViolations.add(new AviationAerViolation(AviationOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY));
        }

        return AviationAerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }

    private boolean checkIssuingAuthorityExistence(Boolean shouldExist,String issuingAuthority) {
        return Boolean.TRUE.equals(shouldExist) && !issuingAuthorityQueryService.existsByName(issuingAuthority);
    }
}
