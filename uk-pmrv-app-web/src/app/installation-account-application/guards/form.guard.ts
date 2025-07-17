import { Inject, Injectable } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { combineLatest, map, Observable, take, tap } from 'rxjs';

import { HoldingCompanyFormComponent } from '@shared/holding-company-form';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { LEGAL_ENTITY_FORM_OP } from '../factories/legal-entity/legal-entity-form-op.factory';
import {
  LEGAL_ENTITY_FORM_REG,
  legalEntityInitialValue,
} from '../factories/legal-entity/legal-entity-form-reg.factory';
import {
  ApplicationSectionType,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export class FormGuard {
  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private fb: UntypedFormBuilder,
    @Inject(INSTALLATION_FORM) private readonly installationForm: UntypedFormGroup,
    @Inject(LEGAL_ENTITY_FORM_REG) private readonly legalEntityForm: UntypedFormGroup,
    @Inject(LEGAL_ENTITY_FORM_OP) private readonly legalEntityOpForm: UntypedFormGroup,
  ) {}

  canActivate(): Observable<boolean> {
    return combineLatest([
      this.store.getTask(ApplicationSectionType.installation),
      this.store.getTask(ApplicationSectionType.legalEntity),
      this.store.select('isReviewed'),
    ]).pipe(
      take(1),
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
          this.legalEntityOpForm.reset();
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
