import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'specialProductsBasisDescription' })
export class SpecialProductsBasisDescriptionPipe implements PipeTransform {
  private readonly basisDescriptions: { [key: string]: string } = {
    F: 'Net fresh feed',
    'F (MNm3)': 'Net fresh feed (MNm3)',
    R: 'Reactor feed (includes recycle)',
    P: 'Product feed',
    'P (MNm3 O2)': 'Product feed (MNm3 O2)',
    SG: 'Synthesis gas production for POX units',
  };

  transform(basis: string): string {
    return this.basisDescriptions[basis] || basis;
  }
}
