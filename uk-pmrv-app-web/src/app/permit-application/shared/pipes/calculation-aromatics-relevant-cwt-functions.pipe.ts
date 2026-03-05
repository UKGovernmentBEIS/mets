import { Pipe, PipeTransform } from '@angular/core';

import { AromaticsSP } from 'pmrv-api';

export type CWTFunctionKey = AromaticsSP['relevantCWTFunctions'][number];

export interface CWTFunctionData {
  key: CWTFunctionKey;
  description: string;
  basis: string;
  cwtFactor: number;
}

@Pipe({ name: 'calculationAromaticsRelevantCWTFunctions' })
export class CalculationAromaticsRelevantCWTFunctionsPipe implements PipeTransform {
  private readonly relevantCWTFunctions: Record<
    CWTFunctionKey,
    { description: string; basis: string; cwtFactor: number }
  > = {
    NAPHTHA_GASOLINE_HYDROTREATER: {
      description: 'Naphtha/Gasoline Hydrotreater',
      basis: 'F',
      cwtFactor: 1.1,
    },
    AROMATIC_SOLVENT_EXTRACTION: {
      description: 'Aromatic Solvent Extraction',
      basis: 'F',
      cwtFactor: 5.25,
    },
    TDP_TDA: {
      description: 'TDP/ TDA',
      basis: 'F',
      cwtFactor: 1.85,
    },
    HYDRODEALKYLATION: {
      description: 'Hydrodealkylation',
      basis: 'F',
      cwtFactor: 2.45,
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
    CYCLOHEXANE_PRODUCTION: {
      description: 'Cyclohexane production',
      basis: 'P',
      cwtFactor: 3.0,
    },
    CUMENE_PRODUCTION: {
      description: 'Cumene production',
      basis: 'P',
      cwtFactor: 5.0,
    },
  };

  transform(funcs: CWTFunctionKey[]): CWTFunctionData[] {
    if (!funcs || funcs.length === 0) {
      return [];
    }

    return funcs
      .map((func) => {
        const data = this.relevantCWTFunctions[func];
        if (data) {
          return { key: func, ...data };
        }
        return null;
      })
      .filter((item): item is CWTFunctionData => item !== null);
  }
}
