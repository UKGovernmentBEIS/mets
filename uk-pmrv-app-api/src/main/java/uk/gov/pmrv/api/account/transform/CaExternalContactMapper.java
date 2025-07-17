package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;

import java.util.List;

@Mapper
public interface CaExternalContactMapper {

    List<CaExternalContactDTO> toCaExternalContactDTOs(List<CaExternalContact> caExternalContacts);

    CaExternalContactDTO toCaExternalContactDTO(CaExternalContact caExternalContact);

    CaExternalContact toCaExternalContact(CaExternalContactRegistrationDTO caExternalContactRegistration,
                                          String competentAuthority);
}
