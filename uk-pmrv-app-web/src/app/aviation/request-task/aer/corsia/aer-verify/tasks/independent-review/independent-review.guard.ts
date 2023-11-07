import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { IndependentReviewFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateIndependentReview: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<IndependentReviewFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectIndependentReview,
    take(1),
    tap((independentReview) => {
      if (independentReview) {
        formProvider.setFormValue(independentReview);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateIndependentReview: CanActivateFn = () => {
  inject<IndependentReviewFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
