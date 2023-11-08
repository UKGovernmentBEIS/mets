package uk.gov.pmrv.api.files.common.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.InputStream;

@Log4j2
@Service
@Getter
class ClamAVClientService implements FileScanService {
    private final AppProperties appProperties;
    private ClamavClient clamavClient;

    public ClamAVClientService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.clamavClient =  getClamavClient();
    }

    public void scan(InputStream is) {
        ScanResult res = getScanResult(is);
        if (res instanceof ScanResult.VirusFound) {
            log.error("The selected file contains a virus");
            throw new BusinessException(ErrorCode.INFECTED_STREAM);
        }
    }

    private ScanResult getScanResult(InputStream is) {
        try {
            return clamavClient.scan(is);
        } catch (xyz.capybara.clamav.ClamavException ex) {
            if (ex.getCause() instanceof xyz.capybara.clamav.CommunicationException) {
                log.error("ClamAV communication exception");
                this.clamavClient = getClamavClient();
                return clamavClient.scan(is);
            }
            throw ex;
        }
    }

    private ClamavClient getClamavClient() {
        return new ClamavClient(appProperties.getClamAV().getHost(), appProperties.getClamAV().getPort());
    }
}
