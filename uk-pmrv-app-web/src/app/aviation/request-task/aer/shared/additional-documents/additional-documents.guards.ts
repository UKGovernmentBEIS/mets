import { inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';

import { RequestTaskStore } from '../../../store';
import { AerUkEtsStoreDelegate } from '../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { aerQuery } from '../../shared/aer.selectors';

export const canActivateAdditionalDocuments: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const form = inject<FormGroup>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return combineLatest([store.pipe(aerQuery.selectAer, take(1)), store.pipe(aerQuery.selectIsCorsia, take(1))]).pipe(
    map(([aer, isCorsia]) => {
      const initialState = isCorsia
        ? AerCorsiaStoreDelegate.INITIAL_STATE.additionalDocuments
        : AerUkEtsStoreDelegate.INITIAL_STATE.additionalDocuments;

      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            additionalDocuments: initialState,
          },
        } as any);
      }

      if (!aer?.additionalDocuments) {
        store.aerDelegate.setAdditionalDocuments(initialState);
      }

      if (aer?.additionalDocuments) {
        let value: any = { ...aer.additionalDocuments };
        if (aer.additionalDocuments.documents?.length) {
          const attachments = store.aerDelegate.payload.aerAttachments;
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
