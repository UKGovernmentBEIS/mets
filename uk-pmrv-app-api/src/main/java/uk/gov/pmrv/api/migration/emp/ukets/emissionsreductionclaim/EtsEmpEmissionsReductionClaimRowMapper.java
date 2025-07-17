package uk.gov.pmrv.api.migration.emp.ukets.emissionsreductionclaim;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpEmissionsReductionClaimRowMapper implements RowMapper<EtsEmpEmissionsReductionClaim> {

	@Override
    public EtsEmpEmissionsReductionClaim mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpEmissionsReductionClaim.builder()
        		.fldEmitterId(rs.getString("fldEmitterId"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
                .purchaseDeliveryTitle(rs.getString("purchase_delivery_title"))
                .purchaseDeliveryReference(rs.getString("purchase_delivery_reference"))
                .purchaseDeliveryDescription(rs.getString("purchase_delivery_description"))
                .purchaseDeliveryPost(rs.getString("purchase_delivery_post"))
                .purchaseDeliveryLocation(rs.getString("purchase_delivery_location"))
                .purchaseDeliverySystem(rs.getString("purchase_delivery_system"))
                .sustainabilityTitle(rs.getString("sustainability_title"))
                .sustainabilityReference(rs.getString("sustainability_reference"))
                .sustainabilityDescription(rs.getString("sustainability_description"))
                .sustainabilityPost(rs.getString("sustainability_post"))
                .sustainabilityLocation(rs.getString("sustainability_location"))
                .sustainabilitySystem(rs.getString("sustainability_system"))
                .avoidanceTitle(rs.getString("avoidance_title"))
                .avoidanceReference(rs.getString("avoidance_reference"))
                .avoidanceDescription(rs.getString("avoidance_description"))
                .avoidancePost(rs.getString("avoidance_post"))
                .avoidanceLocation(rs.getString("avoidance_location"))
                .avoidanceSystem(rs.getString("avoidance_system"))
                .build();
    }
}
