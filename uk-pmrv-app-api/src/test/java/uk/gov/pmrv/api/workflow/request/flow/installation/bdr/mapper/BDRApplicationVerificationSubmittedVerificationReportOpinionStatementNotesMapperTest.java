package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationVerificationSubmittedVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private BDRApplicationVerificationSubmittedVerificationReportOpinionStatementNotesMapper mapper;



    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.BDR_APPLICATION_VERIFICATION_SUBMITTED);
    }

    @Test
    public void getUserRoleTypes(){
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
