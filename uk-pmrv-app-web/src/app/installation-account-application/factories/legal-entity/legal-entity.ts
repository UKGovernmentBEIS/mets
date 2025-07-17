import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';

import { map, Observable } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

export function legalEntityNameNotExists(legalEntitiesService: LegalEntitiesService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> =>
    legalEntitiesService.isExistingLegalEntityName(control.value).pipe(
      map((res) =>
        res
          ? {
              legalEntityExists:
                'The legal entity name already exists. Return to previous page and select this legal entity from the legal entity list or enter a new legal entity on this page.',
            }
          : null,
      ),
    );
}
