package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.repository.CaExternalContactRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CaExternalContactValidatorTest {

    @InjectMocks
    private CaExternalContactValidator caExternalContactValidator;

    @Mock
    private CaExternalContactRepository caExternalContactRepository;

    @Test
    void validateCaExternalContactRegistration() {
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndName(ca, name)).thenReturn(false);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCase(ca, email)).thenReturn(false);

        caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration);
    }

    @Test
    void validateCaExternalContactRegistration_name_already_exists() {
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndName(ca, name)).thenReturn(true);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCase(ca, email)).thenReturn(false);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS);
    }

    @Test
    void validateCaExternalContactRegistration_email_already_exists() {
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndName(ca, name)).thenReturn(false);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCase(ca, email)).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void validateCaExternalContactRegistration_name_email_already_exists() {
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndName(ca, name)).thenReturn(true);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCase(ca, email)).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void validateCaExternalContactRegistration_edit() {
        Long id = 1L;
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration);

        verify(caExternalContactRepository, times(1)).existsByCompetentAuthorityAndNameAndIdNot(ca, name, id);
        verify(caExternalContactRepository, times(1)).existsByCompetentAuthorityAndEmailIgnoreCaseAndIdNot(ca, email, id);
    }

    @Test
    void validateCaExternalContactRegistration_edit_name_already_exists() {
        Long id = 1L;
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndNameAndIdNot(ca, name, id)).thenReturn(true);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCaseAndIdNot(ca, email, id)).thenReturn(false);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_ALREADY_EXISTS);
    }

    @Test
    void validateCaExternalContactRegistration_edit_email_already_exists() {
        Long id = 1L;
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndNameAndIdNot(ca, name, id)).thenReturn(false);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCaseAndIdNot(ca, email, id)).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void validateCaExternalContactRegistration_edit_name_email_already_exists() {
        Long id = 1L;
        CompetentAuthorityEnum ca = ENGLAND;
        String name = "name";
        String email = "email";
        CaExternalContactRegistrationDTO caExternalContactRegistration =
            CaExternalContactRegistrationDTO.builder()
                .name(name)
                .email(email)
                .build();

        when(caExternalContactRepository.existsByCompetentAuthorityAndNameAndIdNot(ca, name, id)).thenReturn(true);
        when(caExternalContactRepository.existsByCompetentAuthorityAndEmailIgnoreCaseAndIdNot(ca, email, id)).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class, () ->
                caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.EXTERNAL_CONTACT_CA_NAME_EMAIL_ALREADY_EXISTS);
    }
}
