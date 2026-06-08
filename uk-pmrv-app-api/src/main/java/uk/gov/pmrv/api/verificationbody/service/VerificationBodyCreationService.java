package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class VerificationBodyCreationService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final AccreditationRefNumValidationService accreditationRefNumValidationService;
    private final VerificationBodyMapper verificationBodyMapper;

    @Transactional
    public VerificationBodyInfoDTO createVerificationBody(VerificationBodyEditDTO verificationBodyCreationDTO) {
        Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemes = verificationBodyCreationDTO.getVerificationBodyEmissionSchemes();
        if (!verificationBodyEmissionSchemes.isEmpty()) {
            verificationBodyEmissionSchemes.forEach(verBodyEmissionScheme -> {
                String accreditationReferenceNumber = verBodyEmissionScheme.getAccreditationReferenceNumber();
                if (accreditationReferenceNumber != null) {
                    accreditationRefNumValidationService.validateCreate(accreditationReferenceNumber);
                }
            });
        }

        VerificationBody verificationBody = verificationBodyMapper.toVerificationBody(verificationBodyCreationDTO);

        verificationBody.setStatus(VerificationBodyStatus.PENDING);
        if (verificationBody.getEmissionSchemes() != null) {
            verificationBody.getEmissionSchemes()
                    .forEach(vbem -> vbem.setVerificationBody(verificationBody));
        }
        VerificationBody persistedVerificationBody = verificationBodyRepository.save(verificationBody);

        return verificationBodyMapper.toVerificationBodyInfoDTO(persistedVerificationBody);
    }
}
