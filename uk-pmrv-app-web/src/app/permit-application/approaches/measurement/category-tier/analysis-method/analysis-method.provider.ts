import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const analysisMethodProvider = {
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

    return fb.group({
      analysis: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.analysis ?? null, disabled },
        GovukValidators.required('Enter a method of analysis'),
      ],
      subParameter: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.subParameter ?? null, disabled },
        GovukValidators.maxLength(250, 'Enter up to 250 characters'),
      ],
      samplingFrequency: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.samplingFrequency ?? null, disabled },
        GovukValidators.required('Select a sampling frequency'),
      ],
      samplingFrequencyOtherDetails: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.samplingFrequencyOtherDetails ?? null, disabled },
        [GovukValidators.required('Enter a short name'), GovukValidators.maxLength(500, 'Enter up to 500 characters')],
      ],
      frequencyMeetsMinRequirements: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.frequencyMeetsMinRequirements ?? null, disabled },
        GovukValidators.required('Select yes or no'),
      ],
      laboratoryName: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.laboratoryName ?? null, disabled },
        [
          GovukValidators.required('Enter laboratory name'),
          GovukValidators.maxLength(250, 'Enter up to 250 characters'),
        ],
      ],
      laboratoryAccredited: [
        { value: subtaskData?.analysisMethods?.[methodIndex]?.laboratoryAccredited ?? null, disabled },
        GovukValidators.required('Select yes or no'),
      ],
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        subtaskData?.analysisMethods?.[methodIndex]?.files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        disabled,
      ),
    });
  },
};
