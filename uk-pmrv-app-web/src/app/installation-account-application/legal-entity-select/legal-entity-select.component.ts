import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { iif, map, Observable, of, switchMap, tap } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { GovukSelectOption } from 'govuk-components';

import { LegalEntitiesService } from 'pmrv-api';

import { LEGAL_ENTITY_FORM } from '../factories/legal-entity-form.factory';
import { ResolvedData } from '../resolved-data';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-select',
  templateUrl: './legal-entity-select.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalEntitySelectComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  form: UntypedFormGroup;
  legalEntities$: Observable<GovukSelectOption<number>[]> = this.route.data.pipe(
    map(({ legalEntities }: ResolvedData) => legalEntities.map(({ id, name }) => ({ text: name, value: id }))),
  );

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    @Inject(LEGAL_ENTITY_FORM) private readonly legalEntityForm: UntypedFormGroup,
    private readonly legalEntitiesService: LegalEntitiesService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {
    this.form = this.legalEntityForm.get('selectGroup') as UntypedFormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  onSubmit(): void {
    if (this.form.get('isNew').value) {
      this.router.navigate(['../details'], { relativeTo: this.route });
    } else {
      this.legalEntitiesService
        .getLegalEntityById(this.form.get('id').value)
        .pipe(
          tap((legalEntity) =>
            this.store.updateTask(
              ApplicationSectionType.legalEntity,
              {
                selectGroup: this.form.value,
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
          switchMap(() => iif(() => this.store.getState().isReviewed, this.store.amend(), of(null))),
        )
        .subscribe(() => this.store.nextStep('../..', this.route));
    }
  }
}
