import { AfterContentInit, contentChild, Directive, ElementRef, input } from '@angular/core';

import { FieldsetHintDirective } from './fieldset-hint.directive';

@Directive({
  selector: 'fieldset[govukFieldset]',
  host: {
    '[class.govuk-fieldset]': 'fieldsetClass',
    '[attr.id]': 'identifier',
    '[attr.aria-describedby]': 'ariaDescribedby',
  },
})
export class FieldsetDirective implements AfterContentInit {
  readonly id = input('fieldset');
  readonly hint = contentChild(FieldsetHintDirective, { read: ElementRef });
  readonly fieldsetClass = true;

  get identifier() {
    return this.id();
  }

  get ariaDescribedby() {
    return this.hint() ? `${this.id()}-hint` : null;
  }

  ngAfterContentInit(): void {
    const hint = this.hint();
    if (hint) {
      hint.nativeElement.id = `${this.identifier}-hint`;
    }
  }
}
