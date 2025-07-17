import {
  AbstractControl,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  ValidatorFn,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { INSPECTION_TASK_FORM, InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { endOfDay, format, isBefore, isEqual, subDays } from 'date-fns';

import { GovukValidators } from 'govuk-components';

import { InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const detailsFormProvider = {
  provide: INSPECTION_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const datePipe = new GovukDatePipe();
    const state = store.value;
    const disabled = !state.isEditable;
    const type = state.requestTaskItem.requestTask.type;

    const isOnsiteInspection = type === 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT';
    const payload = state.requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload;

    const details = payload?.installationInspection?.details;

    const date = (
      state.requestTaskItem.requestTask.payload as InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload
    )?.installationInspection?.details?.date;

    const yesterday = endOfDay(subDays(new Date(), 1));

    const files = details?.files;
    const regulatorExtraFiles = details?.regulatorExtraFiles;
    const officerNames = details?.officerNames;
    const extraOfficers = isOnsiteInspection
      ? officerNames?.length >= 1
        ? officerNames.slice(1)
        : []
      : officerNames?.length >= 2
        ? officerNames.slice(2)
        : [];

    const additionalInformation = details?.additionalInformation;

    return fb.group(
      {
        ...(isOnsiteInspection && {
          date: [
            {
              value: date ? new Date(date) : null,
              disabled,
            },
            {
              validators: GovukValidators.builder(
                `The date must be the same as or before ${datePipe.transform(format(yesterday, 'yyyy-MM-dd'))}`,
                (control: AbstractControl) =>
                  isBefore(new Date(control.value), yesterday) || isEqual(new Date(control.value), yesterday)
                    ? null
                    : { invalidDate: true },
              ),
            },
          ],
        }),
        officerName1: [
          { value: officerNames?.[0] ?? null, disabled },
          { validators: GovukValidators.required('Enter a name') },
        ],
        ...(!isOnsiteInspection && {
          officerName2: [
            { value: officerNames?.[1] ?? null, disabled },
            { validators: GovukValidators.required('Enter a name') },
          ],
        }),
        extraOfficers: fb.array(extraOfficers?.length > 0 ? extraOfficers.map(createAnotherOfficer) : []),
        files: requestTaskFileService.buildFormControl(
          store.requestTaskId,
          files ?? [],
          payload?.inspectionAttachments,
          type === 'INSTALLATION_AUDIT_APPLICATION_SUBMIT'
            ? 'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT'
            : 'INSTALLATION_ONSITE_INSPECTION_UPLOAD_ATTACHMENT',
          false,
          disabled,
        ),
        regulatorExtraFiles: requestTaskFileService.buildFormControl(
          store.requestTaskId,
          regulatorExtraFiles ?? [],
          payload?.inspectionAttachments,
          type === 'INSTALLATION_AUDIT_APPLICATION_SUBMIT'
            ? 'INSTALLATION_AUDIT_UPLOAD_ATTACHMENT'
            : 'INSTALLATION_ONSITE_INSPECTION_UPLOAD_ATTACHMENT',
          false,
          disabled,
        ),
        additionalInformation: [
          { value: additionalInformation, disabled },
          {
            validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
          },
        ],
      },
      { validators: officerNamesDifferentValidator },
    );
  },
};

export const officerNamesDifferentValidator: ValidatorFn = (control: AbstractControl) => {
  const officerName1 = control.get('officerName1');
  const officerName2 = control.get('officerName2');
  const extraOfficers = control.get('extraOfficers') as UntypedFormArray;

  const allNames = [
    officerName1?.value,
    officerName2?.value,
    ...extraOfficers.controls.map((c) => c.get('extraOfficer')?.value),
  ].filter((name) => name !== null && name !== '');

  const uniqueNames = new Set(allNames);

  if (allNames.length !== uniqueNames.size) {
    return { duplicateOfficerNames: 'All officer names must be unique' };
  }

  return null;
};

export function createAnotherOfficer(officer?: string): UntypedFormGroup {
  return new UntypedFormGroup({
    extraOfficer: new UntypedFormControl(officer, []),
  });
}
