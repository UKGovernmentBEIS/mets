package uk.gov.pmrv.api.account.companieshouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyProfileDTO {

    private String name;

    private String registrationNumber;

    private CompanyTypeDTO companyType;

    private CountyAddressDTO address;

    private List<SicCodeDTO> sicCodes;

}
