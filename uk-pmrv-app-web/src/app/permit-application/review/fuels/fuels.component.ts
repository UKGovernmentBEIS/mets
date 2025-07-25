import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { GovukTableColumn } from 'govuk-components';

import { EmissionPoint, EmissionSource, EmissionSummary, MeasurementDeviceOrMethod } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';

@Component({
  selector: 'app-fuels',
  templateUrl: './fuels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsComponent {
  showDiff$ = this.store.showDiff$;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(map((data) => data.groupKey));
  pointsAndSourcesColumns: GovukTableColumn<EmissionPoint | EmissionSource>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
  ];

  measurementDevicesColumns: GovukTableColumn<MeasurementDeviceOrMethod>[] = [
    { field: 'reference', header: 'Reference', widthClass: '' },
    { field: 'type', header: 'Type', widthClass: '' },
    { field: 'measurementRange', header: 'Measurement range', widthClass: '' },
    { field: 'meteringRangeUnits', header: 'Metering range units', widthClass: '' },
    { field: 'specifiedUncertaintyPercentage', header: 'Specified uncertainty', widthClass: '' },
    { field: 'location', header: 'Location', widthClass: '' },
  ];

  emissionSummariesColumns: GovukTableColumn<EmissionSummary>[] = [
    { field: 'sourceStream', header: 'Source stream', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'emissionSources', header: 'Emission sources', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'emissionPoints', header: 'Emission points', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'regulatedActivity', header: 'Regulated activity', widthClass: 'govuk-!-width-one-quarter' },
  ];

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
