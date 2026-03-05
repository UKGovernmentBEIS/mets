import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'verificationReferenceTitle',
})
export class VerificationReferenceTitlePipe implements PipeTransform {
  transform(value: string): string {
    switch (true) {
      case value.startsWith('A'):
        return `${value}: an uncorrected error that remained before the verification report was issued`;
      case value.startsWith('B'):
        return `${value}: an uncorrected error in the monitoring plan`;
      case value.startsWith('C'):
        return `${value}: an uncorrected breach of the MRR, identified during verification`;
      case value.startsWith('D'):
        return `${value}: recommended improvement`;
      case value.startsWith('E'):
        return `${value}: an unresolved breach from a previous year`;
      default:
        return '';
    }
  }
}
