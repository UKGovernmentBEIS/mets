import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'govukDate' })
export class GovukDatePipe implements PipeTransform {
  transform(
    date: string | Date,
    mode: 'date' | 'datetime' = 'date',
    locale?: string,
    options?: Intl.DateTimeFormatOptions,
  ): string {
    if (!date) {
      return '';
    }

    const dateObj = new Date(date);
    if (isNaN(dateObj.getTime())) {
      return '';
    }

    const dateFormatOptions: Intl.DateTimeFormatOptions = {
      timeZone: options?.timeZone ?? 'Europe/London',
      year: options?.year ?? 'numeric',
      month: options?.month ?? 'short',
      day: options?.day ?? 'numeric',
    };

    const timeFormatOptions: Intl.DateTimeFormatOptions = {
      hour: options?.hour ?? 'numeric',
      minute: options?.minute ?? '2-digit',
    };

    const dateTimeFormatOptions = mode === 'date' ? dateFormatOptions : { ...dateFormatOptions, ...timeFormatOptions };
    const formatter = Intl.DateTimeFormat(locale ?? 'en-GB-u-hc-h12', dateTimeFormatOptions);

    const dateString = formatter
      .formatToParts(dateObj)
      .map(({ value }, index) => (index === 9 ? '' : value))
      .join('');

    return dateString;
  }
}
