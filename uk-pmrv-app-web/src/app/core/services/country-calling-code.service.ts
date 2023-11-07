import { Injectable } from '@angular/core';

import { PhoneNumberUtil } from 'google-libphonenumber';

@Injectable({ providedIn: 'root' })
export class CountryCallingCodeService {
  getCountryCallingCode(countryCode: string): number {
    return this.handleMissingCodes(countryCode) ?? PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode);
  }

  private handleMissingCodes(countryCode: string): number {
    switch (countryCode) {
      case 'GB-ENG':
      case 'GB-NIR':
      case 'GB-SCT':
      case 'GB-WLS': {
        return 44;
      }
      default: {
        return null;
      }
    }
  }
}
