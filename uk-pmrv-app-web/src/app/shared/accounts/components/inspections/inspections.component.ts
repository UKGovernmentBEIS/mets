import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { combineLatest, distinctUntilChanged, map, Observable, shareReplay } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';

import { RequestDetailsDTO, RequestSearchCriteria, RequestsService } from 'pmrv-api';

import { BaseComponent, Inspections } from '../shared/base/base.component';
import {
  inspectionsStatusesMap,
  inspectionsStatusesTagMap,
  inspectionsTypesMap,
  inspectionsTypesTagsMap,
} from './inspections.map';

@Component({
  selector: 'app-inspections',
  standalone: true,
  imports: [CommonModule, SharedModule, RouterModule],
  templateUrl: './inspections.component.html',
  styles: `
    span.search-results-list_item_status {
      float: right;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class InspectionsComponent extends BaseComponent implements OnInit {
  category: RequestSearchCriteria['category'] = 'INSPECTION';

  currentPageData$: Observable<Inspections[]>;
  data$: Observable<Inspections[]>;

  inspectionsTypesMap: Record<string, string[]>;
  inspectionsTypesTagsMap = inspectionsTypesTagsMap;
  inspectionsStatusesMap: Record<string, string>;
  inspectionsStatusesTagMap = inspectionsStatusesTagMap;

  constructor(
    fb: UntypedFormBuilder,
    authStore: AuthStore,
    route: ActivatedRoute,
    destroy$: DestroySubject,
    requestsService: RequestsService,
  ) {
    super(fb, authStore, route, destroy$, requestsService, null);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.inspectionsTypesMap = inspectionsTypesMap[this.domain];
    this.inspectionsStatusesMap = inspectionsStatusesMap[this.domain];

    this.data$ = this.getRequestDetails().pipe(
      map((results) => {
        const totalData = this.buildTotalData(results.requestDetails);

        this.totalDataNumber$.next(totalData.length);
        this.showPagination$.next(totalData.length > this.itemsPerPage);

        return totalData;
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.currentPageData$ = combineLatest([this.data$, this.page$.pipe(distinctUntilChanged())]).pipe(
      map(([totalData, page]) => this.getCurrentPageData(totalData, page - 1, this.itemsPerPage)),
    );
  }

  private getCurrentPageData(data: Inspections[], page: number, pageSize: number): Inspections[] {
    return data.slice(page * pageSize, (page + 1) * pageSize);
  }

  private buildTotalData(data: RequestDetailsDTO[]): Inspections[] {
    const detailsByYear = this.groupDetailsByYear(data);

    return (
      (detailsByYear &&
        Object.keys(detailsByYear)
          .map((year) => ({
            year: year,
            details: detailsByYear[year].sort(
              (a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime(),
            ),
          }))
          .sort((a, b) => Number(b.year) - Number(a.year))) ??
      []
    );
  }
}
