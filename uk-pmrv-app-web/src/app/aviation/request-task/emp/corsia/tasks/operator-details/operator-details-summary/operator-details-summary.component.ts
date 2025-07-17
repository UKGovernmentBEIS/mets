import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-review-decision-group/emp-review-decision-group.component';
import { EmpVariationRegulatorLedDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group.component';
import { EmpVariationReviewDecisionGroupComponent } from '@aviation/request-task/emp/shared/emp-variation-review-decision-group/emp-variation-review-decision-group.component';
import { variationSubmitRegulatorLedRequestTaskTypes } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  getSubtaskSummaryValues,
  getSummaryHeaderForTaskType,
  parseCsv,
  showReviewDecisionComponent,
  showVariationRegLedDecisionComponent,
  showVariationReviewDecisionComponent,
} from '@aviation/request-task/util';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import {
  transformFiles,
  ViewModel,
} from '@aviation/shared/components/operator-details/utils/operator-details-summary.util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { OperatorDetailsFlightIdentificationTypePipe } from '@aviation/shared/pipes/operator-details-flight-identification-type.pipe';
import { OperatorDetailsLegalStatusTypePipe } from '@aviation/shared/pipes/operator-details-legal-status-type.pipe';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpCorsiaOperatorDetails, LimitedCompanyOrganisation, SubsidiaryCompanyCorsia } from 'pmrv-api';

import { BaseOperatorDetailsComponent } from '../base-operator-details.component';
import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

@Component({
  selector: 'app-operator-details-summary-page',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmpReviewDecisionGroupComponent,
    EmpVariationReviewDecisionGroupComponent,
    OperatorDetailsFlightIdentificationTypePipe,
    OperatorDetailsLegalStatusTypePipe,
    OperatorDetailsSummaryTemplateComponent,
    EmpVariationRegulatorLedDecisionGroupComponent,
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
    this.store.pipe(empCorsiaQuery.selectStatusForTask('operatorDetails')),
    this.store.pipe(empCorsiaQuery.selectOriginalEmpContainer),
  ]).pipe(
    map(([type, isEditable, taskStatus, originalEmpContainer]) => {
      const operatorDetails = getSubtaskSummaryValues(this.form);

      return {
        operatorDetails: {
          ...operatorDetails,
          operatorName: operatorDetails?.operatorName,
        },
        certificationFiles: transformFiles(
          operatorDetails?.airOperatingCertificate?.certificateFiles ?? [],
          this.downloadUrl,
        ),
        evidenceFiles: transformFiles(operatorDetails?.organisationStructure?.evidenceFiles ?? [], this.downloadUrl),
        pageHeader: getSummaryHeaderForTaskType(type, 'operatorDetails'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          variationSubmitRegulatorLedRequestTaskTypes.includes(type),
        showDecision: showReviewDecisionComponent.includes(type),
        showVariationDecision: showVariationReviewDecisionComponent.includes(type),
        showVariationRegLedDecision: showVariationRegLedDecisionComponent.includes(type),
        showDiff: !!originalEmpContainer,
        originalOperatorDetails: originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails?.subsidiaryCompanies
          ? {
              ...originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails,
              subsidiaryCompanies:
                originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails?.subsidiaryCompanies.map(
                  (subCompany: SubsidiaryCompanyCorsia) => {
                    return {
                      ...subCompany,
                      airOperatingCertificate: {
                        ...subCompany?.airOperatingCertificate,
                        certificateFiles: subCompany?.airOperatingCertificate?.certificateFiles?.map((certFile) => {
                          return {
                            file: { name: this.store.empCorsiaDelegate.payload.empAttachments[certFile] },
                            uuid: certFile,
                          } as any;
                        }),
                      },
                    };
                  },
                ),
            }
          : originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails,
        originalCertificationFiles: transformFiles(
          originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails?.airOperatingCertificate?.certificateFiles?.map(
            (certFile) => {
              return {
                file: { name: this.store.empCorsiaDelegate.payload.empAttachments[certFile] },
                uuid: certFile,
              } as any;
            },
          ),
          this.downloadUrl,
        ),
        originalEvidenceFiles: transformFiles(
          (
            originalEmpContainer?.emissionsMonitoringPlan?.operatorDetails
              ?.organisationStructure as LimitedCompanyOrganisation
          )?.evidenceFiles?.map((evidenceFile) => {
            return {
              file: { name: this.store.empCorsiaDelegate.payload.empAttachments[evidenceFile] },
              uuid: evidenceFile,
            } as any;
          }),
          this.downloadUrl,
        ),
      };
    }),
  );

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

  onSubmit() {
    const operatorDetails = this.form.value as EmpCorsiaOperatorDetails;

    if (operatorDetails.airOperatingCertificate.certificateFiles?.length > 0) {
      operatorDetails.airOperatingCertificate.certificateFiles =
        operatorDetails.airOperatingCertificate.certificateFiles?.map((doc: any) => doc.uuid);
    }

    if ((operatorDetails.organisationStructure as LimitedCompanyOrganisation).evidenceFiles?.length > 0) {
      (operatorDetails.organisationStructure as LimitedCompanyOrganisation).evidenceFiles = (
        operatorDetails.organisationStructure as LimitedCompanyOrganisation
      ).evidenceFiles?.map((doc: any) => doc.uuid);
    }

    if (operatorDetails.flightIdentification.aircraftRegistrationMarkings?.length > 0) {
      operatorDetails.flightIdentification.aircraftRegistrationMarkings = parseCsv(
        operatorDetails.flightIdentification.aircraftRegistrationMarkings as unknown as string,
      );

      if (operatorDetails.subsidiaryCompanies?.length > 0) {
        operatorDetails.subsidiaryCompanies = operatorDetails.subsidiaryCompanies.map((subsidiaryCompany) => ({
          ...subsidiaryCompany,
          flightIdentification: {
            ...subsidiaryCompany.flightIdentification,
            aircraftRegistrationMarkings: parseCsv(
              subsidiaryCompany.flightIdentification.aircraftRegistrationMarkings as unknown as string,
            ),
          },
        }));
      }
    }

    this.submitForm(null, operatorDetails, '../../../../', 'complete');
  }
}
