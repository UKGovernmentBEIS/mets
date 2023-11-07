import { UntypedFormBuilder, ValidationErrors, ValidatorFn } from '@angular/forms';

import { InherentCO2MonitoringApproach, InherentReceivingTransferringInstallation } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { isWizardComplete } from './inherent-co2-wizard';

export const inherentCO2FormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();

    const installations = (state.permit.monitoringApproaches?.INHERENT_CO2 as InherentCO2MonitoringApproach)
      ?.inherentReceivingTransferringInstallations;

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

    return !isEveryWizarCompleted ? { notAllWizardsCompleted: 'Not all items are complete' } : null;
  };
}
