import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, Input } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSubtaskSummaryValues } from '@aviation/request-task/util';
import { OperatorDetailsSubsidiaryListComponent } from '@aviation/shared/components/emp-corsia/operator-details/operator-details-subsidiary-list/operator-details-subsidiary-list.component';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

interface ViewModel {
  subsidiaryCompanies: any | null;
  isEditable: boolean | null;
}
@Component({
  selector: 'app-operator-details-subsidiary-companies',
  templateUrl: './operator-details-subsidiary-companies.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    RouterLinkWithHref,
    NgIf,
    NgFor,
    SharedModule,
    OperatorDetailsSubsidiaryListComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsSubsidiaryCompaniesComponent extends BaseOperatorDetailsComponent {
  @Input() isEditable = false;
  @Input() queryParams: Params = {};

  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([requestActionType]) => {
      return {
        subsidiaryCompanies: requestActionType ? getSubtaskSummaryValues(this.form)?.subsidiaryCompanies : null,
        isEditable: requestActionType,
      };
    }),
  );

  subsidiaryCompanies$ = this.vm$.pipe(map((vm) => vm.subsidiaryCompanies));

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsCorsiaFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  handleSubsidiaryList(index: number) {
    this.formProvider.removeSubsidiaryCompanyItem(index);

    this.submitForm('subsidiaryCompanies', { subsidiaryCompanies: this.form.value.subsidiaryCompanies }, '../list');
  }

  addAnotherSubsidiaryCompany() {
    this.formProvider.addSubsidiaryCompany();

    this.router.navigate(['..', 'add'], {
      relativeTo: this.route,
      queryParams: this.queryParams,
    });
  }
}
