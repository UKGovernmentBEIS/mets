import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';

@Component({
  selector: 'app-doal-task',
  standalone: false,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalTaskComponent {
  @Input() notification: any;
  @Input() breadcrumb: BreadcrumbItem[] | true;
}
