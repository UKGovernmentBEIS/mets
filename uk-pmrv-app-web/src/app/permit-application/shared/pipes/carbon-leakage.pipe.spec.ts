import { CarbonLeakagePipe } from './carbon-leakage.pipe';

describe('CarbonLeakagePipe', () => {
  const pipe = new CarbonLeakagePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('PRIMARY_ALUMINIUM')).toEqual('Exposed');
    expect(pipe.transform('ADIPIC_ACID')).toEqual('Exposed');
    expect(pipe.transform('AMMONIA')).toEqual('Exposed');
    expect(pipe.transform('AROMATICS')).toEqual('Exposed');
    expect(pipe.transform('BOTTLES_AND_JARS_OF_COLOURED_GLASS')).toEqual('Exposed');
    expect(pipe.transform('BOTTLES_AND_JARS_OF_COLOURLESS_GLASS')).toEqual('Exposed');
    expect(pipe.transform('CARBON_BLACK')).toEqual('Exposed');
    expect(pipe.transform('COATED_CARTON_BOARD')).toEqual('Exposed');
    expect(pipe.transform('COATED_FINE_PAPER')).toEqual('Exposed');
    expect(pipe.transform('COKE')).toEqual('Exposed');
    expect(pipe.transform('CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS')).toEqual('Exposed');
    expect(pipe.transform('DOLIME')).toEqual('Exposed');
    expect(pipe.transform('DRIED_SECONDARY_GYPSUM')).toEqual('Exposed');
    expect(pipe.transform('EAF_CARBON_STEEL')).toEqual('Exposed');
    expect(pipe.transform('EAF_HIGH_ALLOY_STEEL')).toEqual('Exposed');
    expect(pipe.transform('E_PVC')).toEqual('Exposed');
    expect(pipe.transform('ETHYLENE_OXIDE_ETHYLENE_GLYCOLS')).toEqual('Exposed');
    expect(pipe.transform('FACING_BRICKS')).toEqual('Exposed');
    expect(pipe.transform('FLOAT_GLASS')).toEqual('Exposed');
    expect(pipe.transform('GREY_CEMENT_CLINKER')).toEqual('Exposed');
    expect(pipe.transform('HOT_METAL')).toEqual('Exposed');
    expect(pipe.transform('HYDROGEN')).toEqual('Exposed');
    expect(pipe.transform('IRON_CASTING')).toEqual('Exposed');
    expect(pipe.transform('LIME')).toEqual('Exposed');
    expect(pipe.transform('LONG_FIBRE_KRAFT_PULP')).toEqual('Exposed');
    expect(pipe.transform('MINERAL_WOOL')).toEqual('Exposed');
    expect(pipe.transform('NEWSPRINT')).toEqual('Exposed');
    expect(pipe.transform('NITRIC_ACID')).toEqual('Exposed');
    expect(pipe.transform('PAVERS')).toEqual('Exposed');
    expect(pipe.transform('PHENOL_ACETONE')).toEqual('Exposed');
    expect(pipe.transform('PLASTER')).toEqual('Exposed');
    expect(pipe.transform('PRE_BAKE_ANODE')).toEqual('Exposed');
    expect(pipe.transform('RECOVERED_PAPER_PULP')).toEqual('Exposed');
    expect(pipe.transform('REFINERY_PRODUCTS')).toEqual('Exposed');
    expect(pipe.transform('ROOF_TILES')).toEqual('Exposed');
    expect(pipe.transform('SHORT_FIBRE_KRAFT_PULP')).toEqual('Exposed');
    expect(pipe.transform('SINTERED_DOLIME')).toEqual('Exposed');
    expect(pipe.transform('SINTERED_ORE')).toEqual('Exposed');
    expect(pipe.transform('SODA_ASH')).toEqual('Exposed');
    expect(pipe.transform('SPRAY_DRIED_POWDER')).toEqual('Exposed');
    expect(pipe.transform('S_PVC')).toEqual('Exposed');
    expect(pipe.transform('STEAM_CRACKING')).toEqual('Exposed');
    expect(pipe.transform('STYRENE')).toEqual('Exposed');
    expect(pipe.transform('SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP')).toEqual('Exposed');
    expect(pipe.transform('SYNTHESIS_GAS')).toEqual('Exposed');
    expect(pipe.transform('TESTLINER_AND_FLUTING')).toEqual('Exposed');
    expect(pipe.transform('TISSUE')).toEqual('Exposed');
    expect(pipe.transform('UNCOATED_CARTON_BOARD')).toEqual('Exposed');
    expect(pipe.transform('UNCOATED_FINE_PAPER')).toEqual('Exposed');
    expect(pipe.transform('VINYL_CHLORIDE_MONOMER')).toEqual('Exposed');
    expect(pipe.transform('WHITE_CEMENT_CLINKER')).toEqual('Exposed');
    expect(pipe.transform('HEAT_BENCHMARK_CL')).toEqual('Exposed');
    expect(pipe.transform('FUEL_BENCHMARK_CL')).toEqual('Exposed');
    expect(pipe.transform('PROCESS_EMISSIONS_CL')).toEqual('Exposed');

    expect(pipe.transform('PLASTERBOARD')).toEqual('Not exposed');
    expect(pipe.transform('HEAT_BENCHMARK_NON_CL')).toEqual('Not exposed');
    expect(pipe.transform('DISTRICT_HEATING_NON_CL')).toEqual('Not exposed');
    expect(pipe.transform('FUEL_BENCHMARK_NON_CL')).toEqual('Not exposed');
    expect(pipe.transform('PROCESS_EMISSIONS_NON_CL')).toEqual('Not exposed');
  });

  it('should handle empty value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should handle undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });
});
