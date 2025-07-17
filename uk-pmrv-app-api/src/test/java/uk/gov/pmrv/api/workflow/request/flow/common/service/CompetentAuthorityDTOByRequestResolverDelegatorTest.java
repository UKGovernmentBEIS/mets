package uk.gov.pmrv.api.workflow.request.flow.common.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompetentAuthorityDTOByRequestResolverDelegatorTest {


    @InjectMocks
    private CompetentAuthorityDTOByRequestResolverDelegator delegator;

    @Mock
    private InstallationAccountCompetentAuthorityDTOByRequestResolver installationAccountCompetentAuthorityDTOByRequestResolver;

    @Spy
    private ArrayList<CompetentAuthorityDTOByRequestResolver> competentAuthorityDTOByRequestResolvers;

    @BeforeEach
    public void setUp() {
        competentAuthorityDTOByRequestResolvers.add(installationAccountCompetentAuthorityDTOByRequestResolver);
    }

    @Test
    void getResolverByAccountType() {
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();
        Request request = Request
                .builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        when(installationAccountCompetentAuthorityDTOByRequestResolver.getAccountType()).thenReturn(AccountType.INSTALLATION);
        when(installationAccountCompetentAuthorityDTOByRequestResolver.resolveCA(request)).thenReturn(ca);

        assertThat(delegator.resolveCA(request, AccountType.INSTALLATION)).isEqualTo(ca);
    }

    @Test
    void getResolverByAccountType_resolver_does_not_exist_throw_business_exception() {
        Request request = Request
                .builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> delegator.resolveCA(request, AccountType.AVIATION));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
