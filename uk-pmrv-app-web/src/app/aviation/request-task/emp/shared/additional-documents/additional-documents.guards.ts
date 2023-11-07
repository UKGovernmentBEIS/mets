import { inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { CorsiaRequestTypes } from '@aviation/request-task/util';

import { RequestTaskStore } from '../../../store';
import { EmpUkEtsStoreDelegate } from '../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { empQuery } from '../../shared/emp.selectors';

export const canActivateAdditionalDocuments: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const form = inject<FormGroup>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;
  const isCorsia = CorsiaRequestTypes.includes(store.getState().requestTaskItem.requestInfo.type);

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      const initDocs = isCorsia
        ? EmpCorsiaStoreDelegate.INITIAL_STATE.additionalDocuments
        : EmpUkEtsStoreDelegate.INITIAL_STATE.additionalDocuments;
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            additionalDocuments: initDocs,
          },
        } as any);
      }

      if (!emp?.additionalDocuments) {
        store.empDelegate.setAdditionalDocuments(initDocs);
      }

      if (emp.additionalDocuments) {
        let value: any = { ...emp.additionalDocuments };
        if (emp.additionalDocuments.documents?.length) {
          const attachments = store.empDelegate.payload.empAttachments;
          value = {
            ...value,
            documents: value.documents.map((doc) => ({
              file: { name: attachments[doc] },
              uuid: doc,
            })),
          };
        }
        form.patchValue(value);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateAdditionalDocuments: CanDeactivateFn<unknown> = () => {
  inject<FormGroup>(TASK_FORM_PROVIDER).reset();
  return true;
};
