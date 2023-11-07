import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { GeneralInformationFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/general-information/general-information-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateGeneralInformation: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<GeneralInformationFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectGeneralInformation,
    take(1),
    tap((generalInformation) => {
      if (generalInformation) {
        formProvider.setFormValue(generalInformation);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateGeneralInformation: CanActivateFn = () => {
  inject<GeneralInformationFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
