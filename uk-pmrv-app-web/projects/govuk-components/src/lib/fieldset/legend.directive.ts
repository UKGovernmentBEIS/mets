import { AfterContentInit, Directive, ElementRef, inject, input } from '@angular/core';

import { FieldsetDirective } from './fieldset.directive';
import { LegendSizeType } from './legend-size.type';

@Directive({
  selector: 'legend[govukLegend],ng-template[govukLegend]',
  host: { '[class]': 'sizeClass', '[attr.id]': 'identifier' },
})
export class LegendDirective implements AfterContentInit {
  private readonly fieldset = inject(FieldsetDirective);
  private readonly elementRef = inject(ElementRef);

  readonly legendSize = input<LegendSizeType>();

  get sizeClass() {
    switch (this.legendSize()) {
      case 'heading':
      case 'large':
        return 'govuk-fieldset__legend govuk-fieldset__legend--l';
      case 'medium':
        return 'govuk-fieldset__legend govuk-fieldset__legend--m';
      case 'small':
        return 'govuk-fieldset__legend govuk-fieldset__legend--s';
      default:
        return 'govuk-fieldset__legend';
    }
  }

  get identifier() {
    return `l.${this.fieldset.identifier}`;
  }

  private get nativeElement(): HTMLLegendElement {
    return this.elementRef.nativeElement;
  }

  ngAfterContentInit(): void {
    const heading = this.nativeElement.querySelector('h1');

    if (heading && this.legendSize() === 'heading') {
      heading.classList.add('govuk-fieldset__heading');
    }
  }
}
