import { ChangeDetectionStrategy, Component, computed, Inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { INSPECTION_TASK_FORM, InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { FollowUpActionsComponent } from '@tasks/inspection/submit/follow-up-actions/follow-up-actions.component';

import { responseDeadlineFormProvider } from '../response-deadline/response-deadline-form.provider';
import { isInstallationInspectionFollowUpSubmitCompleted, responseDeadlineValid } from '../submit.wizard';

@Component({
  selector: 'app-follow-up-summary',
  standalone: true,
  imports: [FollowUpActionsComponent, SharedModule, RouterLink],
  providers: [responseDeadlineFormProvider],
  template: `
    <govuk-error-summary *ngIf="errors()" [form]="form"></govuk-error-summary>

    <app-follow-up-actions>
      <ng-container *ngIf="inspectionPayload().installationInspection?.followUpActionsRequired !== false">
        <h2 class="govuk-heading-m">Deadline for follow-up actions</h2>

        <dl govuk-summary-list [hasBorders]="true">
          <div govukSummaryListRow>
            <dt govukSummaryListRowKey [class.missing-row-key]="errors()?.invalidDate">Response deadline</dt>
            <dd govukSummaryListRowValue>
              {{ inspectionPayload().installationInspection.responseDeadline | date: 'd MMMM yyyy' }}
              <div *ngIf="errors()?.invalidDate" class="error">{{ errors()?.invalidDate }}</div>
            </dd>
            <dd govukSummaryListRowActions *ngIf="isEditable()">
              <a govukLink [routerLink]="['../', 'response-deadline']">Change</a>
            </dd>
          </div>
        </dl>
      </ng-container>
    </app-follow-up-actions>
  `,
  styleUrl: '../../shared/css/form-errors.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpSummaryComponent {
  isEditable = toSignal(this.inspectionService.isEditable$);
  inspectionPayload: Signal<InspectionSubmitRequestTaskPayload> = toSignal(this.inspectionService.payload$);

  errors: Signal<ValidationErrors> = computed(() => {
    const installationInspection = this.inspectionPayload().installationInspection;
    const deadlineValid =
      installationInspection?.followUpActionsRequired === true
        ? responseDeadlineValid(installationInspection.responseDeadline)
        : true;
    const completedAndValid = isInstallationInspectionFollowUpSubmitCompleted(installationInspection) && deadlineValid;

    return completedAndValid
      ? null
      : Object.values(this.form.controls)
          .filter((control) => control?.errors)
          .reduce((errors, control) => {
            return {
              ...errors,
              ...control.errors,
            };
          }, {});
  });

  constructor(
    @Inject(INSPECTION_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly inspectionService: InspectionService,
  ) {}
}
