package uk.gov.pmrv.api.mireport.aviation.accountuserscontacts;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountUsersContactsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AviationAccountUsersContactsRepository implements AccountUsersContactsRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<AviationAccountUserContact> findAccountUserContacts(EntityManager entityManager) {
        return entityManager.createNativeQuery(
                        "select auth.user_id as \"userId\", role.name as \"role\",  account.emitter_id as \"accountId\", account.name as \"accountName\", " +
                                "account.type as \"accountType\", acc_av.status as \"accountStatus\", le.name as \"legalEntityName\", auth.status as \"authorityStatus\", acc_av.central_route_charges_office_code as \"crcoCode\",\n" +
                                "       case when acPrimary.user_id is not null then true else false end as \"primaryContact\",\n" +
                                "       case when acService.user_id is not null then true else false end as \"serviceContact\",\n" +
                                "       case when acFinancial.user_id is not null then true else false end as \"financialContact\",\n" +
                                "       case when acSecondary.user_id is not null then true else false end as \"secondaryContact\",\n" +
                                "       emp.id as \"permitId\"\n" +
                                "from account\n" +
                                "    inner join account_aviation acc_av on account.id = acc_av.id\n" +
                                "    left join account_legal_entity le on account.legal_entity_id = le.id\n" +
                                "    left join emp on account.id = emp.account_id\n" +
                                "    left join au_authority auth on account.id = auth.account_id\n" +
                                "    left join au_role role on auth.code = role.code\n" +
                                "    left join account_contact acPrimary on account.id = acPrimary.account_id and auth.user_id=acPrimary.user_id and acPrimary.contact_type='PRIMARY'\n" +
                                "    left join account_contact acService on account.id = acService.account_id and auth.user_id=acService.user_id and acService.contact_type='SERVICE'\n" +
                                "    left join account_contact acFinancial on account.id = acFinancial.account_id and auth.user_id=acFinancial.user_id and acFinancial.contact_type='FINANCIAL'\n" +
                                "    left join account_contact acSecondary on account.id = acSecondary.account_id and auth.user_id=acSecondary.user_id and acSecondary.contact_type='SECONDARY'\n")
                .unwrap(NativeQuery.class)
                .addScalar("userId", StandardBasicTypes.STRING)
                .addScalar("accountId", StandardBasicTypes.STRING)
                .addScalar("accountName", StandardBasicTypes.STRING)
                .addScalar("accountType", StandardBasicTypes.STRING)
                .addScalar("accountStatus", StandardBasicTypes.STRING)
                .addScalar("legalEntityName", StandardBasicTypes.STRING)
                .addScalar("authorityStatus", StandardBasicTypes.STRING)
                .addScalar("primaryContact", StandardBasicTypes.BOOLEAN)
                .addScalar("secondaryContact", StandardBasicTypes.BOOLEAN)
                .addScalar("financialContact", StandardBasicTypes.BOOLEAN)
                .addScalar("serviceContact", StandardBasicTypes.BOOLEAN)
                .addScalar("permitId", StandardBasicTypes.STRING)
                .addScalar("role", StandardBasicTypes.STRING)
                .addScalar("crcoCode", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer((tuple, aliases) -> {
                    Map<String, Object> map = new HashMap<>();
                    for(int i = 0; i < tuple.length; i++) {
                        map.put(aliases[i], tuple[i]);
                    }
                    AviationAccountUserContact result = new AviationAccountUserContact();
                    result.setUserId((String)map.get("userId"));
                    result.setAccountId((String)map.get("accountId"));
                    result.setAccountName((String)map.get("accountName"));
                    result.setAccountType((String)map.get("accountType"));
                    result.setAccountStatus((String)map.get("accountStatus"));
                    result.setLegalEntityName((String)map.get("legalEntityName"));
                    result.setAuthorityStatus((String)map.get("authorityStatus"));
                    result.setPrimaryContact((Boolean) map.get("primaryContact"));
                    result.setSecondaryContact((Boolean)map.get("secondaryContact"));
                    result.setFinancialContact((Boolean)map.get("financialContact"));
                    result.setServiceContact((Boolean)map.get("serviceContact"));
                    result.setPermitId((String)map.get("permitId"));
                    result.setPermitType((String)map.get("permitType"));
                    result.setRole((String)map.get("role"));
                    result.setCrcoCode((String)map.get("crcoCode"));
                    return result;
                })
                .getResultList();
    }
}
