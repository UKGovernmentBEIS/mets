package uk.gov.pmrv.api.mireport.userdefined;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedEntity;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedInfoDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedMapper;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedRepository;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResults;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmrvMiReportUserDefinedServiceTest {

    private static final String USER_ID = "USER_ID";
    private static final CompetentAuthorityEnum CA = CompetentAuthorityEnum.ENGLAND;

    @InjectMocks
    private PmrvMiReportUserDefinedService service;

    @Mock
    private MiReportUserDefinedService miReportUserDefinedService;

    @Mock
    private MiReportUserDefinedRepository miReportUserDefinedRepository;

    @Mock
    private MiReportUserDefinedMapper miReportUserDefinedMapper;

    @Mock
    private PmrvMiReportUserDefinedAccountTypeRepository pmrvMiReportUserDefinedAccountTypeRepository;

    @Mock
    private PmrvMiReportUserDefinedRepository pmrvMiReportUserDefinedRepository;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Captor
    private ArgumentCaptor<MiReportUserDefinedAccountType> accountTypeCaptor;

    @Captor
    private ArgumentCaptor<String> termCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    void create() {
        MiReportUserDefinedDTO dto = MiReportUserDefinedDTO.builder()
                .reportName("My report")
                .queryDefinition("select 1")
                .build();
        Long reportId = 42L;

        when(miReportUserDefinedRepository.findIdByReportNameAndCA("My report", CA))
                .thenReturn(Optional.of(reportId));

        AppUser appUser = AppUser.builder().authorities(List.of(AppAuthority.builder().competentAuthority(CA).build())).build();
        service.create(appUser, AccountType.AVIATION, dto);

        verify(miReportUserDefinedService, times(1)).create(appUser, dto);
        verify(pmrvMiReportUserDefinedAccountTypeRepository, times(1)).save(accountTypeCaptor.capture());

        MiReportUserDefinedAccountType saved = accountTypeCaptor.getValue();
        assertThat(saved.getMiReportId()).isEqualTo(reportId);
        assertThat(saved.getAccountType()).isEqualTo(AccountType.AVIATION);
    }

    @Test
    void create_reportIdNotFound() {
        MiReportUserDefinedDTO dto = MiReportUserDefinedDTO.builder()
                .reportName("My report")
                .queryDefinition("select 1")
                .build();

        when(miReportUserDefinedRepository.findIdByReportNameAndCA("My report", CA))
                .thenReturn(Optional.empty());

        AppUser appUser = AppUser.builder().authorities(List.of(AppAuthority.builder().competentAuthority(CA).build())).build();
        assertThatThrownBy(() -> service.create(appUser, AccountType.INSTALLATION, dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);

        verify(miReportUserDefinedService, times(1)).create(appUser, dto);
        verifyNoInteractions(pmrvMiReportUserDefinedAccountTypeRepository);
    }

    @Test
    void findAllByCA() {
        int page = 0;
        int size = 10;
        Long categoryId = 5L;
        final AppUser appUser = getAppUser();

        MiReportUserDefinedEntity entity = MiReportUserDefinedEntity.builder()
                .id(1L)
                .reportName("My report")
                .build();
        MiReportUserDefinedInfoDTO infoDTO = MiReportUserDefinedInfoDTO.builder()
                .id(1L)
                .reportName("My report")
                .build();
        Page<MiReportUserDefinedEntity> resultPage = new PageImpl<>(List.of(entity),
                PageRequest.of(page, size), 1L);

        when(pmrvMiReportUserDefinedRepository.findAllByCompetentAuthorityAndFilters(
                eq(CA), eq(AccountType.INSTALLATION), eq(categoryId), any(), any(), any(Pageable.class)))
                .thenReturn(resultPage);
        when(miReportUserDefinedMapper.toMiReportUserDefinedInfoDTO(entity)).thenReturn(infoDTO);

        MiReportUserDefinedResults results =
                service.findAllByCA(appUser, AccountType.INSTALLATION, page, size, categoryId, "Test", false);

        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getQueries()).containsExactly(infoDTO);

        verify(pmrvMiReportUserDefinedRepository, times(1)).findAllByCompetentAuthorityAndFilters(
                eq(CA), eq(AccountType.INSTALLATION), eq(categoryId), termCaptor.capture(), isNull(), pageableCaptor.capture());
        verify(miReportUserDefinedMapper, times(1)).toMiReportUserDefinedInfoDTO(entity);

        assertThat(termCaptor.getValue()).isEqualTo("%test%");

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(page);
        assertThat(pageable.getPageSize()).isEqualTo(size);
        assertThat(pageable.getSort().getOrderFor("lastUpdatedOn")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("lastUpdatedOn").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void findAllByCA_nullTerm() {
        final AppUser appUser = getAppUser();


        Page<MiReportUserDefinedEntity> emptyPage = new PageImpl<>(List.of());

        when(pmrvMiReportUserDefinedRepository.findAllByCompetentAuthorityAndFilters(
                eq(CA), eq(AccountType.INSTALLATION), eq(null), termCaptor.capture(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        MiReportUserDefinedResults results =
                service.findAllByCA(appUser, AccountType.INSTALLATION, 0, 10, null, null, false);

        assertThat(results.getTotal()).isZero();
        assertThat(results.getQueries()).isEmpty();
        assertThat(termCaptor.getValue()).isNull();
        verify(miReportUserDefinedMapper, never()).toMiReportUserDefinedInfoDTO(any());
    }

    @Test
    void findAllByCA_emptyResult() {
        final AppUser appUser = getAppUser();

        Page<MiReportUserDefinedEntity> emptyPage = new PageImpl<>(List.of());

        when(pmrvMiReportUserDefinedRepository.findAllByCompetentAuthorityAndFilters(
                eq(CA), eq(AccountType.AVIATION), eq(null), any(), isNull(), any(Pageable.class)))
                .thenReturn(emptyPage);

        MiReportUserDefinedResults results =
                service.findAllByCA(appUser, AccountType.AVIATION, 0, 10, null, "term", false);

        assertThat(results.getTotal()).isZero();
        assertThat(results.getQueries()).isEmpty();
        verify(miReportUserDefinedMapper, never()).toMiReportUserDefinedInfoDTO(any());
    }

    @Test
    void canManageCustomReports_true() {
        AppUser appUser = getAppUser();
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.MANAGE_MI_REPORT_USER_DEFINED))
                .thenReturn(true);

        assertThat(service.canManageCustomReports(appUser)).isTrue();

        verify(compAuthAuthorizationResourceService, times(1))
                .hasUserScopeToCompAuth(appUser, Scope.MANAGE_MI_REPORT_USER_DEFINED);
    }

    @Test
    void canManageCustomReports_false() {
        AppUser appUser = getAppUser();
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.MANAGE_MI_REPORT_USER_DEFINED))
                .thenReturn(false);

        assertThat(service.canManageCustomReports(appUser)).isFalse();

        verify(compAuthAuthorizationResourceService, times(1))
                .hasUserScopeToCompAuth(appUser, Scope.MANAGE_MI_REPORT_USER_DEFINED);
    }

    private AppUser getAppUser() {
        AppAuthority authority = AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build();

        AppUser appUser = new AppUser();
        appUser.setFirstName("firstName");
        appUser.setLastName("lastName");
        appUser.setUserId("test user id");
        appUser.setAuthorities(List.of(authority));
        return appUser;
    }
}
