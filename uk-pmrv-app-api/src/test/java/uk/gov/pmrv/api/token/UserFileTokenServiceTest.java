package uk.gov.pmrv.api.token;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFileTokenServiceTest {

    @InjectMocks
    private UserFileTokenService service;

    @Mock
    private JwtTokenService jwtTokenService;
    
    @Mock
    private JwtProperties jwtProperties;
    
    @Test
    void generateGetFileToken() {
        String attachmentUuid = "uuid";
        String token = "token";
        long tokenExpirationMinutes = 1l;
        FileToken fileToken = FileToken.builder()
                .token(token)
                .tokenExpirationMinutes(tokenExpirationMinutes)
                .build();
        
        JwtProperties.Claim claim = Mockito.mock(JwtProperties.Claim.class);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getGetFileAttachmentExpIntervalMinutes()).thenReturn(tokenExpirationMinutes);
        when(jwtTokenService.generateToken(
                JwtTokenActionEnum.GET_FILE,
                attachmentUuid, 
                tokenExpirationMinutes)).thenReturn(token);
        
        FileToken result = service.generateGetFileToken(attachmentUuid);
        
        assertThat(result).isEqualTo(fileToken);
        verify(jwtProperties, times(1)).getClaim();
        verify(claim, times(1)).getGetFileAttachmentExpIntervalMinutes();
        verify(jwtTokenService, times(1)).generateToken(
                JwtTokenActionEnum.GET_FILE,
                attachmentUuid, 
                tokenExpirationMinutes);
    }
    
    @Test
    void resolveGetFileAttachmentUuid() {
        String getFileAttachmentToken = "token";
        String attachmentUuid = "resolvedAttachmentUuid";
        when(jwtTokenService.resolveTokenActionClaim(getFileAttachmentToken, JwtTokenActionEnum.GET_FILE))
            .thenReturn(attachmentUuid);
        
        String result = service.resolveGetFileUuid(getFileAttachmentToken);
        
        assertThat(result).isEqualTo(attachmentUuid);
        verify(jwtTokenService, times(1)).resolveTokenActionClaim(getFileAttachmentToken, JwtTokenActionEnum.GET_FILE);
    }
    
}
