import { Component, Input, OnChanges } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Params } from '@angular/router';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'govuk-back-link',
  template: ` <a [routerLink]="routerLink" [queryParams]="queryParams" class="govuk-back-link"> Back </a> `,
})
export class BackLinkComponent implements OnChanges {
  @Input() link: string;
  @Input() route: ActivatedRouteSnapshot;

  routerLink: string[];
  queryParams: Params | null;

  ngOnChanges(): void {
    const urlTree = createUrlTreeFromSnapshot(this.route, [this.link], this.route.queryParams, this.route.fragment);
    this.routerLink = urlTree.root.children.primary.segments.map((s) => s.path);
    this.queryParams = urlTree.queryParams;
  }
}
