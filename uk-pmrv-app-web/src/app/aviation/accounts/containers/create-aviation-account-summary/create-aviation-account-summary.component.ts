import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { AviationAccountsStore, selectNewAccount } from '../../store';

@Component({
  selector: 'app-create-aviation-account-summary',
  templateUrl: './create-aviation-account-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CreateAviationAccountSummaryComponent {
  info$ = this.store.pipe(selectNewAccount, takeUntil(this.destroy$));

  constructor(
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  onSubmit(): void {
    this.store.createAccount().subscribe(() => {
      this.store.setIsSubmitted(true);
      this.router.navigate(['../', 'success'], { relativeTo: this.route });
    });
  }
}
