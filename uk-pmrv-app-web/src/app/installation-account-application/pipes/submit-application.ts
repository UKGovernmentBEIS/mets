import { cloneDeep } from 'lodash-es';

import {
  InstallationAccountOpeningSubmitApplicationCreateActionPayload,
  LegalEntityDTO,
  LocationOffShoreDTO,
  LocationOnShoreDTO,
} from 'pmrv-api';

import { Installation, OnshoreInstallation } from '../installation-type/installation';
import {
  ApplicationSectionType,
  ApplicationTypeSection,
  CommencementSection,
  EtsSchemeSection,
  InstallationSection,
  LegalEntitySection,
  Section,
} from '../store/installation-account-application.state';

export interface SubmitApplicationActionPayload {
  payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD';
  accountType: 'INSTALLATION';
  commencementDate: Date;
  competentAuthority: InstallationAccountOpeningSubmitApplicationCreateActionPayload['competentAuthority'];
  emissionTradingScheme: InstallationAccountOpeningSubmitApplicationCreateActionPayload['emissionTradingScheme'];
  applicationType?: InstallationAccountOpeningSubmitApplicationCreateActionPayload['applicationType'];
  legalEntity: LegalEntityDTO;
  name: string;
  siteName: string;
  location: LocationOffShoreDTO | LocationOnShoreDTO;
}

export interface SubmitApplicationDecisionActionPayload {
  payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD';
  decision: 'ACCEPTED' | 'REJECTED';
  reason?: string;
}

export function mapApplication(sections: Section[], payloadType: string): any {
  const installation = (
    sections.find((section) => section.type === ApplicationSectionType.installation) as InstallationSection
  ).value;
  const commencementDate = (
    sections.find((section) => section.type === ApplicationSectionType.commencement) as CommencementSection
  ).value.commencementDate;
  const legalEntity = (
    sections.find((section) => section.type === ApplicationSectionType.legalEntity) as LegalEntitySection
  ).value;
  const emissionTradingScheme = (
    sections.find((section) => section.type === ApplicationSectionType.etsScheme) as EtsSchemeSection
  ).value.etsSchemeType;
  const applicationType = (
    sections.find((section) => section.type === ApplicationSectionType.applicationType) as ApplicationTypeSection
  ).value.applicationType;

  const legalEntityDetails = cloneDeep(legalEntity.detailsGroup);
  delete legalEntityDetails.belongsToHoldingCompany;
  delete legalEntityDetails.holdingCompanyGroup;

  return {
    payloadType: payloadType,
    accountType: 'INSTALLATION',
    commencementDate,
    competentAuthority: isOnShoreInstallation(installation) ? installation.locationGroup.location : 'OPRED',
    emissionTradingScheme,
    applicationType,
    legalEntity: {
      id: legalEntity.selectGroup.id,
      ...legalEntityDetails,
      holdingCompany: legalEntity.detailsGroup.belongsToHoldingCompany
        ? {
            ...legalEntity.detailsGroup.holdingCompanyGroup,
          }
        : null,
    },
    name: isOnShoreInstallation(installation) ? installation.onshoreGroup.name : installation.offshoreGroup.name,
    siteName: isOnShoreInstallation(installation)
      ? installation.onshoreGroup.siteName
      : installation.offshoreGroup.siteName,
    location: {
      type: installation.installationTypeGroup.type,
      ...(isOnShoreInstallation(installation)
        ? {
            address: installation.onshoreGroup.address,
            gridReference: installation.onshoreGroup.gridReference,
          }
        : {
            latitude: installation.offshoreGroup.latitude,
            longitude: installation.offshoreGroup.longitude,
          }),
    },
  };
}

export function mapApplicationDecision(
  decision: 'ACCEPTED' | 'REJECTED',
  reason?: string,
): SubmitApplicationDecisionActionPayload {
  return {
    payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD',
    decision,
    reason,
  };
}

export function isOnShoreInstallation(installation: Installation): installation is OnshoreInstallation {
  return installation.installationTypeGroup.type === 'ONSHORE';
}
