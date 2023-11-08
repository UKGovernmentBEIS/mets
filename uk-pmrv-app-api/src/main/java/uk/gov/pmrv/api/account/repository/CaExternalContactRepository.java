package uk.gov.pmrv.api.account.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

public interface CaExternalContactRepository extends JpaRepository<CaExternalContact, Long> {

    List<CaExternalContact> findByCompetentAuthority(CompetentAuthorityEnum ca);
    
    List<CaExternalContact> findAllByIdIn(Set<Long> ids);

    Optional<CaExternalContact> findByIdAndCompetentAuthority(Long id, CompetentAuthorityEnum ca);

    boolean existsByCompetentAuthorityAndName(CompetentAuthorityEnum ca, String name);

    boolean existsByCompetentAuthorityAndEmail(CompetentAuthorityEnum ca, String email);

    boolean existsByCompetentAuthorityAndNameAndIdNot(CompetentAuthorityEnum ca, String name, Long id);

    boolean existsByCompetentAuthorityAndEmailAndIdNot(CompetentAuthorityEnum ca, String email, Long id);

    void deleteById(Long id);
}
