import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { maxLength, noComment } from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationSubmitRequestTaskPayload, OperatorAirImprovementYesResponse } from 'pmrv-api';

export const improvementPositiveFormProvider = {
  provide: AIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload
    )?.operatorImprovementResponses?.[id] as OperatorAirImprovementYesResponse;
    const statePayload = state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload;
    const airAttachments = statePayload?.airAttachments;

    return fb.group({
      proposal: [
        { value: operatorImprovementResponse?.proposal, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
      proposedDate: [
        {
          value: operatorImprovementResponse?.proposedDate ? new Date(operatorImprovementResponse.proposedDate) : null,
          disabled,
        },
      ],
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        operatorImprovementResponse?.files ?? [],
        airAttachments,
        'AIR_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
