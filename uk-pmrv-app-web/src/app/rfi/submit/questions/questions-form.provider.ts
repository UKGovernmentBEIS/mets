import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../../shared/services/request-task-file-service/request-task-file.service';
import { RfiStore } from '../../store/rfi.store';

export const RFI_FORM = new InjectionToken<UntypedFormGroup>('Rfi form');

export const questionFormProvider = {
  provide: RFI_FORM,
  deps: [UntypedFormBuilder, RequestTaskFileService, RfiStore],
  useFactory: (fb: UntypedFormBuilder, requestTaskFileService: RequestTaskFileService, rfiStore: RfiStore) => {
    const questions = rfiStore.getValue().rfiSubmitPayload?.questions;
    const files = rfiStore.getValue().rfiSubmitPayload?.files || [];
    const deadline = rfiStore.getValue().rfiSubmitPayload?.deadline;

    return fb.group({
      questions: fb.array(questions?.length > 0 ? questions.map(createAnotherQuestion) : [createAnotherQuestion()]),
      deadline: [deadline],
      files: requestTaskFileService.buildFormControl(
        rfiStore.getState().requestTaskId,
        files ?? [],
        rfiStore.getState().rfiAttachments,
        'RFI_UPLOAD_ATTACHMENT',
      ),
    });
  },
};

export function createAnotherQuestion(question?: string): UntypedFormGroup {
  return new UntypedFormGroup({
    question: new UntypedFormControl(question, [
      GovukValidators.required('Enter a question'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
