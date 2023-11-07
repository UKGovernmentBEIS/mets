import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { AirImprovementTitlePipe } from '@shared/air-shared/pipes/air-improvement-title.pipe';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { OperatorAirImprovementResponseAll } from '@shared/air-shared/types/operator-air-improvement-response-all.type';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { AirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AirService } from '../../core/air.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  payload$ = this.airService.payload$ as Observable<AirApplicationSubmittedRequestActionPayload>;
  operatorAirImprovementResponse$ = this.payload$.pipe(
    map((payload) => payload.operatorImprovementResponses[this.reference] as OperatorAirImprovementResponseAll),
  );
  documentFiles$ = this.operatorAirImprovementResponse$.pipe(
    map((payload) => (payload?.files ? this.airService.getOperatorDownloadUrlFiles(payload?.files) : [])),
  );
  breadcrumbs: BreadcrumbItem[];

  constructor(
    private readonly airService: AirService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly titlePipe: AirImprovementTitlePipe,
  ) {}

  ngOnInit(): void {
    const parentUrlSegments = this.route.snapshot.pathFromRoot.map((route) => route.url).flat();
    const parentUrl = '/' + parentUrlSegments.slice(0, parentUrlSegments.length - 2).join('/');

    this.breadcrumbs = [
      {
        link: this.router.url.startsWith('/aviation') ? ['/aviation/dashboard'] : ['/dashboard'],
        text: 'Dashboard',
      },
      {
        link: [parentUrl],
        text: 'Annual improvement report submitted',
      },
      {
        text: this.titlePipe.transform(this.airImprovement, this.reference),
      },
    ];

    this.breadcrumbService.show(this.breadcrumbs);
  }
}
