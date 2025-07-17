import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CorsiaRequestTypes } from '@aviation/request-task/util';

import { empQuery } from '../emp.selectors';
import { AdditionalDocumentsFormProvider } from './additional-documents-form.provider';

export const canActivateAdditionalDocuments: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AdditionalDocumentsFormProvider>(TASK_FORM_PROVIDER);
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

        formProvider.setFormValue(value);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateAdditionalDocuments: CanDeactivateFn<unknown> = () => {
  inject<AdditionalDocumentsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
