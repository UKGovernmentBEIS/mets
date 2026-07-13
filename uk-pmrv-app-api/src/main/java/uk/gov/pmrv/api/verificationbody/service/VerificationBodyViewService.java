package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationBodyViewService {

    private static final VerificationBodyMapper verificationBodyMapper = Mappers.getMapper(VerificationBodyMapper.class);
    private final VerificationBodyRepository verificationBodyRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    public VerificationBodyInfoResponseDTO getVerificationBodies(AppUser user) {
        List<VerificationBodyInfoDTO> verificationBodies = verificationBodyMapper
            .toVerificationBodyInfoDTO(verificationBodyRepository.findAll());

        // Check if user has the permission of editing VBs
        boolean isEditable = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, Scope.MANAGE_VB);

        return VerificationBodyInfoResponseDTO.builder().verificationBodies(verificationBodies).editable(isEditable).build();
    }
}
