import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { AlrDeterminationSummaryTemplateComponent } from '@shared/components/alr/determination-summary-template/determination-summary-template.component';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  AddressDTO,
  BusinessOrganisationDetails,
  IndividualOrganisationDetails,
  InstallationAccountRegistryIntegrationRequestActionPayload,
  InstallationAccountUpdatedRegistryIntegrationRequestActionPayload,
  PermitIssuanceOrganizationDetails,
  RegistryIntegrationAccountCreateActivePermit,
  RegistryIntegrationAccountUpdateActivePermit,
  RequestActionDTO,
} from 'pmrv-api';

import { RegistryActionService } from '../core/registry.service';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  activePermit: RegistryIntegrationAccountCreateActivePermit | RegistryIntegrationAccountUpdateActivePermit;
  organizationDetails: Partial<
    PermitIssuanceOrganizationDetails & IndividualOrganisationDetails & BusinessOrganisationDetails
  >;
  address: AddressDTO;
}

@Component({
  selector: 'app-information-sent',
  standalone: true,
  imports: [ActionSharedModule, AlrDeterminationSummaryTemplateComponent, NgIf, PipesModule, SharedModule],
  templateUrl: './information-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSentToRegistryComponent {
  payload = this.registryActionService.payload as Signal<
    | InstallationAccountRegistryIntegrationRequestActionPayload
    | InstallationAccountUpdatedRegistryIntegrationRequestActionPayload
  >;
  private readonly requestActionType = this.registryActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const activePermit = payload.activePermit;
    const organizationDetails = payload.organizationDetails;

    return {
      header: 'Information sent to Registry by system',
      expectedActionType: [this.requestActionType()],
      activePermit,
      organizationDetails,
      address:
        (organizationDetails as BusinessOrganisationDetails)?.registeredAddress ??
        (organizationDetails as IndividualOrganisationDetails)?.operatorAddress,
    };
  });

  constructor(private readonly registryActionService: RegistryActionService) {}
}
