package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthority;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityMapper;
import uk.gov.pmrv.api.competentauthority.PmrvCompetentAuthorityRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class AviationAccountCompetentAuthorityDTOByRequestResolver implements CompetentAuthorityDTOByRequestResolver {

	private final PmrvCompetentAuthorityMapper pmrvCompetentAuthorityMapper;
	private final PmrvCompetentAuthorityRepository pmrvCompetentAuthorityRepository;
	
	@Override
	public CompetentAuthorityDTO resolveCA(Request request) {
		final PmrvCompetentAuthority ca = getPmrvCompetentAuthority(request.getCompetentAuthority());
		return pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca, ca.getAviationEmail());
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.AVIATION;
	}
	
	private PmrvCompetentAuthority getPmrvCompetentAuthority(CompetentAuthorityEnum competentAuthorityEnum) {
		return pmrvCompetentAuthorityRepository.findById(competentAuthorityEnum)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

}
