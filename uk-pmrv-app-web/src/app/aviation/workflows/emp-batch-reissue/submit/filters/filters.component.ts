import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil, tap } from 'rxjs';

import {
  emissionTradingSchemeLabelMap,
  reportingStatusLabelMap,
} from '@aviation/workflows/emp-batch-reissue/submit/filters-label.map';
import { EmpBatchReissueStore } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { originalOrder } from '@shared/keyvalue-order';

import { GovukValidators } from 'govuk-components';

@Component({
  selector: 'app-filters',
  templateUrl: './filters.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FiltersComponent implements OnInit {
  form: UntypedFormGroup = this.formBuilder.group({
    reportingStatuses: [[], { validators: GovukValidators.required('Select a status'), updateOn: 'change' }],
    emissionTradingSchemes: [[], { validators: GovukValidators.required('Select a scheme'), updateOn: 'change' }],
  });

  readonly originalOrder = originalOrder;
  readonly reportingStatusLabelMap = reportingStatusLabelMap;
  readonly emissionTradingSchemeLabelMap = emissionTradingSchemeLabelMap;

  displayInstallationCategories = true;

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: EmpBatchReissueStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        tap((state) => this.form.patchValue(state)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  onSubmit(): void {
    if (this.form.dirty) {
      this.store.patchState(this.form.value);
    }
    this.router.navigate(['..', 'signatory'], { relativeTo: this.route });
  }
}
