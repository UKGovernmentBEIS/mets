package uk.gov.pmrv.api.web.orchestrator.authorization.validate;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.AccountOperatorAuthorityUpdateWrapperDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountOperatorAuthorityUpdateValidatorTest {

    @InjectMocks
    private AccountOperatorAuthorityUpdateValidator validator;
    
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    
    @Test
    void isValid_not_empty() {
        AccountOperatorAuthorityUpdateWrapperDTO dto = 
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(List.of(
                            AccountOperatorAuthorityUpdateDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).userId("user").build()
                            ))
                    .contactTypes(Map.of())
                    .build();
        
        boolean result = validator.isValid(dto, constraintValidatorContext);
        assertThat(result).isTrue();
    }
    
    @Test
    void isValid_both_empty() {
        AccountOperatorAuthorityUpdateWrapperDTO dto = 
                AccountOperatorAuthorityUpdateWrapperDTO.builder()
                    .accountOperatorAuthorityUpdateList(List.of())
                    .contactTypes(Map.of())
                    .build();
        
        boolean result = validator.isValid(dto, constraintValidatorContext);
        assertThat(result).isFalse();
    }
}
