import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { ApproachTaskPipe } from '@permit-application/approaches/approach-task.pipe';

import {
  FallbackMonitoringApproach,
  FallbackSourceStreamCategory,
  FallbackSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryFormProvider } from './category-form.provider';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [categoryFormProvider],
})
export class CategoryComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  sourceStream$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('FALLBACK').pipe(
        map((response) => {
          return response?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory?.sourceStream;
        }),
      ),
    ),
  );

  sourceStreamCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('FALLBACK').pipe(
        map((response) => {
          return response?.sourceStreamCategoryAppliedTiers?.[index]?.sourceStreamCategory;
        }),
      ),
    ),
  );
  categoryTypeOptions: FallbackSourceStreamCategory['categoryType'][] = ['MAJOR', 'MINOR', 'DE_MINIMIS', 'MARGINAL'];
  uncertaintyOptions: FallbackSourceStreamCategory['uncertainty'][] = [
    'LESS_OR_EQUAL_1_5',
    'LESS_OR_EQUAL_2_5',
    'LESS_OR_EQUAL_5_0',
    'LESS_OR_EQUAL_7_5',
    'LESS_OR_EQUAL_10_0',
    'LESS_OR_EQUAL_12_5',
    'LESS_OR_EQUAL_15_0',
    'LESS_OR_EQUAL_17_5',
    'N_A',
  ];
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private approachTaskPipe: ApproachTaskPipe,
  ) {}

  onSubmit(): void {
    if (this.form.value.uncertainty === '') {
      this.form.value.uncertainty = null;
    }
    combineLatest([
      this.index$,
      this.store.findTask<FallbackSourceStreamCategoryAppliedTier[]>(
        'monitoringApproaches.FALLBACK.sourceStreamCategoryAppliedTiers',
      ),
      this.store,
      this.route.data,
    ])
      .pipe(
        first(),
        switchMap(([index, tiers, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                FALLBACK: {
                  ...state.permit.monitoringApproaches.FALLBACK,
                  sourceStreamCategoryAppliedTiers:
                    tiers && tiers[index]
                      ? tiers.map((tier, idx) =>
                          index === idx
                            ? {
                                ...tier,
                                sourceStreamCategory: {
                                  ...this.form.value,
                                  annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                                },
                              }
                            : tier,
                        )
                      : [
                          ...(tiers ?? []),
                          {
                            sourceStreamCategory: {
                              ...this.form.value,
                              annualEmittedCO2Tonnes: this.form.controls.annualEmittedCO2Tonnes.value.toString(),
                            },
                          },
                        ],
                } as FallbackMonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              FALLBACK_Category:
                tiers && tiers[index]
                  ? state.permitSectionsCompleted.FALLBACK_Category.map((item, idx) => (index === idx ? true : item))
                  : [...(state.permitSectionsCompleted.FALLBACK_Category ?? []), true],
            },
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
