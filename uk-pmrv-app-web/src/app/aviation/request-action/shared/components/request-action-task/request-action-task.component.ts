import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { RequestActionDTO } from 'pmrv-api';

@Component({
  selector: 'app-request-action-task',
  standalone: true,
  imports: [CommonModule, GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-page-heading>{{ header }}</app-page-heading>
        <ng-content></ng-content>
        <app-return-to-link></app-return-to-link>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class RequestActionTaskComponent {
  @Input() header: string;
  @Input() requestActionType: RequestActionDTO['type'];
  @Input() breadcrumb: BreadcrumbItem[] | true;
}
