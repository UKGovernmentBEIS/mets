import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  parseCsv,
  showReviewDecisionComponent,
} from '@aviation/request-task/util';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import {
  transformFiles,
  ViewModel,
} from '@aviation/shared/components/operator-details/utils/operator-details-summary.util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import {
  AirOperatingCertificate,
  AviationOperatorDetails,
  FlightIdentification,
  LimitedCompanyOrganisation,
} from 'pmrv-api';

import { aerQuery } from '../../../../shared/aer.selectors';
import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-summary-page',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    OperatorDetailsFlightIdentificationTypePipe,
    OperatorDetailsLegalStatusTypePipe,
    OperatorDetailsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  templateUrl: './operator-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class OperatorDetailsSummaryComponent extends BaseOperatorDetailsComponent implements OnInit {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('operatorDetails')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      const operatorDetails = getSubtaskSummaryValues(this.form);

      return {
        operatorDetails: {
          ...operatorDetails,
          operatorName: operatorDetails?.operatorName?.operatorName,
          crcoCode: operatorDetails?.operatorName?.crcoCode,
        },
        certificationFiles: transformFiles(
          operatorDetails?.airOperatingCertificate?.certificateFiles ?? [],
          this.downloadUrl,
        ),
        isCorsia: false,
        evidenceFiles: transformFiles(operatorDetails?.organisationStructure?.evidenceFiles ?? [], this.downloadUrl),
        pageHeader: getSummaryHeaderForTaskType(type, 'operatorDetails'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    public router: Router,
    public route: ActivatedRoute,
    public pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OperatorDetailsFormProvider,
    protected readonly store: RequestTaskStore,
    protected readonly destroy$: DestroySubject,
  ) {
    super(router, route, pendingRequestService, formProvider, store, destroy$);
  }

  onSubmit() {
    const { operatorName, airOperatingCertificate, organisationStructure, flightIdentification } = this.form
      .value as Record<keyof AviationOperatorDetails, any>;
    const operatorDetails = {
      ...this.form.value,
      operatorName: operatorName.operatorName,
      crcoCode: operatorName.crcoCode,
    } as AviationOperatorDetails;

    if ((airOperatingCertificate as AirOperatingCertificate).certificateFiles?.length > 0) {
      operatorDetails.airOperatingCertificate.certificateFiles = airOperatingCertificate.certificateFiles?.map(
        (doc: FileUpload) => doc.uuid,
      );
    }

    if ((organisationStructure as LimitedCompanyOrganisation).evidenceFiles?.length > 0) {
      (operatorDetails.organisationStructure as LimitedCompanyOrganisation).evidenceFiles =
        organisationStructure.evidenceFiles?.map((doc: FileUpload) => doc.uuid);
    }

    if ((flightIdentification as FlightIdentification).aircraftRegistrationMarkings?.length > 0) {
      operatorDetails.flightIdentification.aircraftRegistrationMarkings = parseCsv(
        operatorDetails.flightIdentification.aircraftRegistrationMarkings as unknown as string,
      );
    }

    this.submitForm(null, operatorDetails, '../../../', 'complete');
  }
}
