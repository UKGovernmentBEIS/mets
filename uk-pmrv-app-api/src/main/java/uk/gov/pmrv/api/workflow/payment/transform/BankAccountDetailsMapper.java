package uk.gov.pmrv.api.workflow.payment.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.payment.domain.BankAccountDetails;
import uk.gov.pmrv.api.workflow.payment.domain.dto.BankAccountDetailsDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BankAccountDetailsMapper {

    BankAccountDetailsDTO toBankAccountDetailsDTO(BankAccountDetails bankAccountDetails);
}
