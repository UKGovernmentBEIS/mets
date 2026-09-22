import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ActionSharedModule } from '@actions/shared/action-shared-module';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { PipesModule } from '@shared/pipes/pipes.module';
import { SharedModule } from '@shared/shared.module';

import {
  AviationIndividualCompanyDetails,
  AviationLimitedCompanyDetails,
  AviationOrganisationDetails,
  AviationPartnershipDetails,
  AviationRegistryIntegrationOperatorDetails,
  AviationReportableEmissionsRegistryIntegrationRequestActionPayload,
  AviationUpdateOperatorDetails,
  EmpIssuanceRegistryIntegrationRequestActionPayload,
  EmpVariationRegistryIntegrationRequestActionPayload,
  LocationDTO,
  LocationOffShoreDTO,
  LocationOnShoreDTO,
  LocationOnShoreStateDTO,
  NotificationRegistryIntegrationNoFileRequestActionPayload,
  NotificationRegistryIntegrationRequestActionPayload,
  RequestActionDTO,
  RequestActionPayload,
} from 'pmrv-api';

import { OperatorDetailsLegalStatusTypePipe } from '../../../shared/pipes/operator-details-legal-status-type.pipe';
import { RegistryActionService } from '../core/registry.service';

interface ViewModel {
  expectedActionType: Array<RequestActionDTO['type']>;
  operatorDetails: Partial<
    AviationRegistryIntegrationOperatorDetails &
      AviationUpdateOperatorDetails &
      AviationReportableEmissionsRegistryIntegrationRequestActionPayload
  >;
  organizationDetails: Partial<
    AviationOrganisationDetails &
      AviationIndividualCompanyDetails &
      AviationLimitedCompanyDetails &
      AviationPartnershipDetails
  >;
  address: LocationDTO | LocationOffShoreDTO | LocationOnShoreDTO | LocationOnShoreStateDTO;
  registryId: number;
  notificationType: string;
  payload:
    | EmpIssuanceRegistryIntegrationRequestActionPayload
    | EmpVariationRegistryIntegrationRequestActionPayload
    | AviationReportableEmissionsRegistryIntegrationRequestActionPayload
    | NotificationRegistryIntegrationNoFileRequestActionPayload
    | NotificationRegistryIntegrationRequestActionPayload;
  payloadType: RequestActionPayload['payloadType'];
  actionId: number;
}

@Component({
  selector: 'app-information-sent',
  imports: [ActionSharedModule, PipesModule, SharedModule, OperatorDetailsLegalStatusTypePipe, RouterLink],
  templateUrl: './information-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InformationSentToRegistryComponent {
  payload = this.registryActionService.payload as Signal<
    | EmpIssuanceRegistryIntegrationRequestActionPayload
    | EmpVariationRegistryIntegrationRequestActionPayload
    | AviationReportableEmissionsRegistryIntegrationRequestActionPayload
    | NotificationRegistryIntegrationNoFileRequestActionPayload
    | NotificationRegistryIntegrationRequestActionPayload
  >;
  private readonly requestActionType = this.registryActionService.requestActionType;
  actionId = this.registryActionService.requestAction;
  vm: Signal<ViewModel> = computed(() => {
    const payload = this.payload();
    const expectedActionType = this.requestActionType();
    const operatorDetails =
      expectedActionType === 'AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY'
        ? (payload as AviationReportableEmissionsRegistryIntegrationRequestActionPayload)
        : expectedActionType === 'NOTIFICATION_SENT_TO_REGISTRY'
          ? (payload as NotificationRegistryIntegrationNoFileRequestActionPayload)
          : (
              payload as
                | EmpIssuanceRegistryIntegrationRequestActionPayload
                | EmpVariationRegistryIntegrationRequestActionPayload
            ).operatorDetails;
    const organizationDetails = (payload as EmpIssuanceRegistryIntegrationRequestActionPayload).organisationDetails;
    const actionId = this.actionId().id;

    return {
      expectedActionType: [expectedActionType],
      payloadType: payload?.payloadType,
      payload,
      operatorDetails,
      organizationDetails,
      address:
        (organizationDetails as AviationLimitedCompanyDetails)?.registeredAddress ??
        (organizationDetails as AviationIndividualCompanyDetails)?.address,
      registryId: (payload as NotificationRegistryIntegrationNoFileRequestActionPayload)?.registryId,
      notificationType: (
        payload as
          | NotificationRegistryIntegrationNoFileRequestActionPayload
          | NotificationRegistryIntegrationRequestActionPayload
      )?.notificationType,
      actionId,
    };
  });

  action$ = this.commonActionsStore.requestAction$;

  constructor(
    private readonly registryActionService: RegistryActionService,
    private readonly commonActionsStore: CommonActionsStore,
    readonly pendingRequest: PendingRequestService,
  ) {}
}
