import { Pipe, PipeTransform } from '@angular/core';

import { format } from '@shared/utils/bignumber.utils';
import BigNumber from 'bignumber.js';

@Pipe({
  name: 'bigNumber',
  standalone: false,
})
export class BigNumberPipe implements PipeTransform {
  transform(value: string | number, maxDecimals = 5): string {
    return format(new BigNumber(value), maxDecimals);
  }
}
