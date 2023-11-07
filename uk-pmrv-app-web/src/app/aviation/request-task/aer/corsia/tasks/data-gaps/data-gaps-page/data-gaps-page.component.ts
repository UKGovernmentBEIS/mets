import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { DataGapsExistFormComponent } from '@aviation/shared/components/aer/data-gaps/data-gaps-exist-form';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { DataGapsFormProvider } from '../data-gaps-form.provider';

@Component({
  selector: 'app-data-gaps-page',
  templateUrl: './data-gaps-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, DataGapsPageComponent, DataGapsExistFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class DataGapsPageComponent {
  protected form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    this.store.aerDelegate
      .saveAer({ dataGaps: { ...this.formProvider.getFormValue(), ...this.form.value } }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        let nextRoute = 'threshold';
        if (!this.form.value.exist) {
          nextRoute = 'summary';
        }

        this.router.navigate([nextRoute], { relativeTo: this.route });
      });
  }
}
