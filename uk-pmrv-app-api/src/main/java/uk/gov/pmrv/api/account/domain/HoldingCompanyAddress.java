package uk.gov.pmrv.api.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HoldingCompanyAddress {
	
	/** The line 1 address. */
    @Column(name = "line1")
    @NotBlank
    private String line1;

    /** The line 2 address. */
    @Column(name = "line2")
    private String line2;

    /** The city. */
    @Column(name = "city")
    @NotBlank
    private String city;

    /** The postcode. */
    @Column(name = "postcode")
    @NotBlank
    private String postcode;
}
