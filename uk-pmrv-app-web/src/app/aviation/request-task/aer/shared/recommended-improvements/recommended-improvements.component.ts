import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

import { RecommendedImprovementsFormProvider } from './recommended-improvements-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  recommendedImprovements: AviationAerRecommendedImprovements;
}

@Component({
  selector: 'app-recommended-improvements',
  templateUrl: './recommended-improvements.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent {
  protected recommendedImprovements: AviationAerRecommendedImprovements;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(recommendedImprovementsQuery.selectRecommendedImprovements),
  ]).pipe(
    map(([isEditable, recommendedImprovements]) => ({
      pageHeader: 'Are there any recommended improvements?',
      isEditable,
      recommendedImprovements,
    })),
    tap((data) => {
      this.recommendedImprovements = data.recommendedImprovements;
    }),
  );
  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: RecommendedImprovementsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      this.store
        .pipe(first(), recommendedImprovementsQuery.selectRecommendedImprovements)
        .pipe(
          switchMap((recommendedImprovements) => {
            const value = this.form.get('exist').value
              ? {
                  ...recommendedImprovements,
                  ...this.form.value,
                }
              : ({
                  ...this.form.value,
                  recommendedImprovements: [],
                } as AviationAerRecommendedImprovements);

            this.formProvider.setFormValue(value);
            return this.store.aerVerifyDelegate.saveAerVerify({ recommendedImprovements: value }, 'in progress');
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    switch (this.form.get('exist').value) {
      case false:
        return this.router.navigate(['summary'], { relativeTo: this.route, queryParams: { change: true } });
      case true:
        if (this.recommendedImprovements?.recommendedImprovements?.length > 0) {
          return this.router.navigate(['list'], { relativeTo: this.route, queryParams: { change: true } });
        } else {
          return this.router.navigate(['list/0'], { relativeTo: this.route, queryParams: { change: true } });
        }
    }
  }
}
