import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';

import { Observable } from 'rxjs';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'pmrv-api';

export const viewCustomReportResolver: ResolveFn<MiReportUserDefinedDTO> = (
  route: ActivatedRouteSnapshot,
): Observable<MiReportUserDefinedDTO> => {
  const miReportsService = inject(MiReportsUserDefinedService);

  return miReportsService.getReport(Number(route.paramMap.get('id')));
};
