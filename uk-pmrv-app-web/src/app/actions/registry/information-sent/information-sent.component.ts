import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  AddressDTO,
  BusinessOrganisationDetails,
  IndividualOrganisationDetails,
  InstallationAccountRegistryIntegrationRequestActionPayload,
  InstallationAccountUpdatedRegistryIntegrationRequestActionPayload,
  InstallationReportableEmissionsRegistryIntegrationRequestActionPayload,
  NotificationRegistryIntegrationRequestActionPayload,
  RegistryIntegrationAccountCreateActivePermit,
  RegistryIntegrationAccountUpdateActivePermit,
  RegistryIntegrationOrganizationDetails,
  RegistryIntegrationReportableEmissionsActivePermit,
  RequestActionDTO,
  WithholdingOfAllowancesRegistryIntegrationRequestActionPayload,
} from 'pmrv-api';

import { RegistryActionService } from '../core/registry.service';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  activePermit:
    | RegistryIntegrationAccountCreateActivePermit
    | RegistryIntegrationAccountUpdateActivePermit
    | RegistryIntegrationReportableEmissionsActivePermit;
  organizationDetails: Partial<
    RegistryIntegrationOrganizationDetails & IndividualOrganisationDetails & BusinessOrganisationDetails
  >;
  address: AddressDTO;
  payload: WithholdingOfAllowancesRegistryIntegrationRequestActionPayload &
    NotificationRegistryIntegrationRequestActionPayload;
  actionId: number;
}

@Component({
  selector: 'app-information-sent',
  imports: [ActionSharedModule, NgIf, PipesModule, SharedModule, RouterLink],
  templateUrl: './information-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSentToRegistryComponent {
  private readonly requestActionType = this.registryActionService.requestActionType;

  payload = this.registryActionService.payload as Signal<
    | InstallationAccountRegistryIntegrationRequestActionPayload
    | InstallationAccountUpdatedRegistryIntegrationRequestActionPayload
    | InstallationReportableEmissionsRegistryIntegrationRequestActionPayload
    | WithholdingOfAllowancesRegistryIntegrationRequestActionPayload
  >;

  actionId = this.registryActionService.requestAction;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const activePermit = (payload as InstallationAccountUpdatedRegistryIntegrationRequestActionPayload).activePermit;
    const organizationDetails = (payload as InstallationAccountUpdatedRegistryIntegrationRequestActionPayload)
      .organizationDetails;
    const actionId = this.actionId().id;

    return {
      header: 'Information sent to Registry by system',
      expectedActionType: [this.requestActionType()],
      activePermit,
      organizationDetails,
      address:
        (organizationDetails as BusinessOrganisationDetails)?.registeredAddress ??
        (organizationDetails as IndividualOrganisationDetails)?.operatorAddress,
      payload,
      actionId,
    };
  });

  constructor(private readonly registryActionService: RegistryActionService) {}
}
