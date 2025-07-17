import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { EmpSubmittedViewModel, getEmpSubmittedViewModelData } from '@aviation/request-action/emp/util/emp.util';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { unparseCsv } from '@aviation/request-task/util';
import { EmpReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-review-decision-group-summary/emp-review-decision-group-summary.component';
import { EmpVariationRegulatorLedDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-regulator-led-decision-group-summary/emp-variation-regulator-led-decision-group-summary.component';
import { EmpVariationReviewDecisionGroupSummaryComponent } from '@aviation/shared/components/emp/emp-variation-review-decision-group-summary/emp-variation-review-decision-group-summary.component';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/emp-corsia/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { transformFiles } from '@aviation/shared/components/operator-details/utils/operator-details-summary.util';
import { SharedModule } from '@shared/shared.module';

import { AirOperatingCertificateCorsia, EmpCorsiaOperatorDetails, LimitedCompanyOrganisation } from 'pmrv-api';

interface ViewModel {
  data: EmpCorsiaOperatorDetails;
  certificationFiles: { fileName: string; downloadUrl: string }[];
  evidenceFiles: { fileName: string; downloadUrl: string }[];
  originalCertificationFiles: { fileName: string; downloadUrl: string }[];
  originalEvidenceFiles: { fileName: string; downloadUrl: string }[];
}

@Component({
  selector: 'app-operator-details',
  standalone: true,
  imports: [
    SharedModule,
    RequestActionTaskComponent,
    OperatorDetailsSummaryTemplateComponent,
    EmpReviewDecisionGroupSummaryComponent,
    EmpVariationReviewDecisionGroupSummaryComponent,
    EmpVariationRegulatorLedDecisionGroupSummaryComponent,
  ],
  templateUrl: './operator-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  downloadUrl = `${this.store.empDelegate.baseFileAttachmentDownloadUrl}/`;

  vm$: Observable<ViewModel & EmpSubmittedViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRegulatorViewer),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, regulatorViewer, requestActionType]) => ({
      data: {
        ...payload.emissionsMonitoringPlan.operatorDetails,
        flightIdentification: {
          ...payload.emissionsMonitoringPlan.operatorDetails.flightIdentification,
          aircraftRegistrationMarkings: unparseCsv(
            payload.emissionsMonitoringPlan.operatorDetails.flightIdentification.aircraftRegistrationMarkings || [],
          ) as any,
        },
        subsidiaryCompanies: (payload.emissionsMonitoringPlan.operatorDetails.subsidiaryCompanies || []).map(
          (subsidiaryCompany) => ({
            ...subsidiaryCompany,
            airOperatingCertificate: {
              ...subsidiaryCompany.airOperatingCertificate,
              certificateFiles: transformFiles(
                (this.filesReadyToBeTransformed(subsidiaryCompany.airOperatingCertificate?.certificateFiles) as any) ??
                  [],
                this.downloadUrl,
              ) as any,
            },
          }),
        ),
      },
      certificationFiles: transformFiles(
        (this.filesReadyToBeTransformed(
          payload.emissionsMonitoringPlan.operatorDetails.airOperatingCertificate?.certificateFiles,
        ) as any) ?? [],
        this.downloadUrl,
      ),
      evidenceFiles: transformFiles(
        (this.filesReadyToBeTransformed(
          (payload.emissionsMonitoringPlan.operatorDetails.organisationStructure as LimitedCompanyOrganisation)
            ?.evidenceFiles,
        ) as any) ?? [],
        this.downloadUrl,
      ),
      originalCertificationFiles: transformFiles(
        (this.filesReadyToBeTransformed(
          payload.originalEmpContainer?.emissionsMonitoringPlan.operatorDetails.airOperatingCertificate
            ?.certificateFiles,
        ) as any) ?? [],
        this.downloadUrl,
      ),
      originalEvidenceFiles: transformFiles(
        (this.filesReadyToBeTransformed(
          (
            payload.originalEmpContainer?.emissionsMonitoringPlan.operatorDetails
              .organisationStructure as LimitedCompanyOrganisation
          )?.evidenceFiles,
        ) as any) ?? [],
        this.downloadUrl,
      ),
      ...getEmpSubmittedViewModelData(requestActionType, payload, regulatorViewer, this.downloadUrl, 'operatorDetails'),
    })),
  );

  constructor(public store: RequestActionStore) {}

  private filesReadyToBeTransformed(files: AirOperatingCertificateCorsia['certificateFiles']) {
    return (files || []).map((uuid) => ({
      file: { name: this.store.empDelegate.payload.empAttachments[uuid] } as File,
      uuid,
    }));
  }
}
