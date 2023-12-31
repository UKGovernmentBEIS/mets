import { AsyncValidatorFn, UntypedFormBuilder, UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { first, map, Observable, of } from 'rxjs';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData } from '../category-tier';

export const analysisMethodListProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore, ActivatedRoute, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: PermitApplicationStore<PermitApplicationState>,
    route: ActivatedRoute,
  ): UntypedFormGroup => {
    const index = Number(route.snapshot.paramMap.get('index'));
    const state = store.getValue();
    const subtaskData = getSubtaskData(state, index, route.snapshot.data.statusKey);

    return fb.group(
      {},
      {
        asyncValidators: [
          atLeastOneAnalysisMethodExists(subtaskData),
          atLeastOneSamplingJustificationFormNotFilled(subtaskData),
        ],
      },
    );
  },
};

function atLeastOneAnalysisMethodExists(subtaskData): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    of(subtaskData).pipe(
      first(),
      map((subtaskData) => {
        return subtaskData?.analysisMethods?.length
          ? null
          : { analysisMethodNotExist: 'Enter details of the analysis method used' };
      }),
    );
}

function atLeastOneSamplingJustificationFormNotFilled(subtaskData): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    of(subtaskData).pipe(
      first(),
      map((subtaskData) => {
        return !subtaskData?.analysisMethods ||
          subtaskData?.analysisMethods
            ?.filter((method) => !method.frequencyMeetsMinRequirements)
            .every((method) => !!method?.reducedSamplingFrequencyJustification)
          ? null
          : { samplingJustificationNotExist: 'Select a reason' };
      }),
    );
}
