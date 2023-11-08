package uk.gov.pmrv.api.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFileTokenService {

    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    
    public String resolveGetFileUuid(String getFileToken) {
        return jwtTokenService.resolveTokenActionClaim(getFileToken, JwtTokenActionEnum.GET_FILE);
    }

    public FileToken generateGetFileToken(String fileUuid) {
        long expirationMinutes = jwtProperties.getClaim().getGetFileAttachmentExpIntervalMinutes();
        String token = jwtTokenService.generateToken(JwtTokenActionEnum.GET_FILE,
                fileUuid,
                expirationMinutes);
        return FileToken.builder().token(token).tokenExpirationMinutes(expirationMinutes).build();
    }
}
