package uk.gov.pmrv.api.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CaExternalContactRepository extends JpaRepository<CaExternalContact, Long> {

    List<CaExternalContact> findByCompetentAuthority(CompetentAuthorityEnum ca);
    
    List<CaExternalContact> findAllByIdIn(Set<Long> ids);

    Optional<CaExternalContact> findByIdAndCompetentAuthority(Long id, CompetentAuthorityEnum ca);

    boolean existsByCompetentAuthorityAndName(CompetentAuthorityEnum ca, String name);

    boolean existsByCompetentAuthorityAndEmailIgnoreCase(CompetentAuthorityEnum ca, String email);

    boolean existsByCompetentAuthorityAndNameAndIdNot(CompetentAuthorityEnum ca, String name, Long id);

    boolean existsByCompetentAuthorityAndEmailIgnoreCaseAndIdNot(CompetentAuthorityEnum ca, String email, Long id);

    void deleteById(Long id);
}
