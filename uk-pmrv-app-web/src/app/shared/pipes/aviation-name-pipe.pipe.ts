import { Pipe, PipeTransform } from '@angular/core';

import { AviationAccountCreationDTO } from 'pmrv-api';

const ETS_NAMES_MAP = {
  CORSIA: 'CORSIA',
  UK_ETS_AVIATION: 'UK ETS',
  UK_ETS_INSTALLATIONS: 'UK ETS',
  EU_ETS_INSTALLATIONS: 'EU ETS',
};

@Pipe({
  name: 'aviationNamePipe',
})
export class AviationNamePipePipe implements PipeTransform {
  transform(value: AviationAccountCreationDTO['emissionTradingScheme']): string | null {
    if (value == null) {
      return null;
    }

    return ETS_NAMES_MAP[value] ?? null;
  }
}
