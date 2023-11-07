import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';

import { BehaviorSubject, filter, startWith, take } from 'rxjs';

import { CsvDataErrorSummaryComponent } from '@aviation/shared/components/aer/csv-data-error-summary/csv-data-error-summary.component';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-csv-data-wizard-step',
  templateUrl: './csv-data-wizard-step.component.html',
  styles: [],
  standalone: true,
  imports: [AsyncPipe, CsvDataErrorSummaryComponent, ReactiveFormsModule, SharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CsvDataWizardStepComponent implements OnInit, OnDestroy {
  @Input() showBackLink = false;
  @Input() formGroup: UntypedFormGroup;
  @Input() heading: string;
  @Input() caption: string;
  @Input() submitText = 'Continue';
  @Input() hideSubmit: boolean;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  isSummaryDisplayedSubject = new BehaviorSubject(false);

  constructor(
    private readonly backLinkService: BackLinkService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    if (this.showBackLink) {
      this.backLinkService.show();
    }
  }

  onSubmit(): void {
    this.formGroup.statusChanges
      .pipe(
        startWith(this.formGroup.status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe((status) => {
        switch (status) {
          case 'VALID':
            this.formSubmit.emit(this.formGroup);
            break;
          case 'INVALID':
            this.formGroup.markAllAsTouched();
            this.isSummaryDisplayedSubject.next(true);
            break;
        }
      });
  }

  ngOnDestroy(): void {
    this.breadcrumbService.clear();
    this.backLinkService.hide();
  }
}
