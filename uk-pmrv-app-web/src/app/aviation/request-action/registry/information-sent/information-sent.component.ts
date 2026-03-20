import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  AviationReportableEmissionsOperatorDetails,
  AviationReportableEmissionsRegistryIntegrationRequestActionPayload,
  EmpIssuanceIndividualCompanyDetails,
  EmpIssuanceLimitedCompanyDetails,
  EmpIssuanceOperatorDetails,
  EmpIssuanceOrganisationDetails,
  EmpIssuancePartnershipDetails,
  EmpIssuanceRegistryIntegrationRequestActionPayload,
  EmpVariationRegistryIntegrationRequestActionPayload,
  LocationDTO,
  LocationOffShoreDTO,
  LocationOnShoreDTO,
  LocationOnShoreStateDTO,
  RequestActionDTO,
} from 'pmrv-api';

import { OperatorDetailsLegalStatusTypePipe } from '../../../shared/pipes/operator-details-legal-status-type.pipe';
import { RegistryActionService } from '../core/registry.service';

interface ViewModel {
  expectedActionType: Array<RequestActionDTO['type']>;
  operatorDetails: EmpIssuanceOperatorDetails & AviationReportableEmissionsOperatorDetails;
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
  imports: [ActionSharedModule, PipesModule, SharedModule, OperatorDetailsLegalStatusTypePipe],
  templateUrl: './information-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSentToRegistryComponent {
  payload = this.registryActionService.payload as Signal<
    | EmpIssuanceRegistryIntegrationRequestActionPayload
    | EmpVariationRegistryIntegrationRequestActionPayload
    | AviationReportableEmissionsRegistryIntegrationRequestActionPayload
  >;
  private readonly requestActionType = this.registryActionService.requestActionType;

  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const expectedActionType = this.requestActionType();
    const operatorDetails =
      expectedActionType === 'AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY'
        ? (payload as AviationReportableEmissionsRegistryIntegrationRequestActionPayload)
        : (
            payload as
              | EmpIssuanceRegistryIntegrationRequestActionPayload
              | EmpVariationRegistryIntegrationRequestActionPayload
          ).operatorDetails;
    const organizationDetails = (payload as EmpIssuanceRegistryIntegrationRequestActionPayload).organisationDetails;

    return {
      expectedActionType: [expectedActionType],
      operatorDetails,
      organizationDetails,
      address:
        (organizationDetails as EmpIssuanceLimitedCompanyDetails)?.registeredAddress ??
        (organizationDetails as EmpIssuanceIndividualCompanyDetails)?.address,
    };
  });

  action$ = this.commonActionsStore.requestAction$;

  constructor(
    private readonly registryActionService: RegistryActionService,
    private readonly commonActionsStore: CommonActionsStore,
    readonly pendingRequest: PendingRequestService,
  ) {}
}
