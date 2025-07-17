package uk.gov.pmrv.api.account.companieshouse;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountyAddressDTO {

    private String line1;
    private String line2;
    private String country;
    private String city;
    private String postcode;

    @JsonGetter("line1")
    public String getLine1() {
        return line1;
    }

    @JsonSetter("address_line_1")
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    @JsonGetter("line2")
    public String getLine2() {
        return line2;
    }

    @JsonSetter("address_line_2")
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    @JsonGetter("country")
    public String getCountry() {
        return country;
    }

    @JsonSetter("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonGetter("city")
    public String getCity() {
        return city;
    }

    @JsonSetter("locality")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonGetter("postcode")
    public String getPostcode() {
        return postcode;
    }

    @JsonSetter("postal_code")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

}
