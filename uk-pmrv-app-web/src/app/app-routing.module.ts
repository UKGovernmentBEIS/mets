import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';

import { isFeatureEnabled } from '@core/config/feature.guard';
import { AviationAuthGuard } from '@core/guards/aviation-auth.guard';
import { InstallationAuthGuard } from '@core/guards/installation-auth.guard';
import { LoggedInGuard } from '@core/guards/logged-in.guard';
import { NonAuthGuard } from '@core/guards/non-auth.guard';
import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { TermsAndConditionsGuard } from '@core/guards/terms-and-conditions.guard';

import { AccessibilityComponent } from './accessibility/accessibility.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { InstallationAccountApplicationGuard } from './installation-account-application/installation-account-application.guard';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LandingPageGuard } from './landing-page/landing-page.guard';
import { LegislationComponent } from './legislation/legislation.component';
import { RoleTypeGuard } from './shared/guards/role-type.guard';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TimedOutComponent } from './timeout/timed-out/timed-out.component';
import { VersionComponent } from './version/version.component';

const routes: Routes = [
  {
    path: 'landing',
    data: { pageTitle: 'Manage your UK Emissions Trading Scheme reporting', breadcrumb: 'Home' },
    component: LandingPageComponent,
    canActivate: [LandingPageGuard],
    canDeactivate: [LandingPageGuard],
  },
  {
    path: '',
    redirectTo: 'landing',
    pathMatch: 'full',
  },
  {
    path: '',
    data: { breadcrumb: 'Home' },
    children: [
      {
        path: 'about',
        data: { pageTitle: 'About', breadcrumb: true },
        component: VersionComponent,
      },
      {
        path: 'accessibility',
        data: { pageTitle: 'Accessibility Statement', breadcrumb: true },
        component: AccessibilityComponent,
      },
      {
        path: 'contact-us',
        data: { pageTitle: 'Contact us', breadcrumb: true },
        component: ContactUsComponent,
      },
      {
        path: 'legislation',
        data: { pageTitle: 'UK ETS legislation', breadcrumb: true },
        component: LegislationComponent,
      },
      {
        path: 'feedback',
        data: { pageTitle: 'Feedback', breadcrumb: true },
        component: FeedbackComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'forgot-password',
        loadChildren: () => import('./forgot-password/forgot-password.module').then((m) => m.ForgotPasswordModule),
      },
      {
        path: 'registration',
        loadChildren: () => import('./registration/registration.module').then((m) => m.RegistrationModule),
      },
    ],
  },
  {
    path: 'error',
    loadChildren: () => import('./error/error.module').then((m) => m.ErrorModule),
  },
  {
    path: 'invitation',
    loadChildren: () => import('./invitation/invitation.module').then((m) => m.InvitationModule),
  },
  {
    path: '2fa',
    loadChildren: () => import('./two-fa/two-fa.module').then((m) => m.TwoFaModule),
  },
  {
    path: 'timed-out',
    data: { pageTitle: 'Session Timeout' },
    canActivate: [NonAuthGuard],
    component: TimedOutComponent,
  },
  {
    path: '',
    canActivate: [LoggedInGuard, TermsAndConditionsGuard],
    children: [
      {
        path: 'aviation',
        data: { breadcrumb: 'Dashboard' },
        canActivate: [AviationAuthGuard],
        canMatch: [isFeatureEnabled('aviation')],
        loadChildren: () => import('./aviation/aviation.module').then((m) => m.AviationModule),
      },
      {
        path: '',
        data: { breadcrumb: 'Dashboard' },
        children: [
          {
            path: 'dashboard',
            data: { breadcrumb: 'Dashboard' },
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./dashboard/dashboard.module').then((m) => m.DashboardModule),
          },
          {
            path: 'message',
            data: { breadcrumb: 'Messages' },
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./message/message.module').then((m) => m.MessageModule),
          },
          {
            path: 'accounts',
            data: { breadcrumb: 'Accounts' },
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./accounts/accounts.module').then((m) => m.AccountsModule),
          },
          {
            path: 'user',
            canActivate: [InstallationAuthGuard],
            children: [
              {
                path: 'regulators',
                data: { breadcrumb: 'Regulator users and contacts' },
                loadChildren: () => import('./regulators/regulators.module').then((m) => m.RegulatorsModule),
              },
              {
                path: 'verifiers',
                data: { breadcrumb: 'Manage verifier users' },
                loadChildren: () => import('./verifiers/verifiers.module').then((m) => m.VerifiersModule),
              },
            ],
          },
          {
            path: 'verification-bodies',
            canActivate: [InstallationAuthGuard],
            data: { pageTitle: 'Manage verification bodies', breadcrumb: true },
            loadChildren: () =>
              import('./verification-bodies/verification-bodies.module').then((m) => m.VerificationBodiesModule),
          },
          {
            path: 'installation-account',
            canActivate: [InstallationAccountApplicationGuard],
            loadChildren: () =>
              import('./installation-account-application/installation-account-application.module').then(
                (m) => m.InstallationAccountApplicationModule,
              ),
          },
          {
            path: 'permit-issuance',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./permit-issuance/permit-issuance.module').then((m) => m.PermitIssuanceModule),
          },
          {
            path: 'permit-variation',
            canActivate: [InstallationAuthGuard],
            loadChildren: () =>
              import('./permit-variation/permit-variation.module').then((m) => m.PermitVariationModule),
          },
          {
            path: 'permit-transfer',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./permit-transfer/permit-transfer.module').then((m) => m.PermitTransferModule),
          },
          {
            path: 'permit-revocation',
            canActivate: [InstallationAuthGuard],
            loadChildren: () =>
              import('./permit-revocation/permit-revocation.module').then((m) => m.PermitRevocationModule),
          },
          {
            path: 'permit-surrender',
            canActivate: [InstallationAuthGuard],
            loadChildren: () =>
              import('./permit-surrender/permit-surrender.module').then((m) => m.PermitSurrenderModule),
          },
          {
            path: 'rfi',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./rfi/rfi.module').then((m) => m.RfiModule),
          },
          {
            path: 'rde',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./rde/rde.module').then((m) => m.RdeModule),
          },
          {
            path: 'payment',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./payment/payment.module').then((m) => m.PaymentModule),
          },
          {
            path: 'terms',
            data: { pageTitle: 'Accept terms and conditions' },
            component: TermsAndConditionsComponent,
            canActivate: [TermsAndConditionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'templates',
            data: { breadcrumb: 'Templates' },
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./templates/templates.module').then((m) => m.TemplatesModule),
          },
          {
            path: 'tasks',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./tasks/tasks.module').then((m) => m.TasksModule),
          },
          {
            path: 'actions',
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./actions/actions.module').then((m) => m.ActionsModule),
          },
          {
            path: 'mi-reports',
            data: { breadcrumb: 'MI Reports' },
            canActivate: [InstallationAuthGuard],
            loadChildren: () => import('./mi-reports/mi-reports.module').then((m) => m.MiReportsModule),
          },
          {
            path: 'workflows',
            canActivate: [InstallationAuthGuard],
            children: [
              {
                path: 'batch-variations',
                data: {
                  roleTypeGuards: 'REGULATOR',
                  requestType: 'batch-variation',
                  requestTypeUrlPath: 'batch-variations',
                  breadcrumb: 'Batch variations',
                },
                canActivate: [RoleTypeGuard],
                loadChildren: () =>
                  import('./permit-batch-reissue/permit-batch-reissue.module').then((m) => m.PermitBatchReissueModule),
              },
            ],
          },
        ],
      },
    ],
  },
  // The route below handles all unknown routes / Page Not Found functionality.
  // Please keep this route last else there will be unexpected behavior.
  {
    path: '**',
    redirectTo: 'error/404',
  },
];

const routerOptions: ExtraOptions = {
  paramsInheritanceStrategy: 'always',
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule],
  providers: [InstallationAccountApplicationGuard, LandingPageGuard],
})
export class AppRoutingModule {}
