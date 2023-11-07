import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { ProcedureFormComponent } from '../../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

export const PLAN_FORM = new InjectionToken<UntypedFormGroup>('Procedure plan task form');

export const planFormProvider = {
  provide: PLAN_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const value = (state.permit.monitoringApproaches?.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
      .samplingPlan?.details?.procedurePlan;

    return fb.group({
      procedurePlan: fb.group(ProcedureFormComponent.controlsFactory(value)),
      planIds: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        value?.procedurePlanIds ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
