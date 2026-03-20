import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-breadcrumbs',
  standalone: false,
  template: `
    <div class="govuk-breadcrumbs govuk-breadcrumbs--collapse-on-mobile">
      <ol class="govuk-breadcrumbs__list">
        <ng-content />
      </ol>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbsComponent {}
