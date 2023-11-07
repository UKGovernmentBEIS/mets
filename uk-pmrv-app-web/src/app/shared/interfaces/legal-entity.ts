import { AddressDTO, HoldingCompanyAddressDTO, LegalEntityDTO } from 'pmrv-api';

export interface LegalEntity {
  selectGroup: LegalEntitySelect;
  detailsGroup: LegalEntityDetails;
}

export interface LegalEntitySelect {
  id?: number;
  isNew?: boolean;
}

export interface LegalEntityDetails {
  type: LegalEntityDTO['type'];
  name: string;
  referenceNumber: string;
  noReferenceNumberReason?: string;
  address: AddressDTO;
  belongsToHoldingCompany: boolean;
  holdingCompanyGroup?: HoldingCompany;
}

export const legalEntityTypeMap: Record<LegalEntityDTO['type'], string> = {
  LIMITED_COMPANY: 'Limited company',
  PARTNERSHIP: 'Partnership',
  SOLE_TRADER: 'Sole trader',
  OTHER: 'None of the above',
};

export interface HoldingCompany {
  name: string;
  registrationNumber?: string;
  address: HoldingCompanyAddressDTO;
}
