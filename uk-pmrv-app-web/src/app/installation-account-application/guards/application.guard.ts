import { Inject, Injectable } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot } from '@angular/router';

import { combineLatest, iif, map, Observable, of, switchMap, tap } from 'rxjs';

import { TaskInfoGuard } from '@shared/guards/task-info.guard';
import { HoldingCompanyFormComponent } from '@shared/holding-company-form';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import {
  LEGAL_ENTITY_FORM_REG,
  legalEntityInitialValue,
} from '../factories/legal-entity/legal-entity-form-reg.factory';
import { updateState } from '../functions/update-state';
import {
  ApplicationSectionType,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export class ApplicationGuard {
  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly taskInfoGuard: TaskInfoGuard,
    private fb: UntypedFormBuilder,
    @Inject(INSTALLATION_FORM) private readonly installationForm: UntypedFormGroup,
    @Inject(LEGAL_ENTITY_FORM_REG) private readonly legalEntityForm: UntypedFormGroup,
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
      switchMap(() =>
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
      map(() => true),
    );
  }
}
