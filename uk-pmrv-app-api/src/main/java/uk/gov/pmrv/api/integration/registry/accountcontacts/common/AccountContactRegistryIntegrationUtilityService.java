package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserManagementService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class AccountContactRegistryIntegrationUtilityService {

    private final OperatorUserManagementService operatorUserManagementService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Transactional
    public MetsContactsEvent buildMetsContactsEvent(Account account) {
        List<MetsContactsMessage> metsContactsMessages = new ArrayList<>();

        //get the operator authorities and exclude the inactive ones and the verifier roles
        List<AuthorityRoleDTO> authorityRoleDTOList = operatorAuthorityQueryService.
                findOperatorUserAuthoritiesListByAccount(account.getId()).stream().
                filter(authorityRoleDTO -> AuthorityStatus.ACTIVE.equals(authorityRoleDTO.getAuthorityStatus()) &&
                !(authorityRoleDTO.getRoleCode().equalsIgnoreCase("verifier") ||
                        authorityRoleDTO.getRoleCode().equalsIgnoreCase("verifier_admin"))).toList();

        for(AuthorityRoleDTO authorityRoleDTO : authorityRoleDTOList) {
            OperatorUserDTO operatorUserDTO =
                    operatorUserManagementService.getOperatorUserByAccountAndId(account.getId(), authorityRoleDTO.getUserId());
            metsContactsMessages.add(buildMetsContactsMessage(operatorUserDTO,
                    findAccountContactTypes(account.getContacts(),authorityRoleDTO.getUserId()),
                    authorityRoleDTO.getRoleCode()));
        }

        return MetsContactsEvent.builder().operatorId(String.valueOf(account.getRegistryId())).details(metsContactsMessages).build();
    }

    private MetsContactsMessage buildMetsContactsMessage(OperatorUserDTO operatorUserDTO,List<String> accountContactTypes,String roleCode) {
        return MetsContactsMessage.builder()
                .firstName(operatorUserDTO.getFirstName())
                .lastName(operatorUserDTO.getLastName())
                .email(operatorUserDTO.getEmail())
                .telephoneNumber(operatorUserDTO.getPhoneNumber().getNumber())
                .telephoneCountryCode(operatorUserDTO.getPhoneNumber().getCountryCode())
                .mobileNumber(operatorUserDTO.getMobileNumber().getNumber())
                .mobilePhoneCountryCode(operatorUserDTO.getMobileNumber().getCountryCode())
                .userType(RegistryAccountContactUserType.fromRoleCode(roleCode).name())
                .contactTypes(accountContactTypes)
                .build();
    }

    private List<String> findAccountContactTypes(Map<AccountContactType, String> contacts,String userId) {
        List<String> accountContactTypes = new ArrayList<>();
        for (Map.Entry<AccountContactType, String> contactEntry : contacts.entrySet()) {
            if(contactEntry.getValue()!=null && contactEntry.getValue().equalsIgnoreCase(userId)) {
                accountContactTypes.add(contactEntry.getKey().name());
            }
        }
        return accountContactTypes;
    }

}
