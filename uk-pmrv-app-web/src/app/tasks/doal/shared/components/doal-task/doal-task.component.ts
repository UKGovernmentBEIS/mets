import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';

@Component({
  selector: 'app-doal-task',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DoalTaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
}
