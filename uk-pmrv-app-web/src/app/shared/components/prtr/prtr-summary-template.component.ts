import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { GovukTableColumn } from 'govuk-components';

import { PollutantRegisterActivities } from 'pmrv-api';

@Component({
  selector: 'app-prtr-summary-template',
  templateUrl: './prtr-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrtrSummaryTemplateComponent implements OnInit {
  @Input() cssClass: string;
  @Input() noBottomBorder: boolean;
  @Input() activities: PollutantRegisterActivities;
  @Input() isEditable: boolean;

  activityItems: { activity: any }[];
  columns: GovukTableColumn[] = [
    { field: 'description', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'activity', header: '', widthClass: 'govuk-input--width-20' },
    { field: 'delete', header: '', widthClass: 'govuk-input--width-20' },
  ];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.activityItems = this.activities?.activities?.map((item) => ({ activity: item })) ?? [];
  }

  addAnother(): void {
    this.router.navigate([`../activity/${this.activityItems.length}`], { relativeTo: this.route });
  }
}
