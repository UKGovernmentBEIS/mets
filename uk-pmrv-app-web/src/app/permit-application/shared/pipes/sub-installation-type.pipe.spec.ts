import { SubInstallationTypePipe } from './sub-installation-type.pipe';

describe('SubInstallationTypePipe', () => {
  const pipe = new SubInstallationTypePipe();
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform the stream description', () => {
    expect(pipe.transform('PRIMARY_ALUMINIUM')).toEqual('[Primary] Aluminium');
    expect(pipe.transform('ADIPIC_ACID')).toEqual('Adipic acid');
    expect(pipe.transform('AMMONIA')).toEqual('Ammonia');
    expect(pipe.transform('AROMATICS')).toEqual('Aromatics');
    expect(pipe.transform('BOTTLES_AND_JARS_OF_COLOURED_GLASS')).toEqual('Bottles and jars of coloured glass');
    expect(pipe.transform('BOTTLES_AND_JARS_OF_COLOURLESS_GLASS')).toEqual('Bottles and jars of colourless glass');
    expect(pipe.transform('CARBON_BLACK')).toEqual('Carbon black');
    expect(pipe.transform('COATED_CARTON_BOARD')).toEqual('Coated carton board');
    expect(pipe.transform('COATED_FINE_PAPER')).toEqual('Coated fine paper');
    expect(pipe.transform('COKE')).toEqual('Coke');
    expect(pipe.transform('CONTINUOUS_FILAMENT_GLASS_FIBRE_PRODUCTS')).toEqual(
      'Continuous filament glass fibre products',
    );
    expect(pipe.transform('DOLIME')).toEqual('Dolime');
    expect(pipe.transform('DRIED_SECONDARY_GYPSUM')).toEqual('Dried secondary gypsum');
    expect(pipe.transform('EAF_CARBON_STEEL')).toEqual('EAF carbon steel');
    expect(pipe.transform('EAF_HIGH_ALLOY_STEEL')).toEqual('EAF high alloy steel');
    expect(pipe.transform('E_PVC')).toEqual('E-PVC');
    expect(pipe.transform('ETHYLENE_OXIDE_ETHYLENE_GLYCOLS')).toEqual('Ethylene oxide/ ethylene glycols');
    expect(pipe.transform('FACING_BRICKS')).toEqual('Facing bricks');
    expect(pipe.transform('FLOAT_GLASS')).toEqual('Float glass');
    expect(pipe.transform('GREY_CEMENT_CLINKER')).toEqual('Grey cement clinker');
    expect(pipe.transform('HOT_METAL')).toEqual('Hot metal');
    expect(pipe.transform('HYDROGEN')).toEqual('Hydrogen');
    expect(pipe.transform('IRON_CASTING')).toEqual('Iron casting');
    expect(pipe.transform('LIME')).toEqual('Lime');
    expect(pipe.transform('LONG_FIBRE_KRAFT_PULP')).toEqual('Long fibre kraft pulp');
    expect(pipe.transform('MINERAL_WOOL')).toEqual('Mineral wool');
    expect(pipe.transform('NEWSPRINT')).toEqual('Newsprint');
    expect(pipe.transform('NITRIC_ACID')).toEqual('Nitric acid');
    expect(pipe.transform('PAVERS')).toEqual('Pavers');
    expect(pipe.transform('PHENOL_ACETONE')).toEqual('Phenol/ acetone');
    expect(pipe.transform('PLASTER')).toEqual('Plaster');
    expect(pipe.transform('PLASTERBOARD')).toEqual('Plasterboard');
    expect(pipe.transform('PRE_BAKE_ANODE')).toEqual('Pre-bake anode');
    expect(pipe.transform('RECOVERED_PAPER_PULP')).toEqual('Recovered paper pulp');
    expect(pipe.transform('REFINERY_PRODUCTS')).toEqual('Refinery products');
    expect(pipe.transform('ROOF_TILES')).toEqual('Roof tiles');
    expect(pipe.transform('SHORT_FIBRE_KRAFT_PULP')).toEqual('Short fibre kraft pulp');
    expect(pipe.transform('SINTERED_DOLIME')).toEqual('Sintered dolime');
    expect(pipe.transform('SINTERED_ORE')).toEqual('Sintered ore');
    expect(pipe.transform('SODA_ASH')).toEqual('Soda ash');
    expect(pipe.transform('SPRAY_DRIED_POWDER')).toEqual('Spray dried powder');
    expect(pipe.transform('S_PVC')).toEqual('S-PVC');
    expect(pipe.transform('STEAM_CRACKING')).toEqual('Steam cracking');
    expect(pipe.transform('STYRENE')).toEqual('Styrene');
    expect(pipe.transform('SULPHITE_PULP_THERMO_MECHANICAL_AND_MECHANICAL_PULP')).toEqual(
      'Sulphite pulp, thermo-mechanical and mechanical pulp',
    );
    expect(pipe.transform('SYNTHESIS_GAS')).toEqual('Synthesis gas');
    expect(pipe.transform('TESTLINER_AND_FLUTING')).toEqual('Testliner and fluting');
    expect(pipe.transform('TISSUE')).toEqual('Tissue');
    expect(pipe.transform('UNCOATED_CARTON_BOARD')).toEqual('Uncoated carton board');
    expect(pipe.transform('UNCOATED_FINE_PAPER')).toEqual('Uncoated fine paper');
    expect(pipe.transform('VINYL_CHLORIDE_MONOMER')).toEqual('Vinyl chloride monomer');
    expect(pipe.transform('WHITE_CEMENT_CLINKER')).toEqual('White cement clinker');
    expect(pipe.transform('HEAT_BENCHMARK_CL')).toEqual('Heat benchmark exposed to carbon leakage');
    expect(pipe.transform('HEAT_BENCHMARK_NON_CL')).toEqual('Heat benchmark not exposed to carbon leakage');
    expect(pipe.transform('DISTRICT_HEATING_NON_CL')).toEqual('District Heating');
    expect(pipe.transform('FUEL_BENCHMARK_CL')).toEqual('Fuel benchmark exposed to carbon leakage');
    expect(pipe.transform('FUEL_BENCHMARK_NON_CL')).toEqual('Fuel benchmark not exposed to carbon leakage');
    expect(pipe.transform('PROCESS_EMISSIONS_CL')).toEqual('Process emissions exposed to carbon leakage');
    expect(pipe.transform('PROCESS_EMISSIONS_NON_CL')).toEqual('Process emissions not exposed to carbon leakage');
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
