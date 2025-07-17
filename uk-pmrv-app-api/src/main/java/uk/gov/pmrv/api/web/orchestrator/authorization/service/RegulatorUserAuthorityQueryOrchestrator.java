package uk.gov.pmrv.api.web.orchestrator.authorization.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.RegulatorUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.transform.RegulatorUserAuthorityInfoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorUserAuthorityQueryOrchestrator {

    private final RegulatorAuthorityQueryService regulatorAuthorityQueryService;
    private final RegulatorUserInfoService regulatorUserInfoService;
    private final RegulatorUserAuthorityInfoMapper regulatorUserAuthorityInfoMapper = Mappers.getMapper(RegulatorUserAuthorityInfoMapper.class);

    public RegulatorUsersAuthoritiesInfoDTO getCaUsersAuthoritiesInfo(AppUser appUser) {
        UserAuthoritiesDTO caAuthorities = regulatorAuthorityQueryService.getCaAuthorities(appUser);
        List<String> userIds = caAuthorities.getAuthorities().stream().map(UserAuthorityDTO::getUserId).collect(Collectors.toList());

        List<RegulatorUserInfoDTO> regulatorAuthorityUsersInfo =
                regulatorUserInfoService.getRegulatorUsersInfo(appUser, userIds);

        return getRegulatorUsersAuthoritiesInfo(caAuthorities, regulatorAuthorityUsersInfo);
    }

    private RegulatorUsersAuthoritiesInfoDTO getRegulatorUsersAuthoritiesInfo(UserAuthoritiesDTO regulatorUserAuthorities,
                                                                              List<RegulatorUserInfoDTO> regulatorUsersInfo) {

        List<RegulatorUserAuthorityInfoDTO> caUsers =
                regulatorUserAuthorities.getAuthorities().stream()
                        .map(authority ->
                                regulatorUserAuthorityInfoMapper.toUserAuthorityInfo(
                                        authority,
                                        regulatorUsersInfo.stream()
                                                .filter(info -> info.getId().equals(authority.getUserId()))
                                                .findFirst()
                                                .orElse(new RegulatorUserInfoDTO())))
                        .collect(Collectors.toList());

        return RegulatorUsersAuthoritiesInfoDTO.builder()
                .caUsers(caUsers)
                .editable(regulatorUserAuthorities.isEditable())
                .build();
    }
}
