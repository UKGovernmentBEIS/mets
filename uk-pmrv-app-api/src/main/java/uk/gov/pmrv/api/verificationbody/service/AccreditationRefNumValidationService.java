package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyEmissionSchemeRepository;

@Service
@RequiredArgsConstructor
public class AccreditationRefNumValidationService {

    private final VerificationBodyEmissionSchemeRepository verificationBodyEmissionSchemeRepository;

    public void validateUpdate(String accreditationReferenceNumber, Long verificationBodyId) {
        if (accreditationReferenceNumber == null || accreditationReferenceNumber.isBlank()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Accreditation reference number is mandatory");
        }
        boolean exists = verificationBodyEmissionSchemeRepository.existsByAccreditationReferenceNumberAndVerificationBodyIdNot(accreditationReferenceNumber, verificationBodyId);

        if (exists) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM, accreditationReferenceNumber);
        }
    }

    public void validateCreate(String accreditationReferenceNumber) {
        if (accreditationReferenceNumber == null || accreditationReferenceNumber.isBlank()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Accreditation reference number is mandatory");
        }
        boolean exists = verificationBodyEmissionSchemeRepository.existsByAccreditationReferenceNumber(accreditationReferenceNumber);

        if (exists) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM, accreditationReferenceNumber);
        }
    }

}
