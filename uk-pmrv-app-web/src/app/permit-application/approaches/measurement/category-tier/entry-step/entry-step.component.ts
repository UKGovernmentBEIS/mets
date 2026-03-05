import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { ApproachTaskPipe } from '@permit-application/approaches/approach-task.pipe';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { MEASUREMENTCategoryTierSubtaskStatus } from '../../measurement-status';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { entryStepProvider } from './entry-step.provider';

@Component({
  selector: 'app-entry-step',
  templateUrl: './entry-step.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [entryStepProvider],
})
export class EntryStepComponent implements PendingRequest {
  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  readonly cannotStart$ = combineLatest([this.route.data, this.index$, this.store]).pipe(
    map(
      ([data, index, state]) =>
        MEASUREMENTCategoryTierSubtaskStatus(state, data.statusKey, index) === 'cannot start yet',
    ),
  );

  readonly emissionPoint$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe.transform('MEASUREMENT_CO2').pipe(
        map((response) => {
          return response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory.emissionPoint;
        }),
      ),
    ),
  );

  readonly emissionPointCategory$ = this.index$.pipe(
    switchMap((index) =>
      this.approachTaskPipe
        .transform('MEASUREMENT_CO2')
        .pipe(map((response) => response?.emissionPointCategoryAppliedTiers?.[index]?.emissionPointCategory)),
    ),
  );

  private readonly taskKey = this.route.snapshot.data.taskKey;
  readonly statusKey = this.route.snapshot.data.statusKey;

  private readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private approachTaskPipe: ApproachTaskPipe,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.index$, this.categoryAppliedTiers$, this.store])
        .pipe(
          first(),
          switchMap(([index, categoryAppliedTiers, state]) =>
            this.store.postTask(
              this.taskKey,
              this.buildData(categoryAppliedTiers, index),
              state.permitSectionsCompleted[this.statusKey]?.map((item, idx) => (index === idx ? false : item)),
              this.statusKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(categoryAppliedTiers: categoryAppliedTier[], index: number): categoryAppliedTier[] {
    return categoryAppliedTiers.map((item, idx) =>
      idx === index
        ? {
            ...item,
            [this.subtaskName]: {
              ...(this.form.value.exist ? item[this.subtaskName] : null),
              exist: this.form.value.exist,
            },
          }
        : item,
    );
  }

  private navigateNext(): void {
    const nextStep = this.form.value.exist ? 'tier' : 'answers';

    this.router.navigate([`./${nextStep}`], { relativeTo: this.route });
  }
}
