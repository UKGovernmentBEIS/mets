import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class BackLinkService {
  // constructor(
  // @Inject(BACK_LINK) private readonly backLink$: BehaviorSubject<boolean>,
  // @Inject(BACK_LINK_TARGET) private readonly backLinkTarget$: BehaviorSubject<{ link: string; fragment: string }>,
  // ) {}

  // eslint-disable-next-line
  show(link?: string, fragment?: string): void {
    // this.backLink$.next(true);
    // if (link) {
    //   this.backLinkTarget$.next({
    //     link: link,
    //     ...(fragment ? { fragment: fragment } : null),
    //   });
    // }
  }

  // eslint-disable-next-line
  hide(): void {
    // this.backLink$.next(false);
    // this.backLinkTarget$.next(null);
  }
}
