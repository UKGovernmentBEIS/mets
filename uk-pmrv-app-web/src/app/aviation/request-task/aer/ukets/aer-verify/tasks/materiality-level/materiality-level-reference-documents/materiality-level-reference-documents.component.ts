import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { MaterialityLevelFormProvider } from '../materiality-level-form.provider';
import { AccreditationReferenceDocumentNamePipe } from '../pipes/accreditation-reference-document-name.pipe';
import {
  aviationAerUkeEtsForAccreditedVerifiers,
  aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders,
  aviationAerUkeEtsRulesUkEts,
  aviationAerUkeEtsRulesUkEtsOther,
} from '../reference-documents-type';

@Component({
  selector: 'app-materiality-level-reference-documents',
  templateUrl: './materiality-level-reference-documents.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, AccreditationReferenceDocumentNamePipe],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MaterialityLevelReferenceDocumentsComponent {
  protected form = this.formProvider.accreditationReferenceDocumentTypesGroup;

  protected aviationAerUkeEtsForAccreditedVerifiers = aviationAerUkeEtsForAccreditedVerifiers;

  protected aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders =
    aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders;

  protected aviationAerUkeEtsRulesUkEts = aviationAerUkeEtsRulesUkEts;

  protected aviationAerUkeEtsRulesUkEtsOther = aviationAerUkeEtsRulesUkEtsOther;

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: MaterialityLevelFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    const accreditationReferenceDocumentTypes = [
      ...(this.form.value.aviationAerUkeEtsForAccreditedVerifiers ?? []),
      ...(this.form.value.aviationAerUkeEtsForAccreditedVerifiersAndFinancialAssuranceProviders ?? []),
      ...(this.form.value.aviationAerUkeEtsRulesUkEts ?? []),
      ...(this.form.value.aviationAerUkeEtsRulesUkEtsOther ?? []),
    ];

    const form: any = {
      materialityDetails: this.formProvider.getFormValue().materialityDetails,
      accreditationReferenceDocumentTypes,
    };

    if (this.form.value?.aviationAerUkeEtsRulesUkEtsOther?.length) {
      form.otherReference = this.form.value.otherReference;
    }

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ materialityLevel: form }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setMaterialityLevel(form);

        this.formProvider.setFormValue(form);

        this.router.navigate(['..', 'summary'], { relativeTo: this.route });
      });
  }
}
