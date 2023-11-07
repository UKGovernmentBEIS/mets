import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { OpinionStatementFormProvider } from '../opinion-statement-form.provider';

@Component({
  selector: 'app-opinion-statement-visit-form',
  templateUrl: './opinion-statement-visit-form.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementVisitFormComponent {
  protected siteVisitGroup = this.formProvider.siteVisitGroup;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: OpinionStatementFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.siteVisitGroup.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ opinionStatement: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        let path = '';

        if (this.siteVisitGroup.value.type === 'IN_PERSON') {
          this.formProvider.addInPersonSiteGroup();
          path = 'in-person';
        } else {
          this.formProvider.addVirtualSiteGroup();
          path = 'virtual';
        }

        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setOpinionStatement(this.formProvider.getFormValue());
        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}
