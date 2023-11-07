import { Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { DataGapsFormModel } from './data-gaps-form.model';

export const dataGapsFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder) => {
    return createForm(fb);
  },
};
function createForm(fb: FormBuilder): DataGapsFormModel {
  return fb.group(
    {
      dataGaps: fb.control('', [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        GovukValidators.required('Explain how you identify data gaps and assess if the threshold has been reached'),
      ]),
      secondaryDataSources: fb.control('', [
        GovukValidators.maxLength(2000, 'Enter up to 2000 characters'),
        GovukValidators.required(
          'List the secondary data sources used to estimate fuel consumption if your primary data is missing or incorrect',
        ),
      ]),
      substituteData: fb.control('', [
        GovukValidators.maxLength(2000, 'Enter up to 2000 characters'),
        GovukValidators.required(
          'Describe the surrogate method used to estimate fuel consumption where data is missing.',
        ),
      ]),
      otherDataGapsTypes: fb.control('', [GovukValidators.maxLength(2000, 'Enter up to 2000 characters')]),
    },
    { updateOn: 'change' },
  );
}
