package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationCorsiaAerAnnualOffsettingCreateValidatorTest {

    @InjectMocks
    private AviationCorsiaAerAnnualOffsettingCreateValidator validator;

    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo((Set.of(
                AviationAccountStatus.NEW,
                AviationAccountStatus.LIVE)));
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo((Set.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)));
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING);
    }
}
