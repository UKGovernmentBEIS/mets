package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
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

@Service
@RequiredArgsConstructor
public class InstallationAccountCompetentAuthorityDTOByRequestResolver implements CompetentAuthorityDTOByRequestResolver {

	private final PmrvCompetentAuthorityMapper pmrvCompetentAuthorityMapper;
	private final PmrvCompetentAuthorityRepository pmrvCompetentAuthorityRepository;
	private final InstallationAccountQueryService installationAccountQueryService;

	@Override
	public CompetentAuthorityDTO resolveCA(Request request) {
		final PmrvCompetentAuthority ca = getPmrvCompetentAuthority(request.getCompetentAuthority());
		
		if(RequestType.PERMIT_ISSUANCE.equals(request.getType())){
			PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload)request.getPayload();
			PermitType permitType = requestPayload.getPermitType();
			return pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca,
					PermitType.WASTE.equals(permitType) ? ca.getWasteEmail() : ca.getEmail());
		} else {
			final InstallationAccountDTO accountInfo = installationAccountQueryService
					.getAccountDTOById(request.getAccountId());
			EmitterType emitterType = accountInfo.getEmitterType();
			return pmrvCompetentAuthorityMapper.toCompetentAuthorityDTO(ca,
					EmitterType.WASTE.equals(emitterType) ? ca.getWasteEmail() : ca.getEmail());	
		}
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.INSTALLATION;
	}
	
	private PmrvCompetentAuthority getPmrvCompetentAuthority(CompetentAuthorityEnum competentAuthorityEnum) {
		return pmrvCompetentAuthorityRepository.findById(competentAuthorityEnum)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

}
