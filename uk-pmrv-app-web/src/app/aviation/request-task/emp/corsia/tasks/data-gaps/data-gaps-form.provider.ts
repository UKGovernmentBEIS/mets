import { Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import { DataGapsFormModel } from './data-gaps-form.model';

export const dataGapsFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    return createForm(fb);
  },
};
function createForm(fb: FormBuilder): DataGapsFormModel {
  const form = fb.group(
    {
      dataGaps: fb.control('', [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        GovukValidators.required('Say how you identify data gaps and assess if the threshold has been reached'),
      ]),
      secondaryDataSources: fb.control('', [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        GovukValidators.required(
          'List the secondary data sources used to estimate fuel consumption if your primary data is missing or incorrect',
        ),
      ]),
      secondarySourcesDataGapsExist: fb.control<boolean>(null, [
        GovukValidators.required('Select yes if the data management system allows for data gaps'),
      ]),
      secondarySourcesDataGapsConditions: fb.control(null, [
        GovukValidators.required('Enter a comment'),
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ]),
    },
    {
      updateOn: 'change',
    },
  ) as DataGapsFormModel;
  return form;
}
