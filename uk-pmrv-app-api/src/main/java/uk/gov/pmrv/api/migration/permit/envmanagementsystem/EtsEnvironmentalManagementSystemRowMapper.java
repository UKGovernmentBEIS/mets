package uk.gov.pmrv.api.migration.permit.envmanagementsystem;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsEnvironmentalManagementSystemRowMapper implements RowMapper<EtsEnvironmentalManagementSystem> {

    @Override
    public EtsEnvironmentalManagementSystem mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsEnvironmentalManagementSystem.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .exist(resultSet.getBoolean("emsExist"))
            .certified(resultSet.getBoolean("emsCertified"))
            .certificationStandard(resultSet.getString("emsCertificationStandard"))
            .build();
    }
}
