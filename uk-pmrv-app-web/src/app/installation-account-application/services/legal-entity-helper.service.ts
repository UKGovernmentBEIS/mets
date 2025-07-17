import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { GovukSelectOption } from 'govuk-components';

import { LegalEntitiesService } from 'pmrv-api';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable({
  providedIn: 'root',
})
export class LegalEntityHelperService {
  constructor(
    private readonly legalEntitiesService: LegalEntitiesService,
    private readonly store: InstallationAccountApplicationStore,
  ) {}

  getLegalEntities(route: ActivatedRoute): Observable<GovukSelectOption<number>[]> {
    return route.data.pipe(
      map(({ legalEntities }: { legalEntities: any[] }) =>
        legalEntities.map(({ id, name }) => ({ text: name, value: id })),
      ),
    );
  }

  updateLegalEntityDetails(id: number, selectGroupFormValue: any) {
    return this.legalEntitiesService.getLegalEntityById(id).pipe(
      tap((legalEntity) =>
        this.store.updateTask(
          ApplicationSectionType.legalEntity,
          {
            selectGroup: selectGroupFormValue,
            detailsGroup: {
              address: legalEntity.address,
              name: legalEntity.name,
              type: legalEntity.type,
              referenceNumber: legalEntity.referenceNumber,
              noReferenceNumberReason: legalEntity.noReferenceNumberReason,
              belongsToHoldingCompany: !!legalEntity.holdingCompany,
            },
          },
          'complete',
        ),
      ),
    );
  }
}
