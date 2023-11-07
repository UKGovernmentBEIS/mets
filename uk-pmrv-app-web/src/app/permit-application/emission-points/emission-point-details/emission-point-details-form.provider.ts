import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { IdGeneratorService } from '../../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const emissionPointFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [ActivatedRoute, UntypedFormBuilder, PermitApplicationStore, IdGeneratorService],
  useFactory: (
    route: ActivatedRoute,
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    idGeneratorService: IdGeneratorService,
  ) => {
    const emissionPoint = store.permit.emissionPoints.find(
      (point) => point.id === route.snapshot.paramMap.get('emissionPointId'),
    );

    return fb.group({
      id: [emissionPoint?.id ?? idGeneratorService.generateId()],
      reference: [emissionPoint?.reference, GovukValidators.required('Enter a reference')],
      description: [emissionPoint?.description, GovukValidators.required('Enter a description')],
    });
  },
};
