import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

import { BehaviorSubject, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '../../core/services/destroy-subject.service';

@Component({
  selector: 'app-verification-sent',
  standalone: false,
  templateUrl: './verification-sent.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationSentComponent implements OnInit {
  @Input() email: string;

  title$ = new BehaviorSubject<string>('Check your email');

  constructor(
    private readonly titleService: Title,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit() {
    this.title$
      .pipe(
        takeUntil(this.destroy$),
        tap((title) => this.titleService.setTitle(title)),
      )
      .subscribe();
  }
}
