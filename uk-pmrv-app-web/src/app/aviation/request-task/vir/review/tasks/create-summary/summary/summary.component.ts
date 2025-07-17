import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { RegulatorReviewResponse } from 'pmrv-api';

interface ViewModel {
  heading: string;
  regulatorReviewResponse: RegulatorReviewResponse;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-regulator-create-summary
        [regulatorReviewResponse]="vm.regulatorReviewResponse"
        [isEditable]="vm.isEditable"
        [queryParams]="{ change: true }"></app-regulator-create-summary>
      <div class="govuk-button-group">
        <button (click)="onSubmit()" appPendingButton govukButton type="button" *ngIf="!vm.hideSubmit">
          Confirm and complete
        </button>
      </div>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [VirSharedModule, SharedModule, ReturnToLinkComponent],
})
export class SummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectRegulatorReviewResponse),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(virQuery.selectReviewSectionsCompleted),
  ]).pipe(
    map(([regulatorReviewResponse, isEditable, sectionsCompleted]) => {
      return {
        heading: 'Check your answers',
        regulatorReviewResponse: {
          ...regulatorReviewResponse,
          reportSummary: this.formProvider.getFormValue(),
        },
        isEditable: isEditable,
        hideSubmit: !isEditable || sectionsCompleted['createSummary'],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: CreateSummaryFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveReviewVir('createSummary', 'complete', null, this.formProvider.getFormValue())
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../../../..'], {
          relativeTo: this.route,
        });
      });
  }
}
