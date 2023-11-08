package uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface AccountAssignedRegulatorSiteContactsRepository {

    List<AccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager);
}
