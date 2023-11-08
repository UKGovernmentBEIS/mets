package uk.gov.pmrv.api.migration.legalentity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LegalEntityVOMapper implements RowMapper<LegalEntityVO> {

    @Override
    public LegalEntityVO mapRow(ResultSet rs, int i) throws SQLException {
        return LegalEntityVO.builder()
            .operatorId(rs.getString("operatorId"))
            .emitterId(rs.getString("emitterId"))
            .competentAuthority(rs.getString("competent_authority"))
            .type(rs.getString("type"))
            .name(rs.getString("name"))
            .referenceNumber(rs.getString("reference_number"))
            .line1(rs.getString("line1"))
            .line2(rs.getString("line2"))
            .city(rs.getString("city"))
            .postcode(rs.getString("postcode"))
            .country(rs.getString("country"))
            .emitterDisplayId(rs.getString("emitterDisplayId"))
            .emitterName(rs.getString("emitterName"))
            .holdingCompanyPartOfOrganization(HoldingCompanyPartOfOrganization.convertFrom(rs.getString("belongsToHoldingCompany")))
            .holdingCompanyName(rs.getString("holdingCompanyName"))
            .holdingCompanyRegistrationNumber(rs.getString("holdingCompanyRegistrationNumber"))
            .holdingCompanyAddressLine1(rs.getString("holdingCompanyAddressLine1"))
            .holdingCompanyAddressLine2(rs.getString("holdingCompanyAddressLine2"))
            .holdingCompanyAddressCity(rs.getString("holdingCompanyAddressCity"))
            .holdingCompanyAddressPostcode(rs.getString("holdingCompanyAddressPostcode"))

            .build();
    }
}
