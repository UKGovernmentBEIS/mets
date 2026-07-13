import { Component, input, OnChanges } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Params, RouterLink } from '@angular/router';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'govuk-back-link',
  imports: [RouterLink],
  template: `
    <a
      [routerLink]="routerLink"
      [queryParams]="queryParams"
      [fragment]="fragment()"
      class="govuk-back-link"
      [class.govuk-back-link--inverse]="inverse()">
      Back
    </a>
  `,
})
export class BackLinkComponent implements OnChanges {
  readonly link = input<string>();
  readonly route = input<ActivatedRouteSnapshot>();
  readonly inverse = input(false);
  readonly fragment = input<string>();

  routerLink: string[];
  queryParams: Params | null;

  ngOnChanges(): void {
    const urlTree = createUrlTreeFromSnapshot(
      this.route(),
      [this.link()],
      this.route().queryParams,
      this.route().fragment,
    );
    this.routerLink = urlTree.root.children.primary.segments.map((s) => s.path);
    this.queryParams = urlTree.queryParams;
  }
}
