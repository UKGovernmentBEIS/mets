import { Pipe, PipeTransform } from '@angular/core';

import { WasteQDRRequestMetaData } from 'pmrv-api';

@Pipe({ name: 'quarterName', standalone: true })
export class QuarterNamePipe implements PipeTransform {
  transform(type: WasteQDRRequestMetaData['quarter']): string {
    switch (type) {
      case 'Q1':
        return 'January to March';
      case 'Q2':
        return 'April to June';
      case 'Q3':
        return 'July to September';
      case 'Q4':
        return 'October to December';

      default:
        return '';
    }
  }
}
