package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AirOperatingCertificateCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaOperatorDetailsSectionValidator implements AviationAerCorsiaContextValidator {

    private final EmpIssuingAuthorityQueryService issuingAuthorityQueryService;

    @Override
    public AviationAerValidationResult validate(AviationAerCorsiaContainer aerContainer) {

        final List<AviationAerViolation> aerViolations = new ArrayList<>();

        final AviationCorsiaOperatorDetails mainCompanyDetails = aerContainer.getAer().getOperatorDetails();
        final AirOperatingCertificateCorsia airOperatingCertificate = mainCompanyDetails.getAirOperatingCertificate();
        if (checkIssuingAuthorityExistence(airOperatingCertificate.getCertificateExist(),
            airOperatingCertificate.getIssuingAuthority())) {
            aerViolations.add(new AviationAerViolation(
                AviationCorsiaOperatorDetails.class.getSimpleName(),
                AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY)
            );
        }
        return AviationAerValidationResult.builder()
            .valid(aerViolations.isEmpty())
            .aerViolations(aerViolations)
            .build();
    }

    private boolean checkIssuingAuthorityExistence(Boolean shouldExist, String issuingAuthority) {
        return Boolean.TRUE.equals(shouldExist) && !issuingAuthorityQueryService.existsByName(issuingAuthority);
    }
}
