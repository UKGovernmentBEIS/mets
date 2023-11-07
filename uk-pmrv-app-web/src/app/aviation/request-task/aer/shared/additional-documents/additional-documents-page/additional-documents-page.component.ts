import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AdditionalDocumentsFormModel } from '../additional-documents-form.provider';

interface ViewModel {
  form: FormGroup<AdditionalDocumentsFormModel>;
  submitHidden: boolean;
  downloadUrl: string;
}

@Component({
  selector: 'app-additional-documents-page',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './additional-documents-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsPageComponent implements AfterViewInit, OnDestroy {
  private store = inject(RequestTaskStore);
  private form = inject<FormGroup>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private backLinkService = inject(BackLinkService);

  vm$: Observable<ViewModel> = this.store.pipe(requestTaskQuery.selectIsEditable).pipe(
    map((isEditable) => {
      return {
        form: this.form,
        downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  ngAfterViewInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    const value = this.form.value;
    this.store.aerDelegate
      .saveAer(
        { additionalDocuments: { exist: !!value.exist, documents: value?.documents?.map((doc) => doc.uuid) } },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        value?.documents?.forEach((doc) => {
          this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
        });
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
