package uk.gov.pmrv.api.account.aviation.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountCustomRepository;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

@Repository
public class AviationAccountCustomRepositoryImpl implements AviationAccountCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AviationAccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria) {
        return AviationAccountSearchResults.builder()
                .accounts(constructQuery(accountIds, null, null, searchCriteria, false).getResultList())
                .total(((Number) constructQuery(accountIds, null, null, searchCriteria, true).getSingleResult()).longValue())
                .build();
    }

    @Override
    public AviationAccountSearchResults findByCompAuth(CompetentAuthorityEnum compAuth, AccountSearchCriteria searchCriteria) {
        return AviationAccountSearchResults.builder()
                .accounts(constructQuery(null, compAuth, null, searchCriteria, false).getResultList())
                .total(((Number) constructQuery(null, compAuth, null, searchCriteria, true).getSingleResult()).longValue())
                .build();
    }

    private Query constructQuery(List<Long> accountIds, CompetentAuthorityEnum compAuth, Long verificationBodyId,
                                 AccountSearchCriteria searchCriteria, boolean forCount) {

        StringBuilder sb = new StringBuilder();

        if (forCount) {
            sb.append("select count(*) from ( \n");
        }

        sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc_av.status \n")
                .append("from account acc \n")
                .append("join account_aviation acc_av on acc_av.id=acc.id \n")
                .append("where 1 = 1 \n")
        ;

        constructMainWhereClause(accountIds, compAuth, verificationBodyId, sb);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            sb.append("and (acc.name ilike :accName \n");
            sb.append("or acc_av.central_route_charges_office_code ilike :crcoCode \n");
            sb.append("or acc.emitter_id ilike :accEmitterId \n");
            sb.append(") \n");

            sb.append("UNION \n");

            sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc_av.status \n")
                    .append("from account acc \n")
                    .append("join account_aviation acc_av on acc_av.id=acc.id \n")
                    .append("join account_search_additional_keyword ak on ak.account_id = acc.id \n")
                    .append("where ak.value ilike :keywordValue \n");

            constructMainWhereClause(accountIds, compAuth, verificationBodyId, sb);
        }

        if (!forCount) {
            sb.append("order by emitterId asc \n")
                    .append("limit :limit \n")
                    .append("offset :offset \n");
        } else {
            sb.append(") results");
        }

        Query query;
        if (forCount) {
            query = entityManager.createNativeQuery(sb.toString());
        } else {
            query = entityManager.createNativeQuery(sb.toString(), AviationAccount.AVIATION_ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER);
        }

        populateMainWhereClauseParameters(accountIds, compAuth, verificationBodyId, query);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            query.setParameter("accName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("crcoCode", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("accEmitterId", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("keywordValue", "%" + searchCriteria.getTerm() + "%");

            populateMainWhereClauseParameters(accountIds, compAuth, verificationBodyId, query);
        }

        if (!forCount) {
            query.setParameter("limit", searchCriteria.getPaging().getPageSize());
            query.setParameter("offset", searchCriteria.getPaging().getPageNumber() * searchCriteria.getPaging().getPageSize());
        }

        return query;
    }


    private void constructMainWhereClause(List<Long> accountIds, CompetentAuthorityEnum compAuth, Long verificationBodyId, StringBuilder sb) {

        if (ObjectUtils.isNotEmpty(accountIds)) {
            sb.append("and acc.id in :accountIds \n");
        }

        if (compAuth != null) {
            sb.append("and acc.competent_authority = :compAuth \n");
        }

        if (verificationBodyId != null) {
            sb.append("and acc.verification_body_id = :verificationBodyId \n");
        }
    }

    private void populateMainWhereClauseParameters(List<Long> accountIds, CompetentAuthorityEnum compAuth, Long verificationBodyId, Query query) {

        if (ObjectUtils.isNotEmpty(accountIds)) {
            query.setParameter("accountIds", accountIds);
        }

        if (compAuth != null) {
            query.setParameter("compAuth", compAuth.name());
        }

        if (verificationBodyId != null) {
            query.setParameter("verificationBodyId", verificationBodyId);
        }
    }
}
