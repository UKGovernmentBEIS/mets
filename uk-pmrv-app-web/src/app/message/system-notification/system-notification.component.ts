import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { map, tap } from 'rxjs';

@Component({
  selector: 'app-system-notification',
  templateUrl: './system-notification.component.html',
  styleUrl: './system-notification.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SystemNotificationComponent {
  private readonly info$ = this.route.data.pipe(map((x) => x?.review));
  private readonly requestTask$ = this.info$.pipe(map((x) => x?.requestTask));
  private readonly payload$ = this.requestTask$.pipe(map((x) => x?.payload));

  readonly allowedActions$ = this.info$.pipe(map((x) => x?.allowedRequestTaskActions));
  readonly taskId$ = this.requestTask$.pipe(map((x) => x?.id));
  readonly date$ = this.requestTask$.pipe(map((x) => x?.startDate));
  readonly title$ = this.payload$.pipe(
    map((x) => x?.subject),
    tap((subject) => this.title.setTitle(subject)),
  );
  readonly data$ = this.payload$.pipe(
    map((x) => x?.text),
    map((x) => (x.includes('&quot;') ? this.toHTML(x) : x)),
    map((content) =>
      content.replace(
        /(?:__|[*#])|\[(.*?)]\(.*?\)/gm,
        (text, p1) => `[${p1}](${this.getRoute(JSON.parse(text.match(/{.+}/gm)[0]))})`,
      ),
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly title: Title,
  ) {}

  archived(): void {
    this.router.navigate(['/dashboard']);
  }

  private getRoute(params: { action: string } & { [key: string]: number }): string {
    switch (params.action) {
      case 'ACCOUNT_USERS_SETUP':
        return `/accounts/${params.accountId}` + `#users`;
      case 'VERIFICATION_BODY_USERS_SETUP':
        return '/user/verifiers';
      default:
        return '/';
    }
  }

  toHTML(text: string): string {
    return new DOMParser().parseFromString(text, 'text/html').documentElement?.textContent;
  }
}
