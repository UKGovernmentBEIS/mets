import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'govuk-skip-link',
  imports: [RouterLink],
  template: `
    <a class="govuk-skip-link" [routerLink]="[]" [fragment]="anchor()">Skip to main content</a>
  `,
})
export class SkipLinkComponent {
  readonly anchor = input('main-content');
}
