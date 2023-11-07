import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { UncorrectedItem } from 'pmrv-api';

import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';

interface ViewModel {
  isEditable: boolean;
  uncorrectedNonConformities: UncorrectedItem[];
}

@Component({
  selector: 'app-uncorrected-non-conformities-list',
  templateUrl: './uncorrected-non-conformities-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [GovukComponentsModule, SharedModule, RouterLink, UncorrectedItemGroupComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesListComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([isEditable]) => ({
      isEditable,
      uncorrectedNonConformities: this.formProvider.uncorrectedNonConformitiesGroup.value.uncorrectedNonConformities,
    })),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ uncorrectedNonConformities: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setUncorrectedNonConformities(
          this.formProvider.getFormValue(),
        );
        this.router.navigate(['..', 'prior-year-issues'], { relativeTo: this.route });
      });
  }
}
