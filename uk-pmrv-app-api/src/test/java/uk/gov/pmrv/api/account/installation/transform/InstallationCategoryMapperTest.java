package uk.gov.pmrv.api.account.installation.transform;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InstallationCategoryMapperTest {

    @Test
    void getInstallationCategory() {
        assertEquals(InstallationCategory.N_A, InstallationCategoryMapper.getInstallationCategory(EmitterType.HSE, BigDecimal.valueOf(40000)));

        assertEquals(InstallationCategory.A, InstallationCategoryMapper.getInstallationCategory(EmitterType.GHGE, BigDecimal.valueOf(40000)));

        assertEquals(InstallationCategory.B, InstallationCategoryMapper.getInstallationCategory(EmitterType.GHGE, BigDecimal.valueOf(140000)));

        assertEquals(InstallationCategory.C, InstallationCategoryMapper.getInstallationCategory(EmitterType.GHGE, BigDecimal.valueOf(540000)));
    }
}