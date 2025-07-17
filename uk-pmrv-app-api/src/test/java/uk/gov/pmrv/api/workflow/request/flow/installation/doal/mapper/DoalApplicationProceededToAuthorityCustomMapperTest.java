package uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.OperatorActivityLevelReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.VerificationReportOfTheActivityLevelReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.WithholdingAllowancesNotice;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

@ExtendWith(MockitoExtension.class)
class DoalApplicationProceededToAuthorityCustomMapperTest {

    @InjectMocks
    private DoalApplicationProceededToAuthorityCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final LocalDateTime localDateTime = LocalDateTime.now();

        final UUID attachmentUUID = UUID.randomUUID();
        final String attachmentName = UUID.randomUUID().toString();
        final UUID documentUUID = UUID.randomUUID();
        final UUID document2UUID = UUID.randomUUID();
        final UUID document3UUID = UUID.randomUUID();
        final UUID document4UUID = UUID.randomUUID();
        final String comment1 = "Test comment 1";
        final String comment2 = "Test comment 2";
        final String comment3 = "Test comment 3";
        final String comment4 = "Test comment 4";
        final String comment5 = "Test comment 5";
        final String comment6 = "Test comment 6";
        final String explainEstimates = "test explain estimates string";
        final String otherChangeTypeName = "test other change type name string";
        final String changedActivityLevel = "test changed activity level string";
        final String doalDeterminationReason = "test doal determination reason string";
        final SortedSet<PreliminaryAllocation> preliminaryAllocations = new TreeSet<>();
        preliminaryAllocations.add(PreliminaryAllocation.builder().subInstallationName(SubInstallationName.ALUMINIUM)
            .year(Year.now()).allowances(1).build());

        final RequestAction requestAction = RequestAction.builder()
            .id(11L)
            .type(RequestActionType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(DoalApplicationProceededToAuthorityRequestActionPayload.builder()
                .doalAttachments(Map.of(attachmentUUID, attachmentName))
                .doal(Doal.builder()
                    .operatorActivityLevelReport(OperatorActivityLevelReport.builder().document(documentUUID)
                        .areActivityLevelsEstimated(true).comment(comment1).build())
                    .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                        .document(document2UUID).comment(comment2).build())
                    .additionalDocuments(DoalAdditionalDocuments.builder()
                        .documents(Set.of(document3UUID, document4UUID))
                        .comment(comment3).build())
                    .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                        .activityLevels(List.of(ActivityLevel.builder()
                            .year(Year.now()).subInstallationName(SubInstallationName.ALUMINIUM)
                            .changeType(ChangeType.NO_CHANGE).otherChangeTypeName(otherChangeTypeName)
                            .changedActivityLevel(changedActivityLevel).comments(comment5).build()))
                        .areConservativeEstimates(true)
                        .explainEstimates(explainEstimates)
                        .preliminaryAllocations(preliminaryAllocations)
                        .commentsForUkEtsAuthority(comment4)
                        .build())
                    .determination(DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY).reason(doalDeterminationReason)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                        .articleReasonItems(Set.of(ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5, ArticleReasonItemType.SETTING_ALLOCATION_UNDER_ARTICLE_3A))
                        .hasWithholdingOfAllowances(true)
                        .withholdingAllowancesNotice(WithholdingAllowancesNotice.builder().noticeIssuedDate(LocalDate.now())
                            .withholdingOfAllowancesComment(comment6).build())
                        .needsOfficialNotice(false)
                        .build()).build()
                ).build()).build();


        final RequestActionDTO expected = RequestActionDTO.builder()
            .id(11L)
            .type(RequestActionType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(DoalApplicationProceededToAuthorityRequestActionPayload.builder()
                .doalAttachments(Map.of(attachmentUUID, attachmentName))
                .doal(Doal.builder()
                    .operatorActivityLevelReport(OperatorActivityLevelReport.builder().document(documentUUID)
                        .areActivityLevelsEstimated(true).build())
                    .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                        .document(document2UUID).build())
                    // removed additional documents
                    .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                        .activityLevels(List.of(ActivityLevel.builder()
                            .year(Year.now()).subInstallationName(SubInstallationName.ALUMINIUM)
                            .changeType(ChangeType.NO_CHANGE).otherChangeTypeName(otherChangeTypeName)
                            .changedActivityLevel(changedActivityLevel).build()))
                        .areConservativeEstimates(true)
                        .explainEstimates(explainEstimates)
                        .preliminaryAllocations(preliminaryAllocations)
                        .build())
                    .determination(DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY).reason(doalDeterminationReason)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                        .articleReasonItems(Set.of(ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5, ArticleReasonItemType.SETTING_ALLOCATION_UNDER_ARTICLE_3A))
                        .hasWithholdingOfAllowances(true)
                        .withholdingAllowancesNotice(WithholdingAllowancesNotice.builder().noticeIssuedDate(LocalDate.now()).build())
                        .needsOfficialNotice(false)
                        .build()).build()
                ).build()).build();

        // Invoke
        final RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        Doal requestedActionDoal = ((DoalApplicationProceededToAuthorityRequestActionPayload) requestAction.getPayload())
            .getDoal();
        Doal actualDoal = ((DoalApplicationProceededToAuthorityRequestActionPayload) actual.getPayload())
            .getDoal();

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(requestedActionDoal.getAdditionalDocuments()).isNotNull();
        assertThat(actualDoal.getAdditionalDocuments()).isNull();
        assertThat(requestedActionDoal.getOperatorActivityLevelReport().getComment()).isNotNull();
        assertThat(actualDoal.getOperatorActivityLevelReport().getComment()).isNull();
        assertThat(requestedActionDoal.getVerificationReportOfTheActivityLevelReport().getComment()).isNotNull();
        assertThat(actualDoal.getVerificationReportOfTheActivityLevelReport().getComment()).isNull();
        assertThat(requestedActionDoal.getActivityLevelChangeInformation().getActivityLevels().get(0).getComments()).isNotNull();
        assertThat(actualDoal.getActivityLevelChangeInformation().getActivityLevels().get(0).getComments()).isNull();
        assertThat(((DoalProceedToAuthorityDetermination) requestedActionDoal.getDetermination()).getWithholdingAllowancesNotice().getWithholdingOfAllowancesComment()).isNotNull();
        assertThat(((DoalProceedToAuthorityDetermination) actualDoal.getDetermination()).getWithholdingAllowancesNotice().getWithholdingOfAllowancesComment()).isNull();

    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
