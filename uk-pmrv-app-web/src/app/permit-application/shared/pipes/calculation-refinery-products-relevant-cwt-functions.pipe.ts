import { Pipe, PipeTransform } from '@angular/core';

import { RefineryProductsSP } from 'pmrv-api';

export type CWTFunctionKey = RefineryProductsSP['refineryProductsRelevantCWTFunctions'][number];

export interface CWTFunctionData {
  key: CWTFunctionKey;
  description: string;
  basis: string;
  cwtFactor: number;
}

@Pipe({ name: 'calculationRefineryProductsRelevantCWTFunctions' })
export class CalculationRefineryProductsRelevantCWTFunctionsPipe implements PipeTransform {
  private readonly calculationRefineryProductsRelevantCWTFunctions: Record<
    CWTFunctionKey,
    { description: string; basis: string; cwtFactor: number }
  > = {
    ATMOSPHERIC_CRUDE_DISTILLATION: {
      description: 'Atmospheric Crude Distillation',
      basis: 'F',
      cwtFactor: 1.0,
    },
    VACUUM_DISTILLATION: {
      description: 'Vacuum Distillation',
      basis: 'F',
      cwtFactor: 0.85,
    },
    SOLVENT_DEASPHALTING: {
      description: 'Solvent Deasphalthing',
      basis: 'F',
      cwtFactor: 2.45,
    },
    VISBREAKING: {
      description: 'Visbreaking',
      basis: 'F',
      cwtFactor: 1.4,
    },
    THERMAL_CRACKING: {
      description: 'Thermal Cracking',
      basis: 'F',
      cwtFactor: 2.7,
    },
    DELAYED_COKING: {
      description: 'Delayed Coking',
      basis: 'F',
      cwtFactor: 2.2,
    },
    FLUID_COKING: {
      description: 'Fluid Coking',
      basis: 'F',
      cwtFactor: 7.6,
    },
    FLEXICOKING: {
      description: 'Flexicoking',
      basis: 'F',
      cwtFactor: 16.6,
    },
    COKE_CALCINING: {
      description: 'Coke Calcining',
      basis: 'P',
      cwtFactor: 12.75,
    },
    FLUID_CATALYTIC_CRACKING: {
      description: 'Fluid Catalytic Cracking',
      basis: 'F',
      cwtFactor: 5.5,
    },
    OTHER_CATALYTIC_CRACKING: {
      description: 'Other Catalytic Cracking',
      basis: 'F',
      cwtFactor: 4.1,
    },
    DISTILLATE_GASOIL_HYDROCRACKING: {
      description: 'Distillate / Gasoil Hydrocracking',
      basis: 'F',
      cwtFactor: 2.85,
    },
    RESIDUAL_HYDROCRACKING: {
      description: 'Residual Hydrocracking',
      basis: 'F',
      cwtFactor: 3.75,
    },
    NAPHTHA_GASOLINE_HYDROTREATING: {
      description: 'Naphtha/Gasoline Hydrotreating',
      basis: 'F',
      cwtFactor: 1.1,
    },
    KEROSENE_DIESEL_HYDROTREATING: {
      description: 'Kerosene/ Diesel Hydrotreating',
      basis: 'F',
      cwtFactor: 0.9,
    },
    RESIDUAL_HYDROTREATING: {
      description: 'Residual Hydrotreating',
      basis: 'F',
      cwtFactor: 1.55,
    },
    VGO_HYDROTREATING: {
      description: 'VGO Hydrotreating',
      basis: 'F',
      cwtFactor: 0.9,
    },
    HYDROGEN_PRODUCTION: {
      description: 'Hydrogen Production',
      basis: 'P',
      cwtFactor: 300.0,
    },
    CATALYTIC_REFORMING: {
      description: 'Catalytic Reforming',
      basis: 'F',
      cwtFactor: 4.95,
    },
    ALKYLATION: {
      description: 'Alkylation',
      basis: 'P',
      cwtFactor: 7.25,
    },
    C4_ISOMERISATION: {
      description: 'C4 Isomerisation',
      basis: 'R',
      cwtFactor: 3.25,
    },
    C5_C6_ISOMERISATION: {
      description: 'C5/C6 Isomerisation',
      basis: 'R',
      cwtFactor: 2.85,
    },
    OXYGENATE_PRODUCTION: {
      description: 'Oxygenate Production',
      basis: 'P',
      cwtFactor: 5.6,
    },
    PROPYLENE_PRODUCTION: {
      description: 'Propylene Production',
      basis: 'F',
      cwtFactor: 3.45,
    },
    ASPHALT_MANUFACTURE: {
      description: 'Asphalt Manufacture',
      basis: 'P',
      cwtFactor: 2.1,
    },
    POLYMER_MODIFIED_ASPHALT_BLENDING: {
      description: 'Polymer-Modified Asphalt Blending',
      basis: 'P',
      cwtFactor: 0.55,
    },
    SULPHUR_RECOVERY: {
      description: 'Sulphur Recovery',
      basis: 'P',
      cwtFactor: 18.6,
    },
    AROMATIC_SOLVENT_EXTRACTION: {
      description: 'Aromatic Solvent Extraction',
      basis: 'F',
      cwtFactor: 5.25,
    },
    HYDRODEALKYLATION: {
      description: 'Hydrodealkylation',
      basis: 'F',
      cwtFactor: 2.45,
    },
    TDP_TDA: {
      description: 'TDP/ TDA',
      basis: 'F',
      cwtFactor: 1.85,
    },
    CYCLOHEXANE_PRODUCTION: {
      description: 'Cyclohexane production',
      basis: 'P',
      cwtFactor: 3.0,
    },
    XYLENE_ISOMERISATION: {
      description: 'Xylene Isomerisation',
      basis: 'F',
      cwtFactor: 1.85,
    },
    PARAXYLENE_PRODUCTION: {
      description: 'Paraxylene production',
      basis: 'P',
      cwtFactor: 6.4,
    },
    METAXYLENE_PRODUCTION: {
      description: 'Metaxylene production',
      basis: 'P',
      cwtFactor: 11.1,
    },
    PHTHALIC_ANHYDRIDE_PRODUCTION: {
      description: 'Phthalic anhydride production',
      basis: 'P',
      cwtFactor: 14.4,
    },
    MALEIC_ANHYDRIDE_PRODUCTION: {
      description: 'Maleic anhydride production',
      basis: 'P',
      cwtFactor: 20.8,
    },
    ETHYLBENZENE_PRODUCTION: {
      description: 'Ethylbenzene production',
      basis: 'P',
      cwtFactor: 1.55,
    },
    CUMENE_PRODUCTION: {
      description: 'Cumene production',
      basis: 'P',
      cwtFactor: 5.0,
    },
    PHENOL_PRODUCTION: {
      description: 'Phenol production',
      basis: 'P',
      cwtFactor: 1.15,
    },
    LUBE_SOLVENT_EXTRACTION: {
      description: 'Lube solvent extraction',
      basis: 'F',
      cwtFactor: 2.1,
    },
    LUBE_SOLVENT_DEWAXING: {
      description: 'Lube solvent dewaxing',
      basis: 'F',
      cwtFactor: 4.55,
    },
    CATALYTIC_WAX_ISOMERISATION: {
      description: 'Catalytic Wax Isomerisation',
      basis: 'F',
      cwtFactor: 1.6,
    },
    LUBE_HYDROCRACKER: {
      description: 'Lube Hydrocracker',
      basis: 'F',
      cwtFactor: 2.5,
    },
    WAX_DEOILING: {
      description: 'Wax Deoiling',
      basis: 'P',
      cwtFactor: 12.0,
    },
    LUBE_WAX_HYDROTREATING: {
      description: 'Lube/Wax Hydrotreating',
      basis: 'F',
      cwtFactor: 1.15,
    },
    SOLVENT_HYDROTREATING: {
      description: 'Solvent Hydrotreating',
      basis: 'F',
      cwtFactor: 1.25,
    },
    SOLVENT_FRACTIONATION: {
      description: 'Solvent Fractionation',
      basis: 'F',
      cwtFactor: 0.9,
    },
    MOL_SIEVE_C10_PLUS_PARAFFINS: {
      description: 'Mol sieve for C10+ paraffins',
      basis: 'P',
      cwtFactor: 1.85,
    },
    PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_FUEL: {
      description: 'Partial Oxidation of Residual Feeds (POX) for Fuel',
      basis: 'SG',
      cwtFactor: 8.2,
    },
    PARTIAL_OXIDATION_RESIDUAL_FEEDS_POX_HYDROGEN_METHANOL: {
      description: 'Partial Oxidation of Residual Feeds (POX) for Hydrogen or Methanol',
      basis: 'SG',
      cwtFactor: 44.0,
    },
    METHANOL_FROM_SYNGAS: {
      description: 'Methanol from syngas',
      basis: 'P',
      cwtFactor: -36.2,
    },
    AIR_SEPARATION: {
      description: 'Air Separation',
      basis: 'P (MNm3 O2)',
      cwtFactor: 8.8,
    },
    FRACTIONATION_PURCHASED_NGL: {
      description: 'Fractionation of purchased NGL',
      basis: 'F',
      cwtFactor: 1.0,
    },
    FLUE_GAS_TREATMENT: {
      description: 'Flue gas treatment',
      basis: 'F (MNm3)',
      cwtFactor: 0.1,
    },
    TREATMENT_COMPRESSION_FUEL_GAS_SALES: {
      description: 'Treatment and Compression of Fuel Gas for Sales',
      basis: 'kW',
      cwtFactor: 0.15,
    },
    SEAWATER_DESALINATION: {
      description: 'Seawater Desalination',
      basis: 'P',
      cwtFactor: 1.15,
    },
  };

  transform(funcs: CWTFunctionKey[]): CWTFunctionData[] {
    if (!funcs || funcs.length === 0) {
      return [];
    }

    return funcs
      .map((func) => {
        const data = this.calculationRefineryProductsRelevantCWTFunctions[func];
        if (data) {
          return { key: func, ...data };
        }
        return null;
      })
      .filter((item): item is CWTFunctionData => item !== null);
  }
}
