package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

@Service
@RequiredArgsConstructor
public class VerificationBodyDetailsQueryService {

    private final VerificationBodyQueryService verificationBodyQueryService;
    private static final VerificationBodyMapper verificationBodyMapper = Mappers
            .getMapper(VerificationBodyMapper.class);

    public Optional<VerificationBodyDetails> getVerificationBodyDetails(Long vbId) {
		return verificationBodyQueryService.getVerificationBodyOptById(vbId)
				.map(verificationBodyMapper::toVerificationBodyDetails);
    }
}
