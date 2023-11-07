import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';
import { cloneDeep } from 'lodash-es';

import { aerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-emissions-reduction-claim-declaration',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './emissions-reduction-claim-declaration.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimDeclarationComponent implements OnInit, OnDestroy {
  form = new FormGroup({
    noDoubleCountingDeclarationFile: this.formProvider.declarationFileCtlr,
  });

  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: aerEmissionsReductionClaimFormProvider,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['../../../..', 'file-download', 'attachment', uuid];
  }

  onSubmit() {
    const value = this.form.value;
    const parentFormValue = cloneDeep(this.formProvider.getFormValue());
    parentFormValue.safDetails.noDoubleCountingDeclarationFile = value.noDoubleCountingDeclarationFile?.uuid;
    this.store.aerDelegate
      .saveAer(
        {
          saf: {
            ...parentFormValue,
          },
        },
        'in progress',
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(parentFormValue);
        if (value.noDoubleCountingDeclarationFile) {
          this.store.aerDelegate.addAerAttachment({
            [value.noDoubleCountingDeclarationFile.uuid]: value.noDoubleCountingDeclarationFile.file.name,
          });
        }

        this.router.navigate(['../', 'summary'], { relativeTo: this.route });
      });
  }
}
