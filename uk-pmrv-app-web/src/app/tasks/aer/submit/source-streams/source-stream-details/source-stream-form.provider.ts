import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { IdGeneratorService } from '@shared/services/id-generator.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const sourceStreamFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const state = store.getValue();
    const sourceStream = (
      state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
    ).aer?.sourceStreams?.find((stream) => stream.id === route.snapshot.paramMap.get('streamId'));

    return fb.group({
      id: [sourceStream?.id ?? idGeneratorService.generateId()],
      reference: [sourceStream?.reference ?? null, GovukValidators.required('Enter a reference')],
      description: [sourceStream?.description ?? null, GovukValidators.required('Select a description')],
      otherDescriptionName: [
        { value: sourceStream?.otherDescriptionName ?? null, disabled: sourceStream?.description !== 'OTHER' },
        [GovukValidators.required('Enter a description'), GovukValidators.maxLength(300, 'Enter up to 300 characters')],
      ],
      type: [sourceStream?.type ?? null, GovukValidators.required('Select a type')],
      otherTypeName: [
        { value: sourceStream?.otherTypeName ?? null, disabled: sourceStream?.type !== 'OTHER' },
        [GovukValidators.required('Enter a description'), GovukValidators.maxLength(300, 'Enter up to 300 characters')],
      ],
    });
  },
};
