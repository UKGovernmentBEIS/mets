package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.repository.CaExternalContactRepository;
import uk.gov.pmrv.api.account.transform.CaExternalContactMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CaExternalContactService {

    private final CaExternalContactRepository caExternalContactRepository;
    private final CaExternalContactValidator caExternalContactValidator;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final CaExternalContactMapper caExternalContactMapper = Mappers.getMapper(CaExternalContactMapper.class);

    public CaExternalContactsDTO getCaExternalContacts(AppUser authUser) {
        List<CaExternalContact> caExternalContacts = caExternalContactRepository.findByCompetentAuthority(authUser.getCompetentAuthority());

        return CaExternalContactsDTO.builder()
            .caExternalContacts(caExternalContactMapper.toCaExternalContactDTOs(caExternalContacts))
            .isEditable(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, Scope.EDIT_USER))
            .build();
    }
    
    public CaExternalContactDTO getCaExternalContactById(AppUser authUser, Long id) {
        CompetentAuthorityEnum ca = authUser.getCompetentAuthority();

        CaExternalContact caExternalContact = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA));

        return caExternalContactMapper.toCaExternalContactDTO(caExternalContact);
    }
    
    public List<String> getCaExternalContactEmailsByIds(Set<Long> ids){
    	List<CaExternalContact> contacts = caExternalContactRepository.findAllByIdIn(ids);
    	
    	List<Long> idsMissing = ids.stream()
    		    .filter(id -> !contacts.stream().map(CaExternalContact::getId).collect(Collectors.toList()).contains(id))
    		    .collect(Collectors.toList());
    	
    	if(!idsMissing.isEmpty()) {
    		log.error("External contact ids are missing: " + idsMissing);
    		throw new BusinessException(ErrorCode.EXTERNAL_CONTACT_CA_MISSING, idsMissing);
    	} 
    	
		return contacts.stream().map(CaExternalContact::getEmail).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCaExternalContactById(AppUser authUser, Long id) {
        CompetentAuthorityEnum ca = authUser.getCompetentAuthority();

        Optional<CaExternalContact> byIdAndCompetentAuthority = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca);
        if (byIdAndCompetentAuthority.isEmpty()) {
            throw  new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA);
        }

        caExternalContactRepository.deleteById(id);
    }

    @Transactional
    public void createCaExternalContact(AppUser authUser, CaExternalContactRegistrationDTO caExternalContactRegistration) {
        CompetentAuthorityEnum ca = authUser.getCompetentAuthority();
        caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration);

        CaExternalContact caExternalContact = caExternalContactMapper.toCaExternalContact(
            caExternalContactRegistration,
            ca.name());

        caExternalContactRepository.save(caExternalContact);
    }

    @Transactional
    public void editCaExternalContact(AppUser authUser, Long id, CaExternalContactRegistrationDTO caExternalContactRegistration) {
        CompetentAuthorityEnum ca = authUser.getCompetentAuthority();

        CaExternalContact caExternalContact = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA));

        caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration);

        caExternalContact.setName(caExternalContactRegistration.getName());
        caExternalContact.setEmail(caExternalContactRegistration.getEmail());
        caExternalContact.setDescription(caExternalContactRegistration.getDescription());
    }

}
