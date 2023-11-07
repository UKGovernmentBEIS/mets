import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { ClosedComponent } from './closed/closed.component';
import { AdditionalDocumentsComponent } from './common/additional-documents/additional-documents.component';
import { AlcInformationComponent } from './common/alc-information/alc-information.component';
import { DeterminationComponent } from './common/determination/determination.component';
import { OperatorReportComponent } from './common/operator-report/operator-report.component';
import { SubmittedComponent } from './common/submitted.component';
import { VerificationReportComponent } from './common/verification-report/verification-report.component';
import { CompletedComponent } from './completed/completed.component';
import { DateSubmittedComponent } from './completed/submitted/date-submitted/date-submitted.component';
import { ResponseComponent } from './completed/submitted/response/response.component';
import { SubmittedComponent as CompletedSubmittedComponent } from './completed/submitted/submitted.component';
import { DoalActionComponent } from './doal-action.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { ProceededComponent } from './proceeded/proceeded.component';

const commonChildren = [
  {
    path: 'operator-report',
    data: {
      pageTitle: 'Upload operator activity level report',
      breadcrumb: 'Upload operator activity level report',
    },
    component: OperatorReportComponent,
  },
  {
    path: 'verification-report',
    data: {
      pageTitle: 'Upload verification report of the activity level report',
      breadcrumb: 'Upload verification report of the activity level report',
    },
    component: VerificationReportComponent,
  },
  {
    path: 'additional-documents',
    data: {
      pageTitle: 'Upload additional documents',
      breadcrumb: 'Upload additional documents',
    },
    component: AdditionalDocumentsComponent,
  },
  {
    path: 'alc-information',
    data: {
      pageTitle: 'Provide information about this activity level change',
      breadcrumb: 'Provide information about this activity level change',
    },
    component: AlcInformationComponent,
  },
  {
    path: 'determination',
    data: {
      pageTitle: 'Provide determination of activity level',
      breadcrumb: 'Provide determination of activity level',
    },
    component: DeterminationComponent,
  },
];

const routes: Route[] = [
  {
    path: '',
    component: DoalActionComponent,
    children: [
      {
        path: 'proceeded',
        data: { pageTitle: 'Activity level determination sent to UK Authority' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Activity level determination sent to UK Authority' },
            component: ProceededComponent,
          },
          {
            path: 'submitted',
            data: { pageTitle: 'Activity level determination sent to UK Authority' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Activity level determination sent to UK Authority' },
                component: SubmittedComponent,
              },
              ...commonChildren,
            ],
          },
        ],
      },
      {
        path: 'closed',
        data: { pageTitle: 'Activity level determination closed' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Activity level determination closed' },
            component: ClosedComponent,
          },
          {
            path: 'submitted',
            data: { pageTitle: 'Activity level determination closed' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Activity level determination closed' },
                component: SubmittedComponent,
              },
              ...commonChildren,
            ],
          },
        ],
      },
      {
        path: 'completed',
        data: { pageTitle: 'Activity level determination' },
        children: [
          {
            path: '',
            component: CompletedComponent,
          },
          {
            path: 'submitted',
            data: { pageTitle: 'Activity level determination' },
            children: [
              {
                path: '',
                data: { pageTitle: 'Activity level determination' },
                component: CompletedSubmittedComponent,
              },
              {
                path: 'date-submitted',
                data: {
                  pageTitle: 'Provide the date application was submitted to UK authorities',
                  breadcrumb: 'Provide the date application was submitted to UK authorities',
                },
                component: DateSubmittedComponent,
              },
              {
                path: 'response',
                data: {
                  pageTitle: 'Provide UK ETS Authority response',
                  breadcrumb: 'Provide UK ETS Authority response',
                },
                component: ResponseComponent,
              },
            ],
          },
        ],
      },
      {
        path: 'peer-review-decision',
        data: { pageTitle: 'Peer review decision' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Peer review decision' },
            component: PeerReviewDecisionComponent,
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DoalActionRoutingModule {}
