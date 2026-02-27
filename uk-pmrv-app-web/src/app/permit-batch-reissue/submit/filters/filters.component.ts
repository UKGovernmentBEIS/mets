import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { startWith, takeUntil, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';

import { GovukValidators } from '../../../../../projects/govuk-components/src/public-api';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { originalOrder } from '../../../shared/keyvalue-order';
import {
  accountCategoryLabelMap,
  accountStatusLabelMap,
  accountTypeLabelMap,
  allocationStatusLabelMap,
} from '../filters-label.map';
import { PermitBatchReissueState } from '../store/permit-batch-reissue.state';
import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';

@Component({
  selector: 'app-filters',
  standalone: false,
  templateUrl: './filters.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FiltersComponent implements OnInit {
  form: UntypedFormGroup = this.formBuilder.group({
    accountStatuses: [[], { validators: GovukValidators.required('Select a status'), updateOn: 'change' }],
    emitterTypes: [[], { validators: GovukValidators.required('Select a permit type'), updateOn: 'change' }],
    installationCategories: [[], { updateOn: 'change' }],
    allocationStatuses: [[], { updateOn: 'change' }],
  });

  readonly originalOrder = originalOrder;
  readonly accountStatusLabelMap = accountStatusLabelMap;
  readonly initialAccountTypeLabelMap = accountTypeLabelMap;
  accountTypeLabelMap = accountTypeLabelMap;
  readonly accountCategoryLabelMap = accountCategoryLabelMap;
  private readonly wastePermitEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('wastePermitEnabled'));
  readonly allocationStatusLabelMap = allocationStatusLabelMap;

  displayInstallationCategories = true;
  displayAllocationStatuses = false;

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitBatchReissueStore,
    private readonly destroy$: DestroySubject,
    private readonly configStore: ConfigStore,
  ) {}

  stateToForm(state: PermitBatchReissueState) {
    const { freeAllocation, nonFreeAllocation, ...rest } = state;

    const allocationStatuses = [];
    if (freeAllocation) {
      allocationStatuses.push('FREE_ALLOCATION');
    }
    if (nonFreeAllocation) {
      allocationStatuses.push('NONFREE_ALLOCATION');
    }

    return { ...rest, allocationStatuses: [...allocationStatuses] };
  }

  formToState(formValue): PermitBatchReissueState {
    const allocationStatuses = this.form.value.allocationStatuses;
    delete formValue.allocationStatuses;

    const freeAllocation = allocationStatuses?.includes('FREE_ALLOCATION') ? true : false;
    const nonFreeAllocation = allocationStatuses?.includes('NONFREE_ALLOCATION') ? true : false;

    return { ...formValue, freeAllocation, nonFreeAllocation };
  }

  ngOnInit(): void {
    this.filterWasteBasedOnConfiguration();

    this.store
      .pipe(
        tap((state) => this.form.patchValue(this.stateToForm({ ...state }))),
        takeUntil(this.destroy$),
      )
      .subscribe();

    const emitterTypesControl = this.form.get('emitterTypes');
    const installationCategoriesControl = this.form.get('installationCategories');
    const allocationStatusesControl = this.form.get('allocationStatuses');

    emitterTypesControl.valueChanges
      .pipe(startWith(emitterTypesControl.value), takeUntil(this.destroy$))
      .subscribe((value) => {
        const val = value as string[];
        if (val?.includes('GHGE') || val?.includes('WASTE')) {
          this.displayInstallationCategories = true;
          installationCategoriesControl.addValidators(GovukValidators.required('Select a category'));
        } else {
          this.displayInstallationCategories = false;
          installationCategoriesControl.setValue(null, { emitEvent: false });
          installationCategoriesControl.clearValidators();
          installationCategoriesControl.markAsDirty();
        }

        if (val?.includes('GHGE') || val?.includes('HSE')) {
          this.displayAllocationStatuses = true;
        } else {
          this.displayAllocationStatuses = false;
          allocationStatusesControl.setValue(null, { emitEvent: false });
        }

        installationCategoriesControl.updateValueAndValidity();
        allocationStatusesControl.updateValueAndValidity();
        this.form.updateValueAndValidity();
      });
  }

  onSubmit(): void {
    if (this.form.dirty) {
      this.store.patchState(this.formToState(this.form.value));
    }
    this.router.navigate(['..', 'changes-summary'], { relativeTo: this.route });
  }

  private filterWasteBasedOnConfiguration(): void {
    this.wastePermitEnabled$.subscribe((wastePermitEnabled: boolean) => {
      const labelMapCopy = { ...this.initialAccountTypeLabelMap };
      if (!wastePermitEnabled) {
        delete labelMapCopy['WASTE'];
      }
      this.accountTypeLabelMap = labelMapCopy;
    });
  }
}
