import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  form: UntypedFormGroup;
  heading: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-create-summary',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule],
  templateUrl: './create-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateSummaryComponent {
  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.formProvider.form,
        heading: 'Create summary',
        isEditable: isEditable,
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
      .saveReviewVir('createSummary', 'in progress', null, this.formProvider.getFormValue())
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['summary'], {
          relativeTo: this.route,
        });
      });
  }
}
