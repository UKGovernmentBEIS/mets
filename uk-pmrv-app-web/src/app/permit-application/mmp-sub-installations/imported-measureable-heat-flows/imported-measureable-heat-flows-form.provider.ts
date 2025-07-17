import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const importedMeasureableHeatFlowsAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, ActivatedRoute, PermitApplicationStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    route: ActivatedRoute,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
  ) => {
    const state = store.getValue();
    const productBenchmark = store.permit.monitoringMethodologyPlans?.digitizedPlan?.subInstallations
      ?.filter((subInstallation) => isProductBenchmark(subInstallation.subInstallationType))
      ?.find((product) => product.subInstallationNo === route.snapshot.paramMap.get('subInstallationNo'));

    const formGroup = productBenchmark?.importedMeasurableHeatFlow?.exist
      ? fb.group(
          {
            exist: [
              productBenchmark?.importedMeasurableHeatFlow?.exist ?? null,
              {
                validators: GovukValidators.required('Select yes if there are any measurable heat flows imported'),
                updateOn: 'change',
              },
            ],

            methodologyAppliedDescription: [
              productBenchmark?.importedMeasurableHeatFlow?.methodologyAppliedDescription ?? null,
              [
                GovukValidators.required('Enter a description of the applied methodology'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ],

            supportingFiles: requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              productBenchmark?.importedMeasurableHeatFlow?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          },
          {
            updateOn: 'change',
          },
        )
      : fb.group({
          exist: [
            productBenchmark?.importedMeasurableHeatFlow?.exist ?? null,
            {
              validators: GovukValidators.required('Select yes if there are any measurable heat flows imported'),
              updateOn: 'change',
            },
          ],
        });

    formGroup?.controls?.exist?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('methodologyAppliedDescription')) {
          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(productBenchmark?.importedMeasurableHeatFlow?.methodologyAppliedDescription ?? null, [
              GovukValidators.required('Enter a description of the applied methodology'),
              GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
            ]),
          );
        }

        if (!formGroup.contains('supportingFiles')) {
          formGroup.addControl(
            'supportingFiles',
            requestTaskFileService.buildFormControl(
              store.getState().requestTaskId,
              productBenchmark?.importedMeasurableHeatFlow?.supportingFiles ?? [],
              store.getState().permitAttachments,
              store.getFileUploadSectionAttachmentActionContext(),
              false,
              !state.isEditable,
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('supportingFiles');
      }
      formGroup.updateValueAndValidity();
    });

    return formGroup;
  },
};
