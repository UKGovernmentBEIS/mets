import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { IdGeneratorService } from '../../../shared/services/id-generator.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

export const emisionSourcesAddFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute, IdGeneratorService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
    idGeneratorService: IdGeneratorService,
  ) => {
    const source = store.permit.emissionSources.find(
      (emissionSource) => emissionSource.id === route.snapshot.paramMap.get('sourceId'),
    );

    return fb.group({
      id: [source?.id ?? idGeneratorService.generateId()],
      reference: [source?.reference ?? null, GovukValidators.required('Enter a reference')],
      description: [source?.description ?? null, GovukValidators.required('Enter a description')],
    });
  },
};
