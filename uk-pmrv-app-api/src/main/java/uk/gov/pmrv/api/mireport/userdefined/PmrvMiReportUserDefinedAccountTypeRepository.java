package uk.gov.pmrv.api.mireport.userdefined;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PmrvMiReportUserDefinedAccountTypeRepository extends JpaRepository<MiReportUserDefinedAccountType, Long> {
}
