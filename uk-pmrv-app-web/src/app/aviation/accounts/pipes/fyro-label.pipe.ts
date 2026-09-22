import { Pipe, PipeTransform } from '@angular/core';

import { AviationAccountCreationDTO } from 'pmrv-api';

import { getFyroLabel } from '../utils/fyro.util';

@Pipe({
  name: 'fyroLabel',
  standalone: false,
})
export class FyroLabelPipe implements PipeTransform {
  transform(value: AviationAccountCreationDTO['emissionTradingScheme']): string {
    return getFyroLabel(value as 'UK_ETS_AVIATION' | 'CORSIA');
  }
}
