import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

import { RecommendedImprovementsFormProvider } from '../recommended-improvements-form.provider';
import {
  AER_VERIFY_TASK_FORM,
  RecommendedImprovementsItemFormProvider,
} from './recommended-improvements-item-form.provider';

interface ViewModel {
  isEditable: boolean;
  isCorsia: boolean;
}

@Component({
  selector: 'app-recommended-improvements-item',
  templateUrl: './recommended-improvements-item.component.html',
  providers: [DestroySubject, RecommendedImprovementsItemFormProvider],
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsItemComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectIsCorsia),
  ]).pipe(
    map(([isEditable, isCorsia]) => ({
      isEditable,
      isCorsia,
    })),
  );

  constructor(
    @Inject(AER_VERIFY_TASK_FORM) readonly form: UntypedFormGroup,
    @Inject(TASK_FORM_PROVIDER) readonly parentFormProvider: RecommendedImprovementsFormProvider,

    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      let improvementsValue: AviationAerRecommendedImprovements;
      combineLatest([
        this.store.pipe(first(), recommendedImprovementsQuery.selectRecommendedImprovements),
        this.route.paramMap,
      ])
        .pipe(
          first(),
          switchMap(([recommendedImprovements, paramMap]) => {
            const index = +paramMap.get('index');

            improvementsValue = {
              ...recommendedImprovements,
              recommendedImprovements:
                index >= (recommendedImprovements?.recommendedImprovements?.length ?? 0)
                  ? [
                      ...(recommendedImprovements?.recommendedImprovements ?? []),
                      { ...this.form.value, reference: `D${index + 1}` },
                    ]
                  : recommendedImprovements.recommendedImprovements.map((item, idx) =>
                      idx === index ? { ...item, ...this.form.value } : item,
                    ),
            };

            return this.store.aerVerifyDelegate.saveAerVerify(
              {
                recommendedImprovements: improvementsValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.parentFormProvider.setFormValue({ ...improvementsValue });
          this.nextUrl();
        });
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
