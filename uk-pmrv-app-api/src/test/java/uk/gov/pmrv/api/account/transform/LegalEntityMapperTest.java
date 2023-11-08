package uk.gov.pmrv.api.account.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.HoldingCompany;
import uk.gov.pmrv.api.account.domain.HoldingCompanyAddress;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyAddressDTO;
import uk.gov.pmrv.api.account.domain.dto.HoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class LegalEntityMapperTest {

	private final LegalEntityMapper mapper = Mappers.getMapper(LegalEntityMapper.class);
	
	@Test
	void toLegalEntityWithoutHoldingCompanyDTO() {
		LegalEntity legalEntity = LegalEntity.builder()
						.name("lename")
						.type(LegalEntityType.LIMITED_COMPANY)
						.referenceNumber("refnum")
						.location(LocationOnShore.builder()
								.address(Address.builder()
								.city("city2")
								.line1("line2")
								.build())
								.build())
						.build();
		
		LegalEntityWithoutHoldingCompanyDTO result = mapper.toLegalEntityWithoutHoldingCompanyDTO(legalEntity);
		
		assertThat(result).isEqualTo(LegalEntityWithoutHoldingCompanyDTO.builder()
						.name("lename")
						.type(LegalEntityType.LIMITED_COMPANY)
						.referenceNumber("refnum")
						.address(AddressDTO.builder()
								.city("city2")
								.line1("line2")
								.build())
						.build())
				;
	}

	@Test
	void toLegalEntity() {
		LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder()
			.id(null)
			.name("name")
			.referenceNumber("reference")
			.noReferenceNumberReason(null)
			.type(LegalEntityType.SOLE_TRADER)
			.holdingCompany(HoldingCompanyDTO.builder()
				.name("holding")
				.registrationNumber("123456")
				.address(HoldingCompanyAddressDTO.builder()
					.line1("line1")
					.line2("line2")
					.city("city")
					.postcode("postcode")
					.build())
				.build())
			.build();

		LegalEntity legalEntity = mapper.toLegalEntity(legalEntityDTO);

		assertThat(legalEntity).isEqualTo(LegalEntity.builder()
				.name(legalEntityDTO.getName())
				.id(legalEntityDTO.getId())
				.type(legalEntityDTO.getType())
				.noReferenceNumberReason(legalEntityDTO.getNoReferenceNumberReason())
				.referenceNumber(legalEntityDTO.getReferenceNumber())
				.holdingCompany(HoldingCompany.builder()
					.name(legalEntityDTO.getHoldingCompany().getName())
					.registrationNumber(legalEntityDTO.getHoldingCompany().getRegistrationNumber())
					.address(HoldingCompanyAddress.builder()
						.line1(legalEntityDTO.getHoldingCompany().getAddress().getLine1())
						.line2(legalEntityDTO.getHoldingCompany().getAddress().getLine2())
						.city(legalEntityDTO.getHoldingCompany().getAddress().getCity())
						.postcode(legalEntityDTO.getHoldingCompany().getAddress().getPostcode())
						.build())
					.build())
			.build());
	}

	@Test
	void toLegalEntityDTO() {
		LegalEntity legalEntity = LegalEntity.builder()
			.name("name")
			.status(LegalEntityStatus.ACTIVE)
			.id(1L)
			.noReferenceNumberReason(null)
			.referenceNumber("reference")
			.type(LegalEntityType.LIMITED_COMPANY)
			.holdingCompany(HoldingCompany.builder()
				.name("holding")
				.registrationNumber("123456")
				.address(HoldingCompanyAddress.builder()
					.line1("line1")
					.line2("line2")
					.city("city")
					.postcode("postcode")
					.build())
				.build())
			.build();

		LegalEntityDTO legalEntityDTO = mapper.toLegalEntityDTO(legalEntity);

		assertThat(legalEntityDTO).isEqualTo(LegalEntityDTO.builder()
			.name(legalEntityDTO.getName())
			.id(legalEntityDTO.getId())
			.type(legalEntityDTO.getType())
			.noReferenceNumberReason(legalEntityDTO.getNoReferenceNumberReason())
			.referenceNumber(legalEntityDTO.getReferenceNumber())
			.holdingCompany(HoldingCompanyDTO.builder()
				.name(legalEntityDTO.getHoldingCompany().getName())
				.registrationNumber(legalEntityDTO.getHoldingCompany().getRegistrationNumber())
				.address(HoldingCompanyAddressDTO.builder()
					.line1(legalEntityDTO.getHoldingCompany().getAddress().getLine1())
					.line2(legalEntityDTO.getHoldingCompany().getAddress().getLine2())
					.city(legalEntityDTO.getHoldingCompany().getAddress().getCity())
					.postcode(legalEntityDTO.getHoldingCompany().getAddress().getPostcode())
					.build())
				.build())
			.build());
	}
}
