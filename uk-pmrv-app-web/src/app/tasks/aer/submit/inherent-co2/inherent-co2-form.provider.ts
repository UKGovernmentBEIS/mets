import { UntypedFormBuilder, ValidationErrors, ValidatorFn } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { wizardItemsNotCompleted } from '@tasks/aer/submit/inherent-co2/errors/inherent-co2-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AerApplicationSubmitRequestTaskPayload,
  InherentCO2Emissions,
  InherentReceivingTransferringInstallation,
} from 'pmrv-api';

import { isWizardComplete } from './inherent-co2-wizard';

export const inherentCO2FormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const aer = (store.getValue().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer;
    const installations = (
      aer.monitoringApproachEmissions?.INHERENT_CO2 as InherentCO2Emissions
    )?.inherentReceivingTransferringInstallations?.map(
      (aerInherent) => aerInherent?.inherentReceivingTransferringInstallation,
    );

    return fb.group(
      {},
      {
        validators: [allWizardsCompleted(installations)],
      },
    );
  },
};

function allWizardsCompleted(installations: InherentReceivingTransferringInstallation[]): ValidatorFn {
  return (): ValidationErrors => {
    const isEveryWizarCompleted = installations?.every((installation: InherentReceivingTransferringInstallation) =>
      isWizardComplete(installation),
    );

    return !isEveryWizarCompleted ? { notAllWizardsCompleted: wizardItemsNotCompleted } : null;
  };
}
