import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { MeasurementOfCO2MonitoringApproach, MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { RequestTaskFileService } from '../../../../shared/services/request-task-file-service/request-task-file.service';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const JUSTIFICATION_FORM = new InjectionToken<UntypedFormGroup>('Justification form');

export const justificationFormProvider = {
  provide: JUSTIFICATION_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const { taskKey } = route.snapshot.data;

    const state = store.getValue();

    const justification =
      (
        state.permit.monitoringApproaches[taskKey] as
          | MeasurementOfN2OMonitoringApproach
          | MeasurementOfCO2MonitoringApproach
      ).emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions?.noHighestRequiredTierJustification ?? null;

    return fb.group({
      justification: [
        {
          value: [
            justification?.isCostUnreasonable ? 'isCostUnreasonable' : null,
            justification?.isTechnicallyInfeasible ? 'isTechnicallyInfeasible' : null,
          ].filter(Boolean),
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select a justification'),
      ],
      technicalInfeasibilityExplanation: [
        {
          value: justification?.technicalInfeasibilityExplanation ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required(
            'Explain why it is not technically feasible to meet the highest tier, as set out in Article 17 of the Monitoring and Reporting Regulations (MRR)',
          ),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
      files: requestTaskFileService.buildFormControl(
        store.getState().requestTaskId,
        justification?.files ?? [],
        store.getState().permitAttachments,
        store.getFileUploadSectionAttachmentActionContext(),
        false,
        !state.isEditable,
      ),
    });
  },
};
