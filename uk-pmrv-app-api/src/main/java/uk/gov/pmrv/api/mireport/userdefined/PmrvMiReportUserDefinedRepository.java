package uk.gov.pmrv.api.mireport.userdefined;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedEntity;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Repository
public interface PmrvMiReportUserDefinedRepository extends JpaRepository<MiReportUserDefinedEntity, Long> {

    @Transactional(readOnly = true)
    @Query(value = """
            select distinct r from MiReportUserDefinedEntity r
            join MiReportUserDefinedAccountType a on a.miReportId = r.id
            left join r.categories c
            where r.competentAuthority = :competentAuthority
              and a.accountType = :accountType
              and (:categoryId is null or c.id = :categoryId)
              and (:term is null
                   or lower(r.reportName) like :term escape '\\'
                   or lower(r.description) like :term escape '\\')
              and (:userId is null
                   or exists (select 1 from MiReportUserDefinedFavouriteEntity f
                          where f.miReportId = r.id and f.userId = :userId))""",
            countQuery = """
            select count(distinct r) from MiReportUserDefinedEntity r
            join MiReportUserDefinedAccountType a on a.miReportId = r.id
            left join r.categories c
            where r.competentAuthority = :competentAuthority
              and a.accountType = :accountType
              and (:categoryId is null or c.id = :categoryId)
              and (:term is null
                   or lower(r.reportName) like :term escape '\\'
                   or lower(r.description) like :term escape '\\')
              and (:userId is null
                    or exists (select 1 from MiReportUserDefinedFavouriteEntity f
                           where f.miReportId = r.id and f.userId = :userId))""")
    Page<MiReportUserDefinedEntity> findAllByCompetentAuthorityAndFilters(
            @Param("competentAuthority") CompetentAuthorityEnum competentAuthority,
            @Param("accountType") AccountType accountType,
            @Param("categoryId") Long categoryId,
            @Param("term") String term,
            @Param("userId") String userId,
            Pageable pageable);


}
