import { Component } from '@angular/core';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { ProcedureFormComponent } from '../../../../shared/procedure-form';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-management-procedures-data-flow-form',
  templateUrl: './management-procedures-data-flow-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ProcedureFormComponent],
  providers: [DestroySubject],
  viewProviders: [existingControlContainer],
})
export class ManagementProceduresDataFlowFormComponent {
  getDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', 'attachment', uuid];
  }
}
