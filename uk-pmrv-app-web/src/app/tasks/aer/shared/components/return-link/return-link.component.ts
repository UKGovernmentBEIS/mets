import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

@Component({
  selector: 'app-return-link',
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  @Input() returnLink;
  title: string;

  link$ = this.route.url.pipe(
    map((url) => {
      const isIncludedInUrl = url.some(
        (segment) => segment.path.includes('summary') || segment.path.includes('answers'),
      );
      this.title = 'Emissions report';

      return this.returnLink ? this.returnLink : isIncludedInUrl ? '../..' : '..';
    }),
  );

  constructor(private readonly route: ActivatedRoute) {}
}
