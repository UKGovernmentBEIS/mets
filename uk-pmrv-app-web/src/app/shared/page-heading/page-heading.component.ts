import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-page-heading',
  template: `
    @if (caption()) {
      <span [class]="'govuk-caption-' + size()">{{ caption() }}</span>
    }
    <h1 [class]="'govuk-heading-' + size()">
      <ng-content></ng-content>
    </h1>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageHeadingComponent {
  readonly caption = input<string>();
  readonly size = input<'l' | 'xl'>('l');
}
