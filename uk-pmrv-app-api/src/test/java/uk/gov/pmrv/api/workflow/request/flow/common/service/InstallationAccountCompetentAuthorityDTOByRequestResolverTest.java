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
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthority;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityMapper;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityRepository;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountCompetentAuthorityDTOByRequestResolverTest {

    @InjectMocks
    private InstallationAccountCompetentAuthorityDTOByRequestResolver resolver;

    @Mock
	private PmrvCompetentAuthorityRepository pmrvCompetentAuthorityRepository;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private PmrvCompetentAuthorityMapper pmrvCompetentAuthorityMapper;

    @Test
    void resolveCA_permit_issuance_permit_type_waste_use_correct_ca_email() {

        PmrvCompetentAuthority ca = PmrvCompetentAuthority
                .builder()
                .id(CompetentAuthorityEnum.ENGLAND)
                .wasteEmail("waste@email.com")
                .email("test@email.com")
                .name("ENGLAND")
                .build();

        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload
                .builder()
                .permitType(PermitType.WASTE)
                .build();

        CompetentAuthorityDTO expected = CompetentAuthorityDTO
                .builder()
                .name("ENGLAND")
                .id(CompetentAuthorityEnum.ENGLAND)
                .email("waste@email.com")
                .build();

        Request request = Request
                .builder()
                .type(RequestType.PERMIT_ISSUANCE)
                .payload(requestPayload)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.of(ca));
        when(pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, "waste@email.com")).thenReturn(expected);


        CompetentAuthorityDTO result = resolver.resolveCA(request);

        assertThat(result).isEqualTo(expected);


        verifyNoInteractions(installationAccountQueryService);
        verify(pmrvCompetentAuthorityRepository, times(1)).findById(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void resolveCA_permit_issuance_permit_type_not_waste_use_correct_ca_email() {

        PmrvCompetentAuthority ca = PmrvCompetentAuthority
                .builder()
                .id(CompetentAuthorityEnum.ENGLAND)
                .wasteEmail("waste@email.com")
                .email("test@email.com")
                .name("ENGLAND")
                .build();

        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload
                .builder()
                .permitType(PermitType.GHGE)
                .build();

        CompetentAuthorityDTO expected = CompetentAuthorityDTO
                .builder()
                .name("ENGLAND")
                .id(CompetentAuthorityEnum.ENGLAND)
                .email("test@email.com")
                .build();

        Request request = Request
                .builder()
                .type(RequestType.PERMIT_ISSUANCE)
                .payload(requestPayload)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.of(ca));
        when(pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, "test@email.com")).thenReturn(expected);

        CompetentAuthorityDTO result = resolver.resolveCA(request);

        assertThat(result).isEqualTo(expected);

        verifyNoInteractions(installationAccountQueryService);
        verify(pmrvCompetentAuthorityRepository, times(1)).findById(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void resolveCA_not_permit_issuance_emitter_type_waste_use_correct_ca_email() {

        InstallationAccountDTO account = InstallationAccountDTO
                .builder()
                .emitterType(EmitterType.WASTE)
                .build();

        PmrvCompetentAuthority ca = PmrvCompetentAuthority
                .builder()
                .id(CompetentAuthorityEnum.ENGLAND)
                .wasteEmail("waste@email.com")
                .email("test@email.com")
                .name("ENGLAND")
                .build();

        CompetentAuthorityDTO expected = CompetentAuthorityDTO
                .builder()
                .name("ENGLAND")
                .id(CompetentAuthorityEnum.ENGLAND)
                .email("waste@email.com")
                .build();

        Request request = Request
                .builder()
                .accountId(1L)
                .type(RequestType.PERMIT_NOTIFICATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(account);
        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.of(ca));
        when(pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, "waste@email.com")).thenReturn(expected);

        CompetentAuthorityDTO result = resolver.resolveCA(request);

        assertThat(result).isEqualTo(expected);

        verify(installationAccountQueryService, times(1)).getAccountDTOById(1L);
        verify(pmrvCompetentAuthorityRepository, times(1)).findById(CompetentAuthorityEnum.ENGLAND);
    }

    @Test
    void resolveCA_not_permit_issuance_emitter_type_not_waste_use_correct_ca_email() {

        InstallationAccountDTO account = InstallationAccountDTO
                .builder()
                .emitterType(EmitterType.GHGE)
                .build();

        PmrvCompetentAuthority ca = PmrvCompetentAuthority
                .builder()
                .id(CompetentAuthorityEnum.ENGLAND)
                .wasteEmail("waste@email.com")
                .email("test@email.com")
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
                .accountId(1L)
                .type(RequestType.PERMIT_NOTIFICATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(account);
        when(pmrvCompetentAuthorityRepository.findById(CompetentAuthorityEnum.ENGLAND)).thenReturn(Optional.of(ca));
        when(pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, "test@email.com")).thenReturn(expected);

        CompetentAuthorityDTO result = resolver.resolveCA(request);

        assertThat(result).isEqualTo(expected);

        verify(installationAccountQueryService, times(1)).getAccountDTOById(1L);
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
        assertThat(resolver.getAccountType()).isEqualTo(AccountType.INSTALLATION);
    }
}
