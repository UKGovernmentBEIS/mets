import { inject } from '@angular/core';
import { Router, Routes } from '@angular/router';

import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { EmpCorsiaOperatorDetails } from 'pmrv-api';

import { OperatorDetailsCorsiaFormProvider } from '../operator-details-form.provider';

export const SUBSIDIARY_COMPANY_ROUTES: Routes = [
  {
    path: 'list',
    data: { backlink: '../../' },
    loadComponent: () =>
      import('./operator-details-subsidiary-companies.component').then(
        (c) => c.OperatorDetailsSubsidiaryCompaniesComponent,
      ),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'has-subsidiary-companies',
  },
  {
    path: 'has-subsidiary-companies',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./has-subsidiary-company/has-subsidiary-company.component').then((c) => c.HasSubsidiaryCompanyComponent),
  },
  {
    path: 'add',
    resolve: {
      backlink: () => {
        const formProvider = inject<OperatorDetailsCorsiaFormProvider>(TASK_FORM_PROVIDER);
        const subsidiaryCompanies = formProvider.form.controls.subsidiaryCompanies
          .value as EmpCorsiaOperatorDetails['subsidiaryCompanies'];

        return subsidiaryCompanies.length > 1 ? '../list' : '../has-subsidiary-companies';
      },
    },
    loadComponent: () =>
      import('./add-subsidiary-company/add-subsidiary-company.component').then((c) => c.AddSubsidiaryCompanyComponent),
  },
  {
    path: ':index/edit',
    resolve: {
      backlink: () => {
        const router = inject(Router);
        const isChangeClicked = !!router.getCurrentNavigation().finalUrl.queryParams?.change;

        return isChangeClicked ? '../../../summary' : '../../list';
      },
    },
    loadComponent: () =>
      import('./add-subsidiary-company/add-subsidiary-company.component').then((c) => c.AddSubsidiaryCompanyComponent),
  },
];
