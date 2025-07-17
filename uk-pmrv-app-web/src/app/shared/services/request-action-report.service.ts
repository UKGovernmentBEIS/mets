import { Injectable } from '@angular/core';

import { BehaviorSubject, filter } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RequestActionReportService {
  private printReportSubject$ = new BehaviorSubject<boolean | null>(null);
  printReport$ = this.printReportSubject$.asObservable().pipe(filter((val) => !!val));

  print() {
    this.printReportSubject$.next(true);
  }

  clear() {
    this.printReportSubject$.next(null);
  }
}
