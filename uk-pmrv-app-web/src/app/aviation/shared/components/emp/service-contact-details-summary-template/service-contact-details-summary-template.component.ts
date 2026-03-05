import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ServiceContactDetails } from 'pmrv-api';

@Component({
  selector: 'app-service-contact-details-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, SharedModule],
  templateUrl: './service-contact-details-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceContactDetailsSummaryTemplateComponent {
  @Input() data: ServiceContactDetails;
}
