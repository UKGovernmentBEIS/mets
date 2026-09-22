import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { AviationAccountsService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';
import { AviationAccountFormProvider } from './aviation-account-form.provider';

describe('AviationAccountFormProvider', () => {
  const accountsService = {} as AviationAccountsService;

  function createProvider(emissionTradingScheme: 'CORSIA' | 'UK_ETS_AVIATION', commencementDate: string) {
    const store = {
      getState: () => ({
        currentAccount: {
          account: {
            aviationAccount: {
              emissionTradingScheme,
              commencementDate,
            },
          },
        },
      }),
    } as AviationAccountsStore;

    return new AviationAccountFormProvider(new FormBuilder(), accountsService, store);
  }

  describe('create mode (account open)', () => {
    function buildCommencementDateGroup(provider: AviationAccountFormProvider, emissionTradingScheme: string) {
      return new FormGroup({
        emissionTradingScheme: new FormControl(emissionTradingScheme),
        commencementDate: provider.getCommencementDateFormControl(false),
      });
    }

    it('rejects a CORSIA date before 2019 with the CORSIA message', () => {
      const provider = createProvider('CORSIA', '2023-01-01');
      const group = buildCommencementDateGroup(provider, 'CORSIA');

      group.get('commencementDate').setValue('2018-06-15' as any);

      expect(group.get('commencementDate').errors.invalidCommencementDate).toEqual(
        'The year must be the same as or after 2019 and it cannot be later than the current year',
      );
    });

    it('accepts a CORSIA date on or after 2019', () => {
      const provider = createProvider('CORSIA', '2023-01-01');
      const group = buildCommencementDateGroup(provider, 'CORSIA');

      group.get('commencementDate').setValue('2019-06-15' as any);

      expect(group.get('commencementDate').errors).toBeNull();
    });

    it('rejects a UK ETS date before 2021 with the UK ETS message', () => {
      const provider = createProvider('UK_ETS_AVIATION', '2023-01-01');
      const group = buildCommencementDateGroup(provider, 'UK_ETS_AVIATION');

      group.get('commencementDate').setValue('2020-06-15' as any);

      expect(group.get('commencementDate').errors.invalidCommencementDate).toEqual(
        'The year must be the same as or after 2021 and it cannot be later than the current year',
      );
    });

    it('accepts a UK ETS date on or after 2021', () => {
      const provider = createProvider('UK_ETS_AVIATION', '2023-01-01');
      const group = buildCommencementDateGroup(provider, 'UK_ETS_AVIATION');

      group.get('commencementDate').setValue('2021-06-15' as any);

      expect(group.get('commencementDate').errors).toBeNull();
    });
  });

  describe('edit mode (account details)', () => {
    it('rejects a CORSIA date before 2019 with the CORSIA previously-set message', () => {
      const provider = createProvider('CORSIA', '2023-01-01');
      const control = provider.getCommencementDateFormControl(true);

      control.setValue('2018-06-15' as any);

      expect(control.errors.invalidCommencementDate).toEqual(
        'The year must be the same as or after 2019 and it cannot be later than previously set',
      );
    });

    it('rejects a UK ETS date before 2021 with the UK ETS previously-set message', () => {
      const provider = createProvider('UK_ETS_AVIATION', '2023-01-01');
      const control = provider.getCommencementDateFormControl(true);

      control.setValue('2020-06-15' as any);

      expect(control.errors.invalidCommencementDate).toEqual(
        'The year must be the same as or after 2021 and it cannot be later than previously set',
      );
    });

    it('accepts a date within range for the previously set scheme', () => {
      const provider = createProvider('CORSIA', '2023-01-01');
      const control = provider.getCommencementDateFormControl(true);

      control.setValue('2022-06-01' as any);

      expect(control.errors).toBeNull();
    });
  });
});
