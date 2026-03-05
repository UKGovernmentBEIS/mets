import { Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { DoeDeterminationReasonSubTypePipe } from '@aviation/shared/pipes/doe-determination-reason-subtype.pipe';
import { DoeDeterminationReasonTypePipe } from '@aviation/shared/pipes/doe-determination-reason-type.pipe';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationDoECorsia } from 'pmrv-api';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-doe-emissions-summary-template',
  templateUrl: './doe-emissions-summary-template.component.html',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    DoeDeterminationReasonTypePipe,
    DoeDeterminationReasonSubTypePipe,
  ],
  viewProviders: [existingControlContainer],
})
export class DoeEmissionsSummaryTemplateComponent {
  @Input() data: AviationDoECorsia | null;
  @Input() files: { downloadUrl: string; fileName: string }[];
  @Input() isEditable = false;
  @Input() changeUrlQueryParams: Params = {};
}
