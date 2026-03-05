import { Pipe, PipeTransform } from '@angular/core';

import { AviationAccountCreationDTO } from 'pmrv-api';

const ETS_NAMES_MAP = {
  CORSIA: 'CORSIA',
  UK_ETS_AVIATION: 'UK ETS',
};

@Pipe({
  name: 'etsName',
  pure: true,
})
export class EtsNamePipe implements PipeTransform {
  transform(value: AviationAccountCreationDTO['emissionTradingScheme']): string | null {
    if (value == null) {
      return null;
    }

    return ETS_NAMES_MAP[value] ?? null;
  }
}
