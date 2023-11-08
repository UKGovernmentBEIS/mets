package uk.gov.pmrv.api.account.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class HoldingCompanyAddressMapperTest {
    private final HoldingCompanyAddressMapper mapper = Mappers.getMapper(HoldingCompanyAddressMapper.class);

    @Test
    void toHoldingCompanyAddress() {
        HoldingCompanyAddressDTO holdingCompanyAddressDTO = HoldingCompanyAddressDTO.builder()
            .line1("line1")
            .line2("line2")
            .city("city")
            .postcode("postcode")
            .build();

        HoldingCompanyAddress holdingCompanyAddress = mapper.toHoldingCompanyAddress(holdingCompanyAddressDTO);

        assertThat(holdingCompanyAddress).isEqualTo(HoldingCompanyAddress.builder()
                .line1(holdingCompanyAddressDTO.getLine1())
                .line2(holdingCompanyAddressDTO.getLine2())
                .city(holdingCompanyAddressDTO.getCity())
                .postcode(holdingCompanyAddressDTO.getPostcode())
            .build());
    }
}
