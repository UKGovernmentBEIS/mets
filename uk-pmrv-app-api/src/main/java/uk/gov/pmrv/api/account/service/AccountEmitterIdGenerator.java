package uk.gov.pmrv.api.account.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AccountEmitterIdGenerator {

    public static String generate(Long identifier) {
        return String.format("%s%s", "EM", String.format("%05d", identifier));
    }
}
