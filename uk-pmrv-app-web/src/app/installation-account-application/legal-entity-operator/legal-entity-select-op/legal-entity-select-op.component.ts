import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { LegalEntityHelperService } from '../../services/legal-entity-helper.service';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-select-op',
  templateUrl: './legal-entity-select-op.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalEntitySelectOpComponent implements OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  form: UntypedFormGroup;
  legalEntities$: Observable<GovukSelectOption<number>[]>;

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    @Inject(LEGAL_ENTITY_FORM_OP) private readonly legalEntityOpForm: UntypedFormGroup,
    private readonly legalEntityHelperService: LegalEntityHelperService,
  ) {
    this.form = this.legalEntityOpForm.get('selectGroup') as UntypedFormGroup;
    this.legalEntities$ = this.legalEntityHelperService.getLegalEntities(this.route);
  }

  ngOnInit(): void {
    this.form.markAsPristine();
    this.form.markAsUntouched();

    if (this.form.get('isNew').value === null) {
      this.form.patchValue({
        isNew: false,
      });
    }
  }

  onSubmit(): void {
    if (this.form.get('isNew').value) {
      this.router.navigate(['../regno'], { relativeTo: this.route });
    } else {
      this.legalEntityHelperService
        .updateLegalEntityDetails(this.form.get('id').value, this.form.value)
        .subscribe(() => this.store.nextStep('../..', this.route));
    }
  }
}
