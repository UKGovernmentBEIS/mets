package uk.gov.pmrv.api.account.transform;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.constants.LocationConstants;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.LocationOffShore;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.LocationOnShoreState;
import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOffShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.AddressState;

import java.util.Locale;

/**
 * The Location Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface LocationMapper {

    LocationOnShore toLocationOnShore(LocationOnShoreDTO locationOnShoreDTO);

    LocationOnShoreDTO toAccountLocationOnShoreDTO(LocationOnShore locationOnShore);

    @AfterMapping
    default void setType(@MappingTarget LocationOnShoreDTO locationOnShoreDTO) {
        locationOnShoreDTO.setType(LocationType.ONSHORE);
    }

    @Mapping(target = "address", source = "locationOnShoreStateDTO")
    LocationOnShoreState toLocationOnShoreState(LocationOnShoreStateDTO locationOnShoreStateDTO);

    LocationOnShoreStateDTO toAccountLocationOnShoreStateDTO(AddressState address);

    @AfterMapping
    default void setType(@MappingTarget LocationOnShoreStateDTO locationOnShoreStateDTO) {
        locationOnShoreStateDTO.setType(LocationType.ONSHORE_STATE);
    }

    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    LocationOffShore toLocationOffShore(LocationOffShoreDTO locationOffShoreDTO);

    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    LocationOffShoreDTO toAccountLocationOffShoreDTO(LocationOffShore locationOffShore);

    @AfterMapping
    default void setLatLong(LocationOffShoreDTO locationOffShoreDTO, @MappingTarget LocationOffShore locationOffShore) {
        // Format to DMS
        String latitude = String.format(Locale.ROOT, LocationConstants.COORDINATES_FORMAT, locationOffShoreDTO.getLatitude().getDegree(),
                locationOffShoreDTO.getLatitude().getMinute(), locationOffShoreDTO.getLatitude().getSecond(),
                locationOffShoreDTO.getLatitude().getCardinalDirection().getName().charAt(0));

        String longitude = String.format(Locale.ROOT, LocationConstants.COORDINATES_FORMAT, locationOffShoreDTO.getLongitude().getDegree(),
                locationOffShoreDTO.getLongitude().getMinute(), locationOffShoreDTO.getLongitude().getSecond(),
                locationOffShoreDTO.getLongitude().getCardinalDirection().getName().charAt(0));

        locationOffShore.setLatitude(latitude);
        locationOffShore.setLongitude(longitude);
    }

    @AfterMapping
    default void getLatLongType(LocationOffShore locationOffShore, @MappingTarget LocationOffShoreDTO locationOffShoreDTO) {
        locationOffShoreDTO.setType(LocationType.OFFSHORE);

        String[] latitudeArray = locationOffShore.getLatitude().split(" ");
        CoordinatesDTO latitude = CoordinatesDTO.builder().degree(Integer.parseInt(latitudeArray[0]))
                .minute(Integer.parseInt(latitudeArray[1])).second(Double.parseDouble(latitudeArray[2]))
                .cardinalDirection(CardinalDirection.findCardinalDirection(latitudeArray[3].charAt(0))).build();

        String[] longitudeArray = locationOffShore.getLongitude().split(" ");
        CoordinatesDTO longitude = CoordinatesDTO.builder().degree(Integer.parseInt(longitudeArray[0]))
                .minute(Integer.parseInt(longitudeArray[1])).second(Double.parseDouble(longitudeArray[2]))
                .cardinalDirection(CardinalDirection.findCardinalDirection(longitudeArray[3].charAt(0))).build();

        locationOffShoreDTO.setLatitude(latitude);
        locationOffShoreDTO.setLongitude(longitude);
    }

    default Location toLocation(LocationDTO locationDTO) {
        return switch (locationDTO.getType()) {
            case ONSHORE -> this.toLocationOnShore((LocationOnShoreDTO) locationDTO);
            case OFFSHORE -> this.toLocationOffShore((LocationOffShoreDTO) locationDTO);
            case ONSHORE_STATE -> this.toLocationOnShoreState((LocationOnShoreStateDTO) locationDTO);
        };
    }

    default LocationDTO toAccountLocationDTO(Location location) {
        if (location instanceof HibernateProxy) {
            location = (Location) Hibernate.unproxy(location);
        }
        if(location instanceof LocationOnShore) {
            return this.toAccountLocationOnShoreDTO((LocationOnShore) location);
        }
        else if(location instanceof LocationOffShore){
            return this.toAccountLocationOffShoreDTO((LocationOffShore) location);
        }
        else {
            LocationOnShoreState locationOnShoreState = (LocationOnShoreState) location;
            return locationOnShoreState != null
                    ? this.toAccountLocationOnShoreStateDTO(locationOnShoreState.getAddress())
                    : null;
        }
    }

}
