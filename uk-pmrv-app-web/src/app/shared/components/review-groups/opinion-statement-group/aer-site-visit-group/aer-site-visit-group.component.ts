import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { SiteVisitsService } from '@shared/components/review-groups/opinion-statement-group/services/site-visits.service';

import { SiteVisit } from 'pmrv-api';

@Component({
  selector: 'app-aer-site-visit-group',
  // eslint-disable-next-line @angular-eslint/component-max-inline-declarations
  template: `
    <ng-container *ngIf="siteVisit">
      <h2 class="govuk-heading-m">Site verification</h2>
      <dl govuk-summary-list [class]="cssClass">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Did your team conduct any site visits?</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>{{ resolvedSiteVisitTypeLabel }}</dd>
          <dd *ngIf="isEditable" govukSummaryListRowActions>
            <a govukLink [routerLink]="baseRouterLink">Change</a>
          </dd>
        </div>
        <div govukSummaryListRow *ngIf="siteVisit?.visitDates as visitDates">
          <dt govukSummaryListRowKey>Dates of visit</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>
            <ng-container *ngFor="let visitDate of visitDates"> {{ visitDate | date }}<br /></ng-container>
          </dd>
          <dd *ngIf="isEditable" govukSummaryListRowActions>
            <a govukLink [routerLink]="resolvedSiteVisitLink">Change</a>
          </dd>
        </div>
        <div govukSummaryListRow *ngIf="siteVisit?.teamMembers as teamMembers">
          <dt govukSummaryListRowKey>Team members who conducted the site visit</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>{{ teamMembers }}</dd>
          <dd *ngIf="isEditable" govukSummaryListRowActions>
            <a govukLink [routerLink]="resolvedSiteVisitLink">Change</a>
          </dd>
        </div>
        <div govukSummaryListRow *ngIf="siteVisit?.reason as reason">
          <dt govukSummaryListRowKey>{{ reasonFragment }}</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>{{ reason }}</dd>
          <dd *ngIf="isEditable" govukSummaryListRowActions>
            <a govukLink [routerLink]="resolvedSiteVisitLink">Change</a>
          </dd>
        </div>
      </dl>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerSiteVisitGroupComponent implements OnInit {
  @Input() siteVisit: any;
  @Input() isEditable: boolean;
  @Input() cssClass: string;

  reasonFragment = '';
  baseRouterLink = '../site-visits';
  resolvedSiteVisitLink = '';
  resolvedSiteVisitTypeLabel = '';

  constructor(private readonly siteVisitsService: SiteVisitsService) {}

  ngOnInit() {
    this.siteVisit.siteVisitType === 'VIRTUAL'
      ? (this.reasonFragment = 'Reasons for conducting a virtual site visit')
      : (this.reasonFragment = 'Reasons for not conducting a site visit');

    this.resolvedSiteVisitLink =
      this.baseRouterLink +
      '/' +
      this.siteVisitsService.mapVisitTypeToPath(this.siteVisit.siteVisitType as SiteVisit['siteVisitType']);

    this.resolvedSiteVisitTypeLabel = this.siteVisitsService.mapVisitTypeToLabel(
      this.siteVisit.siteVisitType as SiteVisit['siteVisitType'],
    );
  }
}
