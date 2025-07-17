import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { RequestTaskFileService } from '../../../../../shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const samplingJustificationProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
    requestTaskFileService: RequestTaskFileService,
  ): UntypedFormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const methodIndex = Number(route.snapshot.paramMap.get('methodIndex'));

    const state = store.getValue();
    const disabled = !state.isEditable;

    const subtaskData = getSubtaskData(state, index, route.snapshot.data.statusKey);
    const reducedSamplingFrequencyJustification =
      subtaskData?.analysisMethods?.[methodIndex]?.reducedSamplingFrequencyJustification;

    return fb.group({
      justification: [
        {
          value: [
            reducedSamplingFrequencyJustification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            reducedSamplingFrequencyJustification?.isOneThirdRuleAndSampling ? 'isOneThirdRuleAndSampling' : null,
          ].filter(Boolean),
          disabled,
        },
        GovukValidators.required('Select a reason'),
      ],
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        reducedSamplingFrequencyJustification?.files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        disabled,
      ),
    });
  },
};
