package uk.gov.pmrv.api.account.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalEntityValidatorTest {

    @InjectMocks
    private LegalEntityValidator legalEntityValidator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;

    @Mock
    private NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    @Mock
    private LeafNodeBuilderCustomizableContext leafNodeBuilderCustomizableContext;

    @Test
    void isValid_valid_ref_number() {
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO(
            "09546038",
            null,
            null
        );
        assertTrue(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, never()).buildConstraintViolationWithTemplate(Mockito.anyString());
    }

    @Test
    void isValid_valid_no_ref_number_reason() {
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO(null, "no ref number reason", null);
        assertTrue(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, never()).buildConstraintViolationWithTemplate(Mockito.anyString());
    }

    @Test
    void isValid_invalid_ref_number_greater_than_max_allowed_length() {
        int invalid_ref_number_length = 16;
        String aLargeRefNum = new String(new char[invalid_ref_number_length]).replace('\0', 'a');
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO(aLargeRefNum, null, null);
        mockConstraintViolationActions("{legalEntity.referenceNumber.typeMismatch}", "referenceNumber");
        assertFalse(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate("{legalEntity.referenceNumber.typeMismatch}");
        verify(constraintViolationBuilder, times(1)).addPropertyNode("referenceNumber");
    }

    @Test
    void isValid_invalid_no_ref_number_no_ref_number_reason() {
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO(null, null, null);
        mockConstraintViolationActions("{legalEntity.referenceNumber.noReferenceNumberReason}", "noReferenceNumberReason");
        assertFalse(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate("{legalEntity.referenceNumber.noReferenceNumberReason}");
        verify(constraintViolationBuilder, times(1)).addPropertyNode("noReferenceNumberReason");
    }

    @Test
    void isValid_invalid_ref_number_and_ref_number_reason() {
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO("09546038", "no ref number reason", null);
        mockConstraintViolationActions("{legalEntity.referenceNumber.noReferenceNumberReason}", "referenceNumber");
        assertFalse(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate("{legalEntity.referenceNumber.noReferenceNumberReason}");
        verify(constraintViolationBuilder, times(1)).addPropertyNode("referenceNumber");
    }

    @Test
    void isValid_ref_number_reason_greater_than_max_allowed_length() {
        int invalid_ref_number_reason_length = 501;
        String aLargeString = new String(new char[invalid_ref_number_reason_length]).replace('\0', 'a');
        LegalEntityDTO legalEntityDTO = buildLegalEntityDTO(null, aLargeString, null);
        mockConstraintViolationActions("{legalEntity.noReferenceNumberReason.typeMismatch}", "noReferenceNumberReason");
        assertFalse(legalEntityValidator.isValid(legalEntityDTO, constraintValidatorContext));
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate("{legalEntity.noReferenceNumberReason.typeMismatch}");
        verify(constraintViolationBuilder, times(1)).addPropertyNode("noReferenceNumberReason");
    }

    private void mockConstraintViolationActions(String messageTemplate, String fieldName) {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate))
            .thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(fieldName))
            .thenReturn(nodeBuilderCustomizableContext);
        when(nodeBuilderCustomizableContext.addBeanNode()).thenReturn(leafNodeBuilderCustomizableContext);
    }

    private LegalEntityDTO buildLegalEntityDTO(
        String refNumber,
        String noRefNumberReason,
        HoldingCompanyDTO holdingCompanyDTO
    ) {
        return LegalEntityDTO.builder()
            .type(LegalEntityType.PARTNERSHIP)
            .name("name")
            .referenceNumber(refNumber)
            .noReferenceNumberReason(noRefNumberReason)
            .address(AddressDTO.builder()
                .line1("line1")
                .line2("line2")
                .city("city")
                .country("GR")
                .postcode("postcode").build())
            .holdingCompany(holdingCompanyDTO)
            .build();
    }
}
