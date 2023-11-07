import { Inject, Injectable } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { combineLatest, iif, mapTo, Observable, of, switchMapTo, tap } from 'rxjs';

import { HoldingCompanyFormComponent } from '@shared/holding-company-form';

import { TaskInfoGuard } from '../../shared/guards/task-info.guard';
import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { LEGAL_ENTITY_FORM, legalEntityInitialValue } from '../factories/legal-entity-form.factory';
import { updateState } from '../functions/update-state';
import {
  ApplicationSectionType,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export class ApplicationGuard implements CanActivate {
  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly taskInfoGuard: TaskInfoGuard,
    private fb: UntypedFormBuilder,
    @Inject(INSTALLATION_FORM) private readonly installationForm: UntypedFormGroup,
    @Inject(LEGAL_ENTITY_FORM) private readonly legalEntityForm: UntypedFormGroup,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return iif(
      () => !!this.taskInfoGuard.resolve(),
      of(this.taskInfoGuard.resolve()),
      this.taskInfoGuard.canActivate(route),
    ).pipe(
      tap(() =>
        updateState(this.store, this.taskInfoGuard.resolve().requestTask, Number(route.paramMap.get('taskId'))),
      ),
      switchMapTo(
        combineLatest([
          this.store.getTask(ApplicationSectionType.installation),
          this.store.getTask(ApplicationSectionType.legalEntity),
          this.store.select('isReviewed'),
        ]),
      ),
      tap(
        ([installationSection, legalEntitySection, isReviewed]: [InstallationSection, LegalEntitySection, boolean]) => {
          this.installationForm.reset(installationSection.value);
          if (legalEntitySection.value?.detailsGroup?.belongsToHoldingCompany) {
            (this.legalEntityForm.get('detailsGroup') as UntypedFormGroup).addControl(
              'holdingCompanyGroup',
              this.fb.group(HoldingCompanyFormComponent.controlsFactory()),
            );
          }
          this.legalEntityForm.reset({ ...legalEntityInitialValue, ...legalEntitySection.value });
          if (isReviewed) {
            this.installationForm.get('installationTypeGroup').disable();
            this.installationForm.get('locationGroup').disable();
          }
        },
      ),
      mapTo(true),
    );
  }
}
