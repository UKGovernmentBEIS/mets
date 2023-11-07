import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { ProcessAnalysisFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/process-analysis/process-analysis-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateProcessAnalysis: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ProcessAnalysisFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectProcessAnalysis,
    take(1),
    tap((processAnalysis) => {
      if (processAnalysis) {
        formProvider.setFormValue(processAnalysis);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateProcessAnalysis: CanActivateFn = () => {
  inject<ProcessAnalysisFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
