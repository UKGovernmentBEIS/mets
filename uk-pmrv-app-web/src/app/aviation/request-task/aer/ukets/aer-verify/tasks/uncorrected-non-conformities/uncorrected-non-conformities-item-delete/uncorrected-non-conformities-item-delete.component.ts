import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUncorrectedNonConformities } from 'pmrv-api';

import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';

@Component({
  selector: 'app-uncorrected-non-conformities-item-delete',
  templateUrl: './uncorrected-non-conformities-item-delete.component.html',
  standalone: true,
  imports: [SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesItemDeleteComponent implements OnInit {
  private uncorrectedNonConformities: AviationAerUncorrectedNonConformities;
  protected referenceNumber: string;

  get index() {
    return +this.route.snapshot.paramMap.get('index');
  }

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.uncorrectedNonConformities = this.formProvider.getFormValue();

    this.referenceNumber = this.uncorrectedNonConformities.uncorrectedNonConformities.find(
      (_, index) => index === this.index,
    ).reference;
  }

  onDelete() {
    const uncorrectedNonConformitiesValue: AviationAerUncorrectedNonConformities = {
      ...this.uncorrectedNonConformities,
      uncorrectedNonConformities: (
        this.uncorrectedNonConformities as AviationAerUncorrectedNonConformities
      ).uncorrectedNonConformities
        .filter((_, idx) => idx !== this.index)
        .map((item, idx) => ({
          reference: `B${idx + 1}`,
          explanation: item.explanation,
          materialEffect: item.materialEffect,
        })),
    };

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).saveAerVerify(
      {
        uncorrectedNonConformities: uncorrectedNonConformitiesValue,
      },
      'in progress',
    );

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setUncorrectedNonConformities({
      ...uncorrectedNonConformitiesValue,
    });

    this.formProvider.setFormValue({ ...uncorrectedNonConformitiesValue });

    this.router.navigate(['../..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
