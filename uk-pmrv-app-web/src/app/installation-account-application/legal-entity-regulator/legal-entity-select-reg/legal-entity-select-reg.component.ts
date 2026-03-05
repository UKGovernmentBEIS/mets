import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { iif, map, Observable, of, switchMap } from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import { LEGAL_ENTITY_FORM_REG } from '../../factories/legal-entity/legal-entity-form-reg.factory';
import { LegalEntityHelperService } from '../../services/legal-entity-helper.service';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-select-reg',
  templateUrl: './legal-entity-select-reg.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalEntitySelectRegComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  form: UntypedFormGroup;
  legalEntities$: Observable<GovukSelectOption<number>[]>;

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    @Inject(LEGAL_ENTITY_FORM_REG) private readonly legalEntityForm: UntypedFormGroup,
    private readonly legalEntityHelperService: LegalEntityHelperService,
  ) {
    this.form = this.legalEntityForm.get('selectGroup') as UntypedFormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();
    this.legalEntities$ = this.legalEntityHelperService.getLegalEntities(this.route);
  }

  onSubmit(): void {
    if (this.form.get('isNew').value) {
      this.router.navigate(['../details'], { relativeTo: this.route });
    } else {
      this.legalEntityHelperService
        .updateLegalEntityDetails(this.form.get('id').value, this.form.value)
        .pipe(switchMap(() => iif(() => this.store.getState().isReviewed, this.store.amend(), of(null))))
        .subscribe(() => this.store.nextStep('../..', this.route));
    }
  }
}
