package uk.gov.pmrv.api.account.installation.repository.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountCustomRepository;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Repository
public class InstallationAccountCustomRepositoryImpl implements InstallationAccountCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public AccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria) {
        return AccountSearchResults.builder()
            .accounts(constructQuery(accountIds, null, null, searchCriteria, false).getResultList())
            .total(((Number) constructQuery(accountIds, null, null, searchCriteria, true).getSingleResult()).longValue())
            .build();
    }

    @Override
    public AccountSearchResults findByCompAuth(CompetentAuthorityEnum compAuth, AccountSearchCriteria searchCriteria) {
        return AccountSearchResults.builder()
            .accounts(constructQuery(null, compAuth, null, searchCriteria, false).getResultList())
            .total(((Number) constructQuery(null, compAuth, null, searchCriteria, true).getSingleResult()).longValue())
            .build();
    }

    public AccountSearchResults findByVerificationBodyId(Long verificationBodyId, AccountSearchCriteria searchCriteria) {
        return AccountSearchResults.builder()
            .accounts(constructQuery(null, null, verificationBodyId, searchCriteria, false).getResultList())
            .total(((Number) constructQuery(null, null, verificationBodyId, searchCriteria, true).getSingleResult()).longValue())
            .build();
    }

    private Query constructQuery(List<Long> accountIds, CompetentAuthorityEnum compAuth, Long verificationBodyId,
                                 AccountSearchCriteria searchCriteria, boolean forCount) {
        StringBuilder sb = new StringBuilder();

        if (forCount) {
            sb.append("select count(*) from ( \n");
        }

        sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc_inst.status, le.name as legalEntityName \n")
            .append("from account acc \n")
            .append("join account_installation acc_inst on acc_inst.id=acc.id \n")
            .append("join account_legal_entity le on le.id = acc.legal_entity_id \n")
            .append("where 1 = 1 \n")
        ;

        constructMainWhereClause(accountIds, compAuth, verificationBodyId, sb);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            sb.append("and (acc.name ilike :accName \n");
            sb.append("or acc_inst.site_name ilike :accSiteName \n");
            sb.append("or acc.emitter_id ilike :accEmitterId \n");
            sb.append(") \n");

            sb.append("UNION \n");

            sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc_inst.status, le.name as legalEntityName \n")
                .append("from account acc \n")
                .append("join account_installation acc_inst on acc_inst.id=acc.id \n")
                .append("join account_legal_entity le on le.id = acc.legal_entity_id \n")
                .append("where le.name ilike :leName \n");

            constructMainWhereClause(accountIds, compAuth, verificationBodyId, sb);

            sb.append("UNION \n");

            sb.append("select acc.id, acc.name, acc.emitter_id as emitterId, acc_inst.status, le.name as legalEntityName \n")
                .append("from account acc \n")
                .append("join account_installation acc_inst on acc_inst.id=acc.id \n")
                .append("join account_search_additional_keyword ak on ak.account_id = acc.id \n")
                .append("join account_legal_entity le on le.id = acc.legal_entity_id \n")
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
            query = entityManager.createNativeQuery(sb.toString(), InstallationAccount.ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER);
        }

        populateMainWhereClauseParameters(accountIds, compAuth, verificationBodyId, query);

        if (StringUtils.hasText(searchCriteria.getTerm())) {
            query.setParameter("accName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("accSiteName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("accEmitterId", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("leName", "%" + searchCriteria.getTerm() + "%");
            query.setParameter("keywordValue", "%" + searchCriteria.getTerm() + "%");

            populateMainWhereClauseParameters(accountIds, compAuth, verificationBodyId, query);
        }

        if (!forCount) {
            query.setParameter("limit", searchCriteria.getPaging().getPageSize());
            query.setParameter("offset", searchCriteria.getPaging().getPageNumber() * searchCriteria.getPaging().getPageSize());
        }

        return query;
    }

    private void constructMainWhereClause(List<Long> accountIds, CompetentAuthorityEnum compAuth,
                                          Long verificationBodyId, StringBuilder sb) {
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

    private void populateMainWhereClauseParameters(List<Long> accountIds, CompetentAuthorityEnum compAuth,
                                                   Long verificationBodyId, Query query) {
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
