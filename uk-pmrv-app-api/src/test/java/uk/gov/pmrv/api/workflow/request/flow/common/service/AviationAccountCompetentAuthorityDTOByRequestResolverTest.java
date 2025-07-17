package uk.gov.pmrv.api.workflow.request.flow.common.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthority;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityMapper;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAccountCompetentAuthorityDTOByRequestResolverTest {

    @InjectMocks
    private AviationAccountCompetentAuthorityDTOByRequestResolver resolver;

    @Mock
    private PmrvCompetentAuthorityMapper pmrvCompetentAuthorityMapper;

    @Mock
	private PmrvCompetentAuthorityRepository pmrvCompetentAuthorityRepository;

    @Test
    void resolveCA() {

        PmrvCompetentAuthority ca = PmrvCompetentAuthority
                .builder()
                .id(CompetentAuthorityEnum.ENGLAND)
                .aviationEmail("test@email.com")
                .name("ENGLAND")
                .build();

        CompetentAuthorityDTO expected = CompetentAuthorityDTO
                .builder()
                 .name("ENGLAND")
                .id(CompetentAuthorityEnum.ENGLAND)
                .email("test@email.com")
                .build();

        Request request = Request
                .builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.of(ca));
        when(pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, "test@email.com")).thenReturn(expected);

        CompetentAuthorityDTO result = resolver.resolveCA(request);

        assertThat(result).isEqualTo(expected);

        verify(pmrvCompetentAuthorityRepository, times(1)).findById(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void resolveCA_ca_does_not_exist_throw_business_exception() {

        Request request = Request
                .builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.empty());


        BusinessException ex = assertThrows(BusinessException.class, () -> resolver.resolveCA(request));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

    }


    @Test
    void getAccountType() {
        assertThat(resolver.getAccountType()).isEqualTo(AccountType.AVIATION);
    }


}
