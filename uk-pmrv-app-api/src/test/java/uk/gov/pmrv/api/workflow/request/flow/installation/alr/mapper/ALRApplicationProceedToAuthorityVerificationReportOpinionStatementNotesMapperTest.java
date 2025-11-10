package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationProceedToAuthorityVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private ALRApplicationProceedToAuthorityVerificationReportOpinionStatementNotesMapper mapper;

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.ALR_APPLICATION_PROCEEDED_TO_AUTHORITY);
    }

    @Test
    public void getUserRoleTypes() {
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
