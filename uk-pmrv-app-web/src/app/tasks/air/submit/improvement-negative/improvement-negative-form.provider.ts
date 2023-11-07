import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import {
  maxLength,
  noJustification,
  noTechnicalInfeasibilityExplanation,
} from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationSubmitRequestTaskPayload, OperatorAirImprovementNoResponse } from 'pmrv-api';

export const improvementNegativeFormProvider = {
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
    )?.operatorImprovementResponses?.[id] as OperatorAirImprovementNoResponse;
    const statePayload = state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload;
    const airAttachments = statePayload?.airAttachments;

    return fb.group({
      justification: [
        {
          value: [
            operatorImprovementResponse?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            operatorImprovementResponse?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled: !state.isEditable,
        },
        GovukValidators.required(noJustification),
      ],
      technicalInfeasibilityExplanation: [
        {
          value: operatorImprovementResponse?.technicalInfeasibilityExplanation ?? null,
          disabled: !state.isEditable,
        },
        [GovukValidators.required(noTechnicalInfeasibilityExplanation), GovukValidators.maxLength(10000, maxLength)],
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
