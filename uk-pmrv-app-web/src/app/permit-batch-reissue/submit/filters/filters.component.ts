import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { startWith, takeUntil, tap } from 'rxjs';

import { GovukValidators } from '../../../../../projects/govuk-components/src/public-api';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { originalOrder } from '../../../shared/keyvalue-order';
import { accountCategoryLabelMap, accountStatusLabelMap, accountTypeLabelMap } from '../filters-label.map';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';

@Component({
  selector: 'app-filters',
  templateUrl: './filters.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FiltersComponent implements OnInit {
  form: UntypedFormGroup = this.formBuilder.group({
    accountStatuses: [[], { validators: GovukValidators.required('Select a status'), updateOn: 'change' }],
    emitterTypes: [[], { validators: GovukValidators.required('Select a permit type'), updateOn: 'change' }],
    installationCategories: [[], { updateOn: 'change' }],
  });

  readonly originalOrder = originalOrder;
  readonly accountStatusLabelMap = accountStatusLabelMap;
  readonly accountTypeLabelMap = accountTypeLabelMap;
  readonly accountCategoryLabelMap = accountCategoryLabelMap;

  displayInstallationCategories = true;

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitBatchReissueStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        tap((state) => this.form.patchValue(state)),
        takeUntil(this.destroy$),
      )
      .subscribe();

    const emitterTypesControl = this.form.get('emitterTypes');
    const installationCategoriesControl = this.form.get('installationCategories');
    emitterTypesControl.valueChanges
      .pipe(startWith(emitterTypesControl.value), takeUntil(this.destroy$))
      .subscribe((value) => {
        const val = value as string[];
        if (val?.length === 1 && val[0] === 'HSE') {
          this.displayInstallationCategories = false;
          installationCategoriesControl.reset();
          installationCategoriesControl.clearValidators();
        } else {
          this.displayInstallationCategories = true;
          installationCategoriesControl.addValidators(GovukValidators.required('Select a category'));
        }
        installationCategoriesControl.updateValueAndValidity();
        this.form.updateValueAndValidity();
      });
  }

  onSubmit(): void {
    if (this.form.dirty) {
      this.store.patchState(this.form.value);
    }
    this.router.navigate(['..', 'signatory'], { relativeTo: this.route });
  }
}
