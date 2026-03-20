package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewNotes;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRS2ApplicationCompletedCustomMapperTest {

    @InjectMocks
    private BDRS2ApplicationCompletedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        UUID fileUuid = UUID.randomUUID();

        // Build regulator review notes with internal notes populated
        BDRS2RegulatorReviewNotes freeAllocationNotes = BDRS2RegulatorReviewNotes.builder()
                .internalNotes("internal free allocation")
                .build();

        BDRS2RegulatorReviewNotes covidNotes = BDRS2RegulatorReviewNotes.builder()
                .internalNotes("internal covid")
                .build();

        BDRS2RegulatorReviewNotes installationSectorNotes = BDRS2RegulatorReviewNotes.builder()
                .internalNotes("internal installation sector")
                .build();

        BDRS2RegulatorReviewNotes cbamSplitNotes = BDRS2RegulatorReviewNotes.builder()
                .internalNotes("internal cbam")
                .build();

        BDRS2ApplicationRegulatorReviewOutcome regulatorReviewOutcome =
                BDRS2ApplicationRegulatorReviewOutcome.builder()
                        .freeAllocationReviewNotes(freeAllocationNotes)
                        .covidAdjustmentsReviewNotes(covidNotes)
                        .installationSectorReviewNotes(installationSectorNotes)
                        .cbamSplitReviewNotes(cbamSplitNotes)
                        .build();

        BDRS2VerificationOpinionStatement opinionStatement =
                BDRS2VerificationOpinionStatement.builder()
                        .notes("Should not be visible")
                        .opinionStatementFile(fileUuid)
                        .supportingFiles(Set.of(fileUuid))
                        .build();

        BDRS2VerificationReport verificationReport =
                BDRS2VerificationReport.builder()
                        .verificationData(
                                BDRS2VerificationData.builder()
                                        .opinionStatement(opinionStatement)
                                        .build()
                        )
                        .build();

        BDRS2ApplicationCompletedRequestActionPayload payload =
                BDRS2ApplicationCompletedRequestActionPayload.builder()
                        .regulatorReviewOutcome(regulatorReviewOutcome)
                        .verificationReport(verificationReport)
                        .build();

        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.BDRS2_APPLICATION_COMPLETED)
                .payload(payload)
                .build();

        // Act
        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        // Assert basic mapping
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        assertThat(result.getPayload())
                .isInstanceOf(BDRS2ApplicationCompletedRequestActionPayload.class);

        BDRS2ApplicationCompletedRequestActionPayload resultPayload =
                (BDRS2ApplicationCompletedRequestActionPayload) result.getPayload();

        // Assert internal notes are nulled
        assertThat(resultPayload.getRegulatorReviewOutcome()
                .getFreeAllocationReviewNotes()
                .getInternalNotes()).isNull();

        assertThat(resultPayload.getRegulatorReviewOutcome()
                .getCovidAdjustmentsReviewNotes()
                .getInternalNotes()).isNull();

        assertThat(resultPayload.getRegulatorReviewOutcome()
                .getInstallationSectorReviewNotes()
                .getInternalNotes()).isNull();

        assertThat(resultPayload.getRegulatorReviewOutcome()
                .getCbamSplitReviewNotes()
                .getInternalNotes()).isNull();

        // Assert verification notes are nulled but files remain
        BDRS2VerificationOpinionStatement resultOpinion =
                resultPayload.getVerificationReport()
                        .getVerificationData()
                        .getOpinionStatement();

        assertThat(resultOpinion.getNotes()).isNull();
        assertThat(resultOpinion.getOpinionStatementFile()).isNotNull();
        assertThat(resultOpinion.getSupportingFiles()).isNotEmpty();
    }

    @Test
    void getRequestActionType() {
        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType)
                .isEqualTo(RequestActionType.BDRS2_APPLICATION_COMPLETED);
    }

    @Test
    void getUserRoleTypes() {
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
