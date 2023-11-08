package uk.gov.pmrv.api.competentauthority.repository;

import java.util.Optional;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.competentauthority.domain.CompetentAuthority;

public interface CompetentAuthorityCustomRepository {

	Optional<CompetentAuthority> findByIdForUpdate(CompetentAuthorityEnum id);
	
}
