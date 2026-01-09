import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  EmpIssuanceIndividualCompanyDetails,
  EmpIssuanceLimitedCompanyDetails,
  EmpIssuanceOperatorDetails,
  EmpIssuanceOrganisationDetails,
  EmpIssuancePartnershipDetails,
  EmpIssuanceRegistryIntegrationRequestActionPayload,
  LocationDTO,
  LocationOffShoreDTO,
  LocationOnShoreDTO,
  LocationOnShoreStateDTO,
  RequestActionDTO,
} from 'pmrv-api';

import { OperatorDetailsLegalStatusTypePipe } from '../../../shared/pipes/operator-details-legal-status-type.pipe';
import { RegistryActionService } from '../core/registry.service';

interface ViewModel {
  header: string;
  expectedActionType: Array<RequestActionDTO['type']>;
  operatorDetails: EmpIssuanceOperatorDetails;
  organizationDetails: Partial<
    EmpIssuanceOrganisationDetails &
      EmpIssuanceIndividualCompanyDetails &
      EmpIssuanceLimitedCompanyDetails &
      EmpIssuancePartnershipDetails
  >;
  address: LocationDTO | LocationOffShoreDTO | LocationOnShoreDTO | LocationOnShoreStateDTO;
}

@Component({
  selector: 'app-information-sent',
  standalone: true,
  imports: [ActionSharedModule, NgIf, PipesModule, SharedModule, OperatorDetailsLegalStatusTypePipe],
  templateUrl: './information-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSentToRegistryComponent {
  payload = this.registryActionService.payload as Signal<EmpIssuanceRegistryIntegrationRequestActionPayload>;
  private readonly requestActionType = this.registryActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const operatorDetails = payload.operatorDetails;
    const organizationDetails = payload.organisationDetails;

    return {
      header: 'Information sent to registry by system',
      expectedActionType: [this.requestActionType()],
      operatorDetails,
      organizationDetails,
      address:
        (organizationDetails as EmpIssuanceLimitedCompanyDetails)?.registeredAddress ??
        (organizationDetails as EmpIssuanceIndividualCompanyDetails)?.address,
    };
  });

  constructor(private readonly registryActionService: RegistryActionService) {}
}
