import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { atLeastOneRequiredValidator } from '@shared/utils/validators';

import { GovukValidators } from 'govuk-components';

import { DirectlyAttributableEmissionsPB } from 'pmrv-api';

import { isProductBenchmark } from '../mmp-sub-installations-status';

export const directlyAttributableEmissionsAddFormFactory = {
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

    const formGroup = fb.group(
      {
        attribution: [
          (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)?.attribution ?? null,
          [GovukValidators.maxLength(15000, 'Enter up to 15000 characters')],
        ],

        furtherInternalSourceStreamsRelevant: [
          (
            productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB as DirectlyAttributableEmissionsPB
          )?.furtherInternalSourceStreamsRelevant ?? null,
          {
            validators: GovukValidators.required('Select yes if further internal source streams are relevant'),
            updateOn: 'change',
          },
        ],

        methodologyAppliedDescription: [
          (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)
            ?.methodologyAppliedDescription ?? null,
          [
            GovukValidators.required('Enter a description of the applied methodology'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],

        transferredCO2ImportedOrExportedRelevant: [
          (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)
            ?.transferredCO2ImportedOrExportedRelevant ?? null,
          {
            validators: GovukValidators.required('Select yes if the transferred CO2 imported or exported is relevant'),
            updateOn: 'change',
          },
        ],

        amountsMonitoringDescription: [
          (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)
            ?.amountsMonitoringDescription ?? null,
          [
            GovukValidators.required('Enter a description of how the amounts are covered'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        ],

        supportingFiles: requestTaskFileService.buildFormControl(
          store.getState().requestTaskId,
          (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)?.supportingFiles ?? [],
          store.getState().permitAttachments,
          store.getFileUploadSectionAttachmentActionContext(),
          false,
          !state.isEditable,
        ),
      },
      {
        updateOn: 'change',
      },
    );

    formGroup?.controls?.furtherInternalSourceStreamsRelevant?.valueChanges?.subscribe((val) => {
      if (val) {
        if (!formGroup.contains('dataSources')) {
          const dataSources = (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)
            ?.dataSources;

          formGroup.addControl(
            'dataSources',
            fb.array(
              (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)?.dataSources?.length
                ? (
                    productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB
                  )?.dataSources?.map((dataSource, index) => {
                    return fb.group(
                      {
                        amounts: [
                          dataSource?.amounts ?? '',
                          index === 0 ? [GovukValidators.required('Select amounts imported or exported')] : [],
                        ],
                        energyContent: [dataSource?.energyContent ?? ''],
                        emissionFactorOrCarbonContent: [dataSource?.emissionFactorOrCarbonContent ?? ''],
                        biomassContent: [dataSource?.biomassContent ?? ''],
                      },
                      {
                        validators:
                          index > 0
                            ? [
                                atLeastOneRequiredValidator(
                                  'Select at least one option in the data source group or remove the group',
                                ),
                              ]
                            : [],
                      },
                    );
                  })
                : [
                    fb.group({
                      amounts: [
                        dataSources?.[0]?.amounts ?? '',
                        [GovukValidators.required('Select amounts imported or exported')],
                      ],
                      energyContent: [dataSources?.[0]?.energyContent ?? ''],
                      emissionFactorOrCarbonContent: [dataSources?.[0]?.emissionFactorOrCarbonContent ?? ''],
                      biomassContent: [dataSources?.[0]?.biomassContent ?? ''],
                    }),
                  ],
              [GovukValidators.required('Select the data sources used')],
            ),
          );

          formGroup.addControl(
            'methodologyAppliedDescription',
            fb.control(
              (productBenchmark?.directlyAttributableEmissions as DirectlyAttributableEmissionsPB)
                ?.methodologyAppliedDescription ?? null,
              [
                GovukValidators.required('Enter a description of the applied methodology'),
                GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
              ],
            ),
          );
        }
      } else {
        formGroup.removeControl('methodologyAppliedDescription');
        formGroup.removeControl('dataSources');
      }
      formGroup.updateValueAndValidity();
    });

    formGroup.controls.furtherInternalSourceStreamsRelevant.updateValueAndValidity({ emitEvent: true });

    return formGroup;
  },
};
