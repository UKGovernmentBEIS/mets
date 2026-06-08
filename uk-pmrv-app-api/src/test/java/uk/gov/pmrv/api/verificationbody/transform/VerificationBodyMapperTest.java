package uk.gov.pmrv.api.verificationbody.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEmissionSchemeDTO;

class VerificationBodyMapperTest {

    private VerificationBodyMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(VerificationBodyMapper.class);
    }

    @Test
    void toVerificationBody() {
        String name = "name";
        AddressDTO address = AddressDTO.builder().line1("line1").line2("line2").city("city").country("country").postcode("code").build();

        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .accreditationReferenceNumber("accreditationRefNum")
                .accreditationName("name1")
                .build();
        VerificationBodyEmissionSchemeDTO verificationBodyEmissionSchemeDTO2 = VerificationBodyEmissionSchemeDTO.builder()
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .accreditationReferenceNumber("accreditationRefNum2")
                .accreditationName("name2")
                .build();
        Set<VerificationBodyEmissionSchemeDTO> verificationBodyEmissionSchemeDTOS = Set.of(verificationBodyEmissionSchemeDTO, verificationBodyEmissionSchemeDTO2);

        VerificationBodyEditDTO verificationBodyEditDTO = VerificationBodyEditDTO.builder()
            .name(name)
            .address(address)
            .verificationBodyEmissionSchemes(verificationBodyEmissionSchemeDTOS)
            .build();

        //invoke
        VerificationBody verificationBody = mapper.toVerificationBody(verificationBodyEditDTO);

        //assertions
        assertThat(verificationBody).isNotNull();
        assertEquals(name, verificationBody.getName());

        Address verificationBodyAddress = verificationBody.getAddress();
        assertThat(verificationBodyAddress).isNotNull();
        assertEquals(address.getLine1(), verificationBodyAddress.getLine1());
        assertEquals(address.getLine2(), verificationBodyAddress.getLine2());
        assertEquals(address.getCity(), verificationBodyAddress.getCity());
        assertEquals(address.getCountry(), verificationBodyAddress.getCountry());
        assertEquals(address.getPostcode(), verificationBodyAddress.getPostcode());

        assertThat(verificationBody.getEmissionSchemes()).hasSize(2);
    }
}