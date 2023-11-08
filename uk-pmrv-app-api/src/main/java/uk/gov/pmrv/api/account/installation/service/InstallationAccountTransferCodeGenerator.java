package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Log4j2
public class InstallationAccountTransferCodeGenerator {
    
    private static final int MAX_ATTEMPTS = 10;
    
    private final InstallationAccountQueryService installationAccountQueryService;

    public String generate() {

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            final int num = new SecureRandom().nextInt(1_000_000_000);
            final String transferCode = String.format("%09d", num);
            final boolean exists = installationAccountQueryService.transferCodeExists(transferCode);
            if (!exists) {
                return transferCode;
            }
        }
        log.error("Exceeded maximum attempts to create a unique transfer code");
        throw new RuntimeException();
    }
}
