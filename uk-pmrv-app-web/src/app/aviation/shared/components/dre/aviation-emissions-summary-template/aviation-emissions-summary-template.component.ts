import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { AviationDreEmissionsTypePipe } from '@aviation/shared/pipes/aviation-dre-emissions-type.pipe';
import { DeterminationReasonTypePipe } from '@aviation/shared/pipes/determination-reason-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationDre } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-emissions-summary-template',
  templateUrl: './aviation-emissions-summary-template.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    AviationDreEmissionsTypePipe,
    DeterminationReasonTypePipe,
  ],
  viewProviders: [existingControlContainer],
})
export class AviationEmissionsSummaryTemplateComponent {
  @Input() data: AviationDre | null;
  @Input() files: { downloadUrl: string; fileName: string }[];
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
