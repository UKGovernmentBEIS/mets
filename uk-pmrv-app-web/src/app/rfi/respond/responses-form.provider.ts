import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../shared/services/request-task-file-service/request-task-file.service';
import { RfiStore } from '../store/rfi.store';

export const RFI_FORM = new InjectionToken<UntypedFormGroup>('Rfi form');

export const responseFormProvider = {
  provide: RFI_FORM,
  deps: [UntypedFormBuilder, RequestTaskFileService, RfiStore],
  useFactory: (fb: UntypedFormBuilder, requestTaskFileService: RequestTaskFileService, rfiStore: RfiStore) => {
    const disabled = !rfiStore.getValue().isEditable;
    const questions = rfiStore.getValue().rfiQuestionPayload?.questions || [];
    const regulatorFiles = rfiStore.getValue().rfiQuestionPayload?.files || [];
    const responses = rfiStore.getValue().rfiResponsePayload?.answers || [];
    const files = rfiStore.getValue().rfiResponsePayload?.files || [];
    const pairs = questions.map((q, index) => [q, responses[index]]);

    return fb.group({
      pairs: fb.array(pairs.map((p) => createAnotherPair(p, disabled))),
      regulatorFiles: requestTaskFileService.buildFormControl(
        rfiStore.getState().requestTaskId,
        regulatorFiles,
        rfiStore.getState().rfiAttachments,
        'RFI_UPLOAD_ATTACHMENT',
        false,
        true,
      ),
      files: requestTaskFileService.buildFormControl(
        rfiStore.getState().requestTaskId,
        files,
        rfiStore.getState().rfiAttachments,
        'RFI_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};

export function createAnotherPair(pairs: string[], disabled: boolean): UntypedFormGroup {
  return new UntypedFormGroup({
    question: new UntypedFormControl({
      value: pairs?.[0] || null,
      disabled: true,
    }),
    response: new UntypedFormControl(
      {
        value: pairs?.[1] || null,
        disabled: disabled,
      },
      [GovukValidators.required('Enter a response'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
    ),
  });
}
