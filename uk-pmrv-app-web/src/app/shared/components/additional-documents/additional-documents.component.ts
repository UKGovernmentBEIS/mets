import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { map, Observable, startWith, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { WizardStepComponent } from '../../wizard/wizard-step.component';

@Component({
  selector: 'app-additional-documents-shared',
  templateUrl: './additional-documents.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, PendingRequestService],
})
export class AdditionalDocumentsSharedComponent implements OnInit, PendingRequest {
  @Input() submitText = 'Confirm and complete';
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() downloadUrl: string;

  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  isFileUploaded$: Observable<boolean>;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.isFileUploaded$ = this.form.get('documents').valueChanges.pipe(
      startWith(this.form.get('documents').value),
      map((value) => value?.length > 0),
    );

    this.form
      .get('exist')
      .valueChanges.pipe(takeUntil(this.destroy$), startWith(this.form.value.exist))
      .subscribe((exist) => {
        if (exist) {
          this.form.get('documents').enable();
        } else {
          this.form.get('documents').disable();
        }
      });
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }

  getDownloadUrl() {
    return this.downloadUrl;
  }
}
