package uk.gov.pmrv.api.user.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.user.core.domain.UserLoginDomain;

@Repository
public interface UserLoginDomainRepository extends JpaRepository<UserLoginDomain, String> {
}
