package uk.gov.pmrv.api.account.transform;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Component
public class StringToAccountTypeEnumConverter implements Converter<String, AccountType> {
    @Override
    public AccountType convert(String source) {
        return AccountType.valueOf(source.toUpperCase());
    }
}
