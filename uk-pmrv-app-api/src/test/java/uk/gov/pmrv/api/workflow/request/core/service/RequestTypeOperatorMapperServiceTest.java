package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestTypeOperatorMapperServiceTest {


    @InjectMocks
    private RequestTypeOperatorMapperService service;

    @Mock
    private UserLoginDomainService userLoginDomainService;

    @Test
    public void getRequestTypes() {
        AppUser appUser = AppUser.builder().userId("test").roleType(RoleTypeConstants.OPERATOR).build();

        when(userLoginDomainService.getUserLastLoginDomain("test")).thenReturn(AccountType.INSTALLATION);

        Set<RequestType> requestTypes = service.getRequestTypes(appUser);

        Set<RequestType> expectedRequestTypes = Arrays.stream(RequestType.values())
                .filter(type->( AccountType.INSTALLATION.equals(type.getAccountType()) || type.getAccountType() == null)
                        && type.getRoleTypes().contains(RoleTypeConstants.OPERATOR))
                .collect(Collectors.toSet());

        assertThat(requestTypes).isEqualTo(expectedRequestTypes);
    }


    @Test
    public void getRoleType() {
        assertThat(service.getRoleType()).isEqualTo(RoleTypeConstants.OPERATOR);
    }

}
