import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'govuk-breadcrumbs',
  template: `
    <nav
      class="govuk-breadcrumbs"
      [class.govuk-breadcrumbs--collapse-on-mobile]="collapseOnMobile()"
      [class.govuk-breadcrumbs--inverse]="inverse()"
      [attr.aria-label]="labelText()">
      <ol class="govuk-breadcrumbs__list">
        <ng-content />
      </ol>
    </nav>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbsComponent {
  readonly inverse = input(false);
  readonly collapseOnMobile = input(true);
  readonly labelText = input('Breadcrumb');
}
