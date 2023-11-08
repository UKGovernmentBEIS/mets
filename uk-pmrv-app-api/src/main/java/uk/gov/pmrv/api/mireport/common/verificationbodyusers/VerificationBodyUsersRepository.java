package uk.gov.pmrv.api.mireport.common.verificationbodyusers;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class VerificationBodyUsersRepository {

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<VerificationBodyUser> findAllVerificationBodyUsers(EntityManager entityManager) {

        return entityManager.createNativeQuery("select verification_body.name as \"verificationBodyName\", verification_body.status as \"accountStatus\", " +
                " verification_body.accreditation_reference_number as \"accreditationReferenceNumber\", " +
                " case when emissionTradingSchemeUKETSInstallations.emission_trading_scheme is not null then true else false end as \"isAccreditedForUKETSInstallations\", " +
                " case when emissionTradingSchemeEUETSInstallations.emission_trading_scheme is not null then true else false end as \"isAccreditedForEUETSInstallations\", " +
                " case when emissionTradingSchemeUKETSAviation.emission_trading_scheme is not null then true else false end as \"isAccreditedForUKETSAviation\", " +
                " case when emissionTradingSchemeCorsia.emission_trading_scheme is not null then true else false end as \"isAccreditedForCorsia\", " +
                " role.name as \"role\", auth.status as \"authorityStatus\", auth.user_id as \"userId\" " +
                " from verification_body " +
                " left join au_authority auth on verification_body.id = auth.verification_body_id " +
                " left join au_role role on auth.code = role.code " +
                " left join verification_body_emission_trading_scheme emissionTradingSchemeUKETSInstallations on verification_body.id=emissionTradingSchemeUKETSInstallations.verification_body_id and emissionTradingSchemeUKETSInstallations.emission_trading_scheme='UK_ETS_INSTALLATIONS' " +
                " left join verification_body_emission_trading_scheme emissionTradingSchemeEUETSInstallations on verification_body.id=emissionTradingSchemeEUETSInstallations.verification_body_id and emissionTradingSchemeEUETSInstallations.emission_trading_scheme='EU_ETS_INSTALLATIONS' " +
                " left join verification_body_emission_trading_scheme emissionTradingSchemeUKETSAviation on verification_body.id=emissionTradingSchemeUKETSAviation.verification_body_id and emissionTradingSchemeUKETSAviation.emission_trading_scheme='UK_ETS_AVIATION' " +
                " left join verification_body_emission_trading_scheme emissionTradingSchemeCorsia on verification_body.id=emissionTradingSchemeCorsia.verification_body_id and emissionTradingSchemeCorsia.emission_trading_scheme='CORSIA' " +
                " order by verification_body.status, verification_body.name, role.name ")
                .unwrap(NativeQuery.class)
                .addScalar("verificationBodyName", StandardBasicTypes.STRING)
                .addScalar("accountStatus", StandardBasicTypes.STRING)
                .addScalar("accreditationReferenceNumber", StandardBasicTypes.STRING)
                .addScalar("isAccreditedForUKETSInstallations", StandardBasicTypes.BOOLEAN)
                .addScalar("isAccreditedForEUETSInstallations", StandardBasicTypes.BOOLEAN)
                .addScalar("isAccreditedForUKETSAviation", StandardBasicTypes.BOOLEAN)
                .addScalar("isAccreditedForCorsia", StandardBasicTypes.BOOLEAN)
                .addScalar("role", StandardBasicTypes.STRING)
                .addScalar("authorityStatus", StandardBasicTypes.STRING)
                .addScalar("userId", StandardBasicTypes.STRING)
                .setReadOnly(true)
                .setTupleTransformer(Transformers.aliasToBean(VerificationBodyUser.class))
                .getResultList();
    }
}
