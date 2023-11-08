package uk.gov.pmrv.api.permit.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementDeviceType {
    BALANCE("Balance"),
    BELLOWS_METER("Bellows meter"),
    BELT_WEIGHER("Belt weigher"),
    CORIOLIS_METER("Coriolis meter"),
    ELECTRONIC_VOLUME_CONVERSION_INSTRUMENT("Electronic volume conversion instrument (EVCI)"),
    GAS_CHROMATOGRAPH("Gas chromatograph"),
    LEVEL_GAUGE("Level gauge"),
    ORIFICE_METER("Orifice meter"),
    OTHER("Other"),
    OVALRAD_METER("Ovalrad meter"),
    ROTARY_METER("Rotary meter"),
    TANK_DIP("Tank dip"),
    TURBINE_METER("Turbine meter"),
    ULTRASONIC_METER("Ultrasonic meter"),
    VENTURI_METER("Venturi meter"),
    VORTEX_METER("Vortex meter"),
    WEIGHBRIDGE("Weighbridge"),
    WEIGHSCALE("Weighscale");

    private String description;
}