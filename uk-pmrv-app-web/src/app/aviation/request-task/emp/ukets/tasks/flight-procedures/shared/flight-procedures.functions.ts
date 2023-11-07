import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';

export function onSubmitFlightProcedures(
  store: RequestTaskStore,
  form: FormGroup,
  pageFieldForm: string,
  nextStep: string,
  router: Router,
  route: ActivatedRoute,
  pendingRequestService: PendingRequestService,
) {
  if (form?.valid) {
    const dataToSave = {
      ...store.empUkEtsDelegate.payload.emissionsMonitoringPlan.flightAndAircraftProcedures,
      [pageFieldForm]: form.value,
    } as any;

    const saveEmp$ = store.empUkEtsDelegate.saveEmp({ flightAndAircraftProcedures: dataToSave }, 'in progress');

    const trackRequest$ = pendingRequestService?.trackRequest();
    const request$ = trackRequest$ ? saveEmp$?.pipe(trackRequest$) : saveEmp$;

    return request$?.subscribe(() => {
      store.empUkEtsDelegate.setFlightProcedures(dataToSave);
      router.navigate([(pageFieldForm !== 'aircraftUsedDetails' ? '../' : '') + nextStep], {
        relativeTo: route,
      });
    });
  }
}
