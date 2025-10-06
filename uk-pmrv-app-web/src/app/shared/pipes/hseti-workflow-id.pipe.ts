import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'hsetiWorkflowId' })
export class HsetiWorkFlowIdPipe implements PipeTransform {
  transform(value: string): string {
    if (value.startsWith('HSETI') && value.split('-')?.[1]?.split('_')?.[0].length === 2) {
      return value
        .split('-')
        .map((val, index) =>
          index === 1
            ? val
                .split('_')
                .map((y) => (y.length === 2 ? '20' + y : y))
                .join('_')
            : val,
        )
        .join('-');
    } else {
      return value;
    }
  }
}
