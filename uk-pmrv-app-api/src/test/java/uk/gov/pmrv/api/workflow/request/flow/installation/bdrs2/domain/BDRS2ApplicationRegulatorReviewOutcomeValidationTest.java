package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRS2ApplicationRegulatorReviewOutcomeValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> validate(
            BDRS2ApplicationRegulatorReviewOutcome outcome) {
        return validator.validate(outcome, Default.class);
    }

    private BDRS2ApplicationRegulatorReviewOutcome baseOutcome() {
        BDRS2ApplicationRegulatorReviewOutcome outcome =
                new BDRS2ApplicationRegulatorReviewOutcome();
        outcome.setFreeAllocationOpinion(
                BDRS2RegulatorReviewFreeAllocationOpinion.SENT_TO_AUTHORITY);
        return outcome;
    }

    /* -------------------------------------------------
       COVID ADJUSTMENTS NOTES
       ------------------------------------------------- */

    @Test
    void covid_notes_without_opinion_is_invalid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setCovidAdjustmentsReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.regulatorReview.covidAdjustments.notes.notAllowed}");
    }

    @Test
    void covid_notes_with_opinion_is_valid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setCovidAdjustmentsOpinion(
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.NO_ADJUSTMENTS);
        outcome.setCovidAdjustmentsReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations).isEmpty();
    }

    /* -------------------------------------------------
       INSTALLATION SECTOR NOTES
       ------------------------------------------------- */

    @Test
    void installation_notes_without_opinion_is_invalid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setInstallationSectorReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.regulatorReview.installationSector.notes.notAllowed}");
    }

    @Test
    void installation_notes_with_opinion_is_valid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setInstallationSectorOpinion(
                BDRS2RegulatorReviewInstallationSectorOpinion.CBAM_DOES_NOT_APPLY);
        outcome.setInstallationSectorReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations).isEmpty();
    }

    /* -------------------------------------------------
       CBAM SPLIT NOTES
       ------------------------------------------------- */

    @Test
    void cbam_notes_without_opinion_is_invalid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setCbamSplitReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.regulatorReview.cbamSplit.notes.notAllowed}");
    }

    @Test
    void cbam_notes_with_opinion_is_valid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setCbamSplitOpinion(
                BDRS2RegulatorReviewCbamSplitOpinion.SENT_TO_AUTHORITY);
        outcome.setCbamSplitReviewNotes(new BDRS2RegulatorReviewNotes());

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations).isEmpty();
    }

    /* -------------------------------------------------
       CONTROL: opinion without notes is valid
       ------------------------------------------------- */

    @Test
    void opinion_without_notes_is_valid() {
        BDRS2ApplicationRegulatorReviewOutcome outcome = baseOutcome();
        outcome.setCovidAdjustmentsOpinion(
                BDRS2RegulatorReviewCovidAdjustmentsOpinion.NO_ADJUSTMENTS);

        Set<ConstraintViolation<BDRS2ApplicationRegulatorReviewOutcome>> violations =
                validate(outcome);

        assertThat(violations).isEmpty();
    }
}
