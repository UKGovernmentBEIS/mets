package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalEntityValidationServiceTest {

    @InjectMocks
    private LegalEntityValidationService service;

    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Test
    void validateNameExistenceInOtherActiveLegalEntities_exists() {
        Long id = 1L;
        String name = "name";

        when(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.validateNameExistenceInOtherActiveLegalEntities(name, id));
        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);

        verify(legalEntityRepository, times(1))
            .existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id);
    }

    @Test
    void validateNameExistenceInOtherActiveLegalEntities_not_exists() {
        Long id = 1L;
        String name = "name";

        when(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id)).thenReturn(false);

        service.validateNameExistenceInOtherActiveLegalEntities(name, id);

        verify(legalEntityRepository, times(1))
            .existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id);
    }

    @Test
    void isExistingActiveLegalEntity_regulator() {
        final AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final String leName = "lename";
        when(legalEntityRepository.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE)).thenReturn(true);

        //invoke
        boolean result = service.isExistingActiveLegalEntityName(leName, appUser);

        //assert
        assertThat(result).isTrue();

        //verify mocks
        verify(legalEntityRepository, times(1)).existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
        verify(legalEntityRepository, never()).existsActiveLegalEntityNameInAnyOfAccounts(anyString(), Mockito.anySet());
    }

    @Test
    void isExistingActiveLegalEntity_operator() {
        AppUser appUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.OPERATOR)
                .authorities(List.of(
                        AppAuthority.builder().accountId(1L).build()
                )).build();
        final String leName = "lename";
        when(legalEntityRepository.existsActiveLegalEntityNameInAnyOfAccounts(leName, appUser.getAccounts())).thenReturn(true);

        //invoke
        boolean result = service.isExistingActiveLegalEntityName(leName, appUser);

        //assert
        assertThat(result).isTrue();

        //verify mocks
        verify(legalEntityRepository, never()).existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
        verify(legalEntityRepository, times(1)).existsActiveLegalEntityNameInAnyOfAccounts(leName, appUser.getAccounts());
    }
}