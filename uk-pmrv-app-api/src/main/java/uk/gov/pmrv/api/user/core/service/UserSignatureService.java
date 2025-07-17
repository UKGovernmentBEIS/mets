package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSignatureService {

    private final UserAuthService userAuthService;
    private final UserFileTokenService userFileTokenService;
    
    @Transactional
    public FileToken generateSignatureFileToken(String userId, UUID signatureUuid) {
        UserDetails userDetails = userAuthService.getUserDetails(userId).orElseThrow(() -> new BusinessException(
            ErrorCode.RESOURCE_NOT_FOUND));
        
        if(userDetails.getSignature() == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        if(!signatureUuid.toString().equals(userDetails.getSignature().getUuid())){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        return userFileTokenService.generateGetFileToken(signatureUuid.toString());
    }
    
    @Transactional(readOnly = true)
    public FileDTO getSignatureFileDTOByToken(String getFileToken) {
        String fileUuid = userFileTokenService.resolveGetFileUuid(getFileToken);
        return userAuthService.getUserSignature(fileUuid).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
    
}
