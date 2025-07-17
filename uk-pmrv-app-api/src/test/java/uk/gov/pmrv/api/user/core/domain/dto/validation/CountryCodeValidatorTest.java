package uk.gov.pmrv.api.user.core.domain.dto.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CountryCodeValidatorTest {

    @InjectMocks
    private CountryCodeValidator countryCodeValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validPhoneNumber() {
        final String validCountryCode = "30";
        assertTrue(countryCodeValidator.isValid(validCountryCode, context));
    }

    @Test
    void notValidPhoneNumber() {
        final String invalidCountryCode = "300";
        assertFalse(countryCodeValidator.isValid(invalidCountryCode, context));
    }

    @Test
    void notValidNumberPhoneNumber() {
        final String invalidCountryCode = "none";
        assertFalse(countryCodeValidator.isValid(invalidCountryCode, context));
    }
}
