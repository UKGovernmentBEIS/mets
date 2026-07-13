package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.validation.AllowanceAllocationValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.OperatorActivityLevelReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.VerificationReportOfTheActivityLevelReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.time.Year;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalSubmitValidatorTest {

    @InjectMocks
    private DoalSubmitValidator doalSubmitValidator;

    @Mock
    private DoalAttachmentsValidator attachmentsValidator;

    @Mock
    private AllowanceAllocationValidator allowanceAllocationValidator;

    static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory f = Validation.buildDefaultValidatorFactory()) {
            validator = f.getValidator();
        }
    }

    @Test
    void validate() {
        final UUID attachment = UUID.randomUUID();
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2022))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2022))
                        .allowances(20)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2023))
                        .allowances(400)
                        .build()
        );
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .document(attachment)
                                        .build())
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                                        .build()
                                )
                                .determination(DoalProceedToAuthorityDetermination.builder()
                                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_34H_REASONS)
                                        .articleReasonItems(Set.of(
                                                ArticleReasonItemType.ERROR_IN_NEW_ENTRANT_DATA_REPORT,
                                                ArticleReasonItemType.ERROR_IN_ACTIVITY_LEVEL_REPORT
                                        ))
                                        .build())
                                .build())
                        .build();

        when(allowanceAllocationValidator.isValid(preliminaryAllocations)).thenReturn(true);
        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(eq(Set.of(attachment)), anySet())).thenReturn(true);

        // Invoke
        doalSubmitValidator.validate(payload);

        // Verify
        verify(allowanceAllocationValidator, times(1)).isValid(preliminaryAllocations);
        verify(attachmentsValidator, times(1)).attachmentsExist(Set.of(attachment));
        verify(attachmentsValidator, times(1)).attachmentsReferenced(eq(Set.of(attachment)), anySet());
    }

    @Test
    void validate_not_valid_allocations() {
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2022))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2023))
                        .allowances(20)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2023))
                        .allowances(400)
                        .build()
        );
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                                        .build()
                                )
                                .build())
                        .build();

        when(allowanceAllocationValidator.isValid(preliminaryAllocations)).thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> doalSubmitValidator.validate(payload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
        verify(allowanceAllocationValidator, times(1)).isValid(preliminaryAllocations);
        verifyNoInteractions(attachmentsValidator);
    }

    @Test
    void validate_no_allocations() {
        final UUID attachment = UUID.randomUUID();
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .document(attachment)
                                        .build())
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>())
                                        .build()
                                )
                                .determination(DoalProceedToAuthorityDetermination.builder()
                                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_34H_REASONS)
                                        .articleReasonItems(Set.of(
                                                ArticleReasonItemType.ERROR_IN_NEW_ENTRANT_DATA_REPORT,
                                                ArticleReasonItemType.ERROR_IN_ACTIVITY_LEVEL_REPORT
                                        ))
                                        .build())
                                .build())
                        .build();

        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(eq(Set.of(attachment)), anySet())).thenReturn(true);

        // Invoke
        doalSubmitValidator.validate(payload);

        // Verify
        verify(attachmentsValidator, times(1)).attachmentsExist(Set.of(attachment));
        verify(attachmentsValidator, times(1)).attachmentsReferenced(eq(Set.of(attachment)), anySet());
        verifyNoInteractions(allowanceAllocationValidator);
    }

    @Test
    void validate_whenArticleReasonsNotValid_thenException() {
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>())
                                        .build()
                                )
                                .determination(DoalProceedToAuthorityDetermination.builder()
                                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_34H_REASONS)
                                        .articleReasonItems(Set.of(
                                                ArticleReasonItemType.ERROR_IN_NEW_ENTRANT_DATA_REPORT,
                                                ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5
                                        ))
                                        .build())
                                .build())
                        .build();

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> doalSubmitValidator.validate(payload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
        verifyNoInteractions(attachmentsValidator, allowanceAllocationValidator);
    }

    @Test
    void validate_whenAttachmentsMissing_thenException() {
        final UUID attachment = UUID.randomUUID();
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>())
                                        .build()
                                )
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .document(attachment)
                                        .build())
                                .determination(DoalClosedDetermination.builder()
                                        .type(DoalDeterminationType.CLOSED)
                                        .build())
                                .build())
                        .build();

        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> doalSubmitValidator.validate(payload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
        verify(attachmentsValidator, times(1)).attachmentsExist(Set.of(attachment));
        verify(attachmentsValidator, never()).attachmentsReferenced(anySet(), anySet());
        verifyNoInteractions(allowanceAllocationValidator);
    }

    @Test
    void validate_whenAttachmentsNotReferenced_thenException() {
        final UUID attachment = UUID.randomUUID();
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder()
                                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>())
                                        .build()
                                )
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .document(attachment)
                                        .build())
                                .determination(DoalClosedDetermination.builder()
                                        .type(DoalDeterminationType.CLOSED)
                                        .build())
                                .build())
                        .build();

        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(eq(Set.of(attachment)), anySet())).thenReturn(false);

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> doalSubmitValidator.validate(payload));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
        verify(attachmentsValidator, times(1)).attachmentsExist(Set.of(attachment));
        verify(attachmentsValidator, times(1)).attachmentsReferenced(eq(Set.of(attachment)), anySet());
        verifyNoInteractions(allowanceAllocationValidator);
    }

    @Test
    void validate_whenNoDocumentsExistsActivityLevelReports() {
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder().activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>()).build())
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .areActivityLevelsEstimated(false).comment("test").build())
                                .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                                        .comment("test").build())
                                .additionalDocuments(DoalAdditionalDocuments.builder().exist(false).build())
                                .determination(DoalProceedToAuthorityDetermination.builder()
                                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY).reason("test")
                                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                                        .articleReasonItems(Set.of(ArticleReasonItemType.TEMPORARY_CESSATION))
                                        .hasWithholdingOfAllowances(false)
                                        .needsOfficialNotice(false)
                                        .build())
                                .build()).build();

        Set<ConstraintViolation<Doal>> violations = validator.validate(payload.getDoal());
        when(attachmentsValidator.attachmentsExist(Collections.emptySet())).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(Collections.emptySet(),Collections.emptySet())).thenReturn(true);

        assertEquals(0, violations.size());
        doalSubmitValidator.validate(payload);

    }

    @Test
    void validate_whenDocumentsExistsActivityLevelReports() {
        final UUID attachment = UUID.randomUUID();
        final DoalApplicationSubmitRequestTaskPayload payload =
                DoalApplicationSubmitRequestTaskPayload.builder()
                        .doal(Doal.builder().activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                        .preliminaryAllocations(new TreeSet<>()).build())
                                .operatorActivityLevelReport(OperatorActivityLevelReport.builder()
                                        .areActivityLevelsEstimated(false).comment("test").document(attachment).build())
                                .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                                        .comment("test").document(attachment).build())
                                .additionalDocuments(DoalAdditionalDocuments.builder().exist(false).build())
                                .determination(DoalProceedToAuthorityDetermination.builder()
                                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY).reason("test")
                                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                                        .articleReasonItems(Set.of(ArticleReasonItemType.TEMPORARY_CESSATION))
                                        .hasWithholdingOfAllowances(false)
                                        .needsOfficialNotice(false)
                                        .build())
                                .build()).build();

        Set<ConstraintViolation<Doal>> violations = validator.validate(payload.getDoal());
        when(attachmentsValidator.attachmentsExist(any())).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(any(),any())).thenReturn(true);

        assertEquals(0, violations.size());
        doalSubmitValidator.validate(payload);
    }

    @Test
    void validate_whenArticle5ReasonsAndArticleReasonItemsExist_thenViolation() {
        final DoalProceedToAuthorityDetermination determination =
                DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_5_REASONS)
                        .articleReasonItems(Set.of(
                                ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5
                        ))
                        .reason("test")
                        .hasWithholdingOfAllowances(false)
                        .needsOfficialNotice(false)
                        .build();

        Set<ConstraintViolation<DoalProceedToAuthorityDetermination>> violations =
                validator.validate(determination);

        assertTrue(
                violations.stream()
                        .anyMatch(v -> "{doal.determination.articleReasonItems}"
                                .equals(v.getMessage()))
        );
    }

    @Test
    void validate_whenArticle34HReasonsAndArticleReasonItemsEmpty_thenViolation() {
        final DoalProceedToAuthorityDetermination determination =
                DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_34H_REASONS)
                        .articleReasonItems(Collections.emptySet())
                        .reason("test")
                        .hasWithholdingOfAllowances(false)
                        .needsOfficialNotice(false)
                        .build();

        Set<ConstraintViolation<DoalProceedToAuthorityDetermination>> violations =
                validator.validate(determination);

        assertTrue(
                violations.stream()
                        .anyMatch(v -> "{doal.determination.articleReasonItems}"
                                .equals(v.getMessage()))
        );
    }

    @Test
    void validate_whenArticle5ReasonsAndArticleReasonItemsEmpty_thenNoViolation() {
        final DoalProceedToAuthorityDetermination determination =
                DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_5_REASONS)
                        .articleReasonItems(Collections.emptySet())
                        .reason("test")
                        .hasWithholdingOfAllowances(false)
                        .needsOfficialNotice(false)
                        .build();

        Set<ConstraintViolation<DoalProceedToAuthorityDetermination>> violations =
                validator.validate(determination);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_whenArticle6AReasonsAndArticleReasonItemsExist_thenNoViolation() {
        final DoalProceedToAuthorityDetermination determination =
                DoalProceedToAuthorityDetermination.builder()
                        .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                        .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                        .articleReasonItems(Set.of(
                                ArticleReasonItemType.TEMPORARY_CESSATION
                        ))
                        .reason("test")
                        .hasWithholdingOfAllowances(false)
                        .needsOfficialNotice(false)
                        .build();

        Set<ConstraintViolation<DoalProceedToAuthorityDetermination>> violations =
                validator.validate(determination);

        assertEquals(0, violations.size());
    }
}
