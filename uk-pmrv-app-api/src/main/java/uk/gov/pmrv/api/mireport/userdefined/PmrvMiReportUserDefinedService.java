package uk.gov.pmrv.api.mireport.userdefined;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedEntity;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedMapper;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedRepository;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResults;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.QuerySearchUtils;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;


@Service
@RequiredArgsConstructor
public class PmrvMiReportUserDefinedService {

    private final MiReportUserDefinedService miReportUserDefinedService;
    private final MiReportUserDefinedRepository miReportUserDefinedRepository;
    private final MiReportUserDefinedMapper miReportUserDefinedMapper;
    private final PmrvMiReportUserDefinedAccountTypeRepository pmrvMiReportUserDefinedAccountTypeRepository;
    private final PmrvMiReportUserDefinedRepository pmrvMiReportUserDefinedRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;


    @Transactional
    public void create(AppUser appUser, AccountType type,
                       MiReportUserDefinedDTO miReportUserDefinedDTO) {

        miReportUserDefinedService.create(appUser, miReportUserDefinedDTO);

        Long id = miReportUserDefinedRepository
                .findIdByReportNameAndCA(miReportUserDefinedDTO.getReportName(), appUser.getCompetentAuthority())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        pmrvMiReportUserDefinedAccountTypeRepository.save(MiReportUserDefinedAccountType.builder().miReportId(id).accountType(type).build());
    }

    @Transactional(readOnly = true)
    public MiReportUserDefinedResults findAllByCA(AppUser appUser, AccountType accountType,
                                                  int page, int size, Long categoryId, String term, boolean favourites) {
        Page<MiReportUserDefinedEntity> result = pmrvMiReportUserDefinedRepository.findAllByCompetentAuthorityAndFilters(
                appUser.getCompetentAuthority(), accountType, categoryId, QuerySearchUtils.toSearchPattern(term),
                favourites ? appUser.getUserId() : null,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdatedOn")));

        return result.isEmpty()
                ? MiReportUserDefinedResults.emptyMiReportUserDefinedResults()
                : MiReportUserDefinedResults.builder()
                .queries(result.getContent().stream()
                        .map(miReportUserDefinedMapper::toMiReportUserDefinedInfoDTO)
                        .toList())
                .total(result.getTotalElements())
                .build();
    }

    public boolean canManageCustomReports(AppUser appUser) {
        return compAuthAuthorizationResourceService
                .hasUserScopeToCompAuth(appUser, Scope.MANAGE_MI_REPORT_USER_DEFINED);
    }

}
