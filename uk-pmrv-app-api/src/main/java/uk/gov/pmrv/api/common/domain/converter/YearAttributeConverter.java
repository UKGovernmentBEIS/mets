package uk.gov.pmrv.api.common.domain.converter;

import jakarta.persistence.AttributeConverter;
import java.time.Year;

public class YearAttributeConverter implements AttributeConverter<Year, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Year year) {
        return year.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Integer value) {
        return Year.of(value);
    }
}
