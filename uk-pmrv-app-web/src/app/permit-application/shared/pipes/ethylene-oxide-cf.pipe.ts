import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'ethyleneOxideCF' })
export class EthyleneOxideCFPipe implements PipeTransform {
  private readonly cfValues: { [key: string]: string } = {
    ETHYLEN_OXIDE: '1.000',
    MONOTHYLENE_GLYCOL: '0.710',
    DIETHYLENE_GLYCOL: '0.830',
    TRIETHYLENE_GLYCOL: '0.880',
  };

  transform(value: string): string | null {
    return this.cfValues[value] || null;
  }
}
