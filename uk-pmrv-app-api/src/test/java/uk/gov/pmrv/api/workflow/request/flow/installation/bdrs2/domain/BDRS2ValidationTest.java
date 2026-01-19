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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType.*;


@ExtendWith(MockitoExtension.class)
public class BDRS2ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private Set<ConstraintViolation<BDRS2>> validate(BDRS2 bdrs2) {
        return validator.validate(bdrs2, Default.class);
    }

    private BDRS2GuardQuestions guardQuestions(
            BDRS2ContinueApplicationForFreeAllocationType type,
            Boolean inEite,
            Boolean cbam
    ) {
        BDRS2GuardQuestions gq = new BDRS2GuardQuestions();
        gq.setContinueApplicationForFreeAllocationType(type);
        gq.setCovidAdjustments(true);
        gq.setInEiteSector(inEite);
        gq.setRequiresAdditionalSubInstallationSplitsForCbam(cbam);
        return gq;
    }

    private BDRS2Files files() {
        return BDRS2Files.builder().file(UUID.randomUUID()).build();
    }

    @Test
    void withdraw_application_with_files_is_invalid() {
        BDRS2 bdrs2 = new BDRS2();
        bdrs2.setBdrs2guardQuestions(
                guardQuestions(WITHDRAW, false, false)
        );
        bdrs2.setBdrs2Files(files());

        Set<ConstraintViolation<BDRS2>> violations = validate(bdrs2);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.files.bdrs2.required}");
    }

    @Test
    void non_withdraw_application_without_files_is_invalid() {
        BDRS2 bdrs2 = new BDRS2();
        bdrs2.setBdrs2guardQuestions(
                guardQuestions(CONTINUE_AS_MAIN_SCHEME_PARTICIPANT, false, false)
        );

        Set<ConstraintViolation<BDRS2>> violations = validate(bdrs2);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.files.bdrs2.required}");
    }


    @Test
    void eite_cbam_without_mmp_is_invalid() {
        BDRS2 bdrs2 = new BDRS2();
        bdrs2.setBdrs2guardQuestions(
                guardQuestions(
                        CONTINUE_AS_MAIN_SCHEME_PARTICIPANT,
                        true,
                        true
                )
        );
        bdrs2.setBdrs2Files(files());

        Set<ConstraintViolation<BDRS2>> violations = validate(bdrs2);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("{bdrs2.files.mmp.required}");
    }

    @Test
    void eite_cbam_with_mmp_is_valid() {
        BDRS2 bdrs2 = new BDRS2();
        bdrs2.setBdrs2guardQuestions(
                guardQuestions(
                        CONTINUE_AS_MAIN_SCHEME_PARTICIPANT,
                        true,
                        true
                )
        );
        bdrs2.setBdrs2Files(files());
        bdrs2.setMmpFiles(files());

        Set<ConstraintViolation<BDRS2>> violations = validate(bdrs2);

        assertThat(violations).isEmpty();
    }
}
