package uk.gov.pmrv.api.mireport.common.customreport;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.customreport.CustomMiReportGeneratorHandler;
import uk.gov.netz.api.mireport.customreport.CustomMiReportParams;
import uk.gov.netz.api.userinfoapi.UserInfoApi;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;

@Service
@Log4j2
@Primary
public class PmrvCustomMiReportGeneratorHandler extends CustomMiReportGeneratorHandler implements InstallationMiReportGeneratorHandler<CustomMiReportParams>,
        AviationMiReportGeneratorHandler<CustomMiReportParams> {

    public PmrvCustomMiReportGeneratorHandler(UserInfoApi userInfoApi) {
        super(userInfoApi);
    }
}
