package uk.gov.pmrv.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityResourceAdapterTest {

    @InjectMocks
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Test
    void getUserScopedRequestTaskTypesByAccountType() {
        final String userId = "userId";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final AccountType accountType = AccountType.AVIATION;

        when(regulatorAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
            .thenReturn(Map.of(
                competentAuthority,
                Set.of(
                    RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW.name(),
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name()))
            );

        Map<CompetentAuthorityEnum, Set<RequestTaskType>> userScopedRequestTaskTypesByAccountType =
            regulatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(userId, accountType);

        assertThat(userScopedRequestTaskTypesByAccountType).containsExactlyEntriesOf(
            Map.of(
                competentAuthority, Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT)
            )
        );
    }
}