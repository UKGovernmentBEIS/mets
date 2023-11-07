import { LegalEntity } from '@shared/interfaces/legal-entity';

import { LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import { ApplicationType } from '../application-type/application-type';
import { ResponsibilityOption } from '../confirm-responsibility/responsibility';
import { EtsScheme } from '../ets-scheme/ets-scheme';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

export function updateState(store: InstallationAccountApplicationStore, request: any, taskId?: number): void {
  const payload = request.payload;
  store.setState({ ...store.getState(), isReviewed: true, taskId, requestActionCreationDate: request.creationDate });
  store.updateTask(
    ApplicationSectionType.responsibility,
    {
      responsibility: [
        ResponsibilityOption.control,
        ResponsibilityOption.effectivePermit,
        ResponsibilityOption.management,
      ],
    },
    'complete',
  );
  store.updateTask(
    ApplicationSectionType.installation,
    {
      installationTypeGroup: { type: payload.location.type },
      locationGroup: {
        location: payload.competentAuthority === 'OPRED' ? null : payload.competentAuthority,
      },
      ...(isOnshore(payload.location)
        ? {
            onshoreGroup: {
              name: payload.name,
              siteName: payload.siteName,
              address: payload.location.address,
              gridReference: payload.location.gridReference,
            },
          }
        : {
            offshoreGroup: {
              name: payload.name,
              siteName: payload.siteName,
              latitude: payload.location.latitude,
              longitude: payload.location.longitude,
            },
          }),
    },
    'complete',
  );

  const belongsToHoldingCompany = !!payload.legalEntity.holdingCompany;
  let legalEntityState: LegalEntity = {
    selectGroup: { id: payload.legalEntity.id ?? null, isNew: !payload.legalEntity.id },
    detailsGroup: {
      name: payload.legalEntity.name,
      address: payload.legalEntity.address,
      noReferenceNumberReason: payload.legalEntity.noReferenceNumberReason ?? null,
      referenceNumber: payload.legalEntity.referenceNumber,
      type: payload.legalEntity.type,
      belongsToHoldingCompany,
    },
  };
  if (belongsToHoldingCompany) {
    legalEntityState = {
      ...legalEntityState,
      detailsGroup: {
        ...legalEntityState.detailsGroup,
        holdingCompanyGroup: {
          name: payload.legalEntity.holdingCompany.name,
          registrationNumber: payload.legalEntity.holdingCompany.registrationNumber ?? null,
          address: payload.legalEntity.holdingCompany.address ?? null,
        },
      },
    };
  }
  store.updateTask(ApplicationSectionType.legalEntity, legalEntityState, 'complete');

  store.updateTask(
    ApplicationSectionType.etsScheme,
    { etsSchemeType: payload.emissionTradingScheme as EtsScheme['etsSchemeType'] },
    'complete',
  );

  store.updateTask(
    ApplicationSectionType.commencement,
    { commencementDate: new Date(payload.commencementDate) },
    'complete',
  );

  store.updateTask(
    ApplicationSectionType.applicationType,
    { applicationType: payload.applicationType as ApplicationType['applicationType'] },
    'complete',
  );
}

function isOnshore(location: LocationOnShoreDTO | LocationOffShoreDTO): location is LocationOnShoreDTO {
  return location.type === 'ONSHORE';
}
