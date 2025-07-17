package uk.gov.pmrv.api.migration.emp.common.abbreviations;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEmpAbbreviationRowMapper implements RowMapper<EtsEmpAbbreviation> {

	@Override
    public EtsEmpAbbreviation mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpAbbreviation.builder()
        		.fldEmitterId(rs.getString("fldEmitterId"))
                .fldEmitterDisplayId(rs.getString("fldEmitterDisplayId"))
                .abbreviation(rs.getString("Abbreviation"))
                .definition(rs.getString("Definition"))
                .build();
    }
}
