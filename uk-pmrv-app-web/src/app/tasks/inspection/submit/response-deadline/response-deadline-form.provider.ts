import { AbstractControl, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { INSPECTION_TASK_FORM } from '@tasks/inspection/shared/inspection';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { addDays, format, isAfter, startOfDay } from 'date-fns';

import { GovukValidators } from 'govuk-components';

import { InstallationAuditApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const responseDeadlineFormProvider = {
  provide: INSPECTION_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const datePipe = new GovukDatePipe();
    const state = store.value;
    const disabled = !state.isEditable;
    const responseDeadline = (
      state.requestTaskItem.requestTask.payload as InstallationAuditApplicationSubmitRequestTaskPayload
    )?.installationInspection.responseDeadline;
    const tomorrow = startOfDay(addDays(new Date(), 1));

    return fb.group({
      responseDeadline: [
        {
          value: responseDeadline ? new Date(responseDeadline) : null,
          disabled,
        },
        {
          validators: GovukValidators.builder(
            `The date must be the same as or after ${datePipe.transform(format(tomorrow, 'yyyy-MM-dd'))}`,
            (control: AbstractControl) => (isAfter(new Date(), new Date(control.value)) ? { invalidDate: true } : null),
          ),
        },
      ],
    });
  },
};
