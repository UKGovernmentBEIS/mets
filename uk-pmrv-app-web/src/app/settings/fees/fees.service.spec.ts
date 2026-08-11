import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store';

import { FeeRowDTO, SettingsService } from 'pmrv-api';

import { FeesService } from './fees.service';

describe('FeesService', () => {
  let service: FeesService;
  let authStore: AuthStore;
  let getFees: jest.Mock;

  const dto = (overrides: Partial<FeeRowDTO>): FeeRowDTO => ({ requestType: 'PERMIT_SURRENDER', ...overrides });

  beforeEach(() => {
    getFees = jest.fn();

    TestBed.configureTestingModule({
      providers: [{ provide: SettingsService, useValue: { getFees } }],
    });

    service = TestBed.inject(FeesService);
    authStore = TestBed.inject(AuthStore);
    authStore.setCurrentDomain('INSTALLATION');
  });

  it('requests fees for the current account domain', (done) => {
    getFees.mockReturnValue(of([]));

    service.getFees().subscribe(() => {
      expect(getFees).toHaveBeenCalledWith('INSTALLATION');
      done();
    });
  });

  it('maps requestType/feeType to a workflow label and parses the amount', (done) => {
    getFees.mockReturnValue(of([dto({ requestType: 'PERMIT_VARIATION', amount: '1125' })]));

    service.getFees().subscribe((rows) => {
      expect(rows).toEqual([
        {
          key: 'PERMIT_VARIATION',
          workflow: 'Permit variation (GHGE and HSE)',
          currentAmount: 1125,
          scheduledChange: null,
        },
      ]);
      done();
    });
  });

  it('disambiguates PERMIT_ISSUANCE rows by feeType', (done) => {
    getFees.mockReturnValue(of([dto({ requestType: 'PERMIT_ISSUANCE', feeType: 'CAT_A', amount: '5622' })]));

    service.getFees().subscribe((rows) => {
      expect(rows[0].workflow).toBe('Permit application (GHGE category A)');
      done();
    });
  });

  it('drops rows whose requestType/feeType is not shown on the Fees page', (done) => {
    getFees.mockReturnValue(of([dto({ requestType: 'WASTE_QDR', amount: '100' })]));

    service.getFees().subscribe((rows) => {
      expect(rows).toEqual([]);
      done();
    });
  });

  it('includes the scheduled change when scheduledAmount and scheduledDate are present', (done) => {
    getFees.mockReturnValue(
      of([dto({ requestType: 'NER', amount: '7496', scheduledAmount: '8500', scheduledDate: '2026-07-21' })]),
    );

    service.getFees().subscribe((rows) => {
      expect(rows[0].scheduledChange).toEqual({ amount: 8500, date: '2026-07-21' });
      done();
    });
  });

  it('sorts rows alphabetically by workflow label', (done) => {
    getFees.mockReturnValue(
      of([dto({ requestType: 'PERMIT_VARIATION', amount: '1' }), dto({ requestType: 'NER', amount: '1' })]),
    );

    service.getFees().subscribe((rows) => {
      expect(rows.map((row) => row.workflow)).toEqual([
        'New entrant reserve (GHGE)',
        'Permit variation (GHGE and HSE)',
      ]);
      done();
    });
  });

  it('requests fees for the current account domain when it is AVIATION', (done) => {
    authStore.setCurrentDomain('AVIATION');
    getFees.mockReturnValue(of([]));

    service.getFees().subscribe(() => {
      expect(getFees).toHaveBeenCalledWith('AVIATION');
      done();
    });
  });

  it.each([
    ['EMP_ISSUANCE_UKETS', 'EMP application (UK ETS)'],
    ['EMP_ISSUANCE_CORSIA', 'EMP application (CORSIA)'],
    ['EMP_VARIATION_UKETS', 'EMP variation (UK ETS)'],
    ['EMP_VARIATION_CORSIA', 'EMP variation (CORSIA)'],
  ] as [FeeRowDTO['requestType'], string][])(
    'maps aviation requestType %s to workflow label %s',
    (requestType, workflow) => {
      authStore.setCurrentDomain('AVIATION');

      return new Promise<void>((resolve) => {
        getFees.mockReturnValue(of([dto({ requestType, amount: '2000' })]));

        service.getFees().subscribe((rows) => {
          expect(rows[0].workflow).toBe(workflow);
          resolve();
        });
      });
    },
  );

  it('drops aviation rows whose requestType is not shown on the Fees page', (done) => {
    authStore.setCurrentDomain('AVIATION');
    getFees.mockReturnValue(of([dto({ requestType: 'AVIATION_NON_COMPLIANCE', amount: '100' })]));

    service.getFees().subscribe((rows) => {
      expect(rows).toEqual([]);
      done();
    });
  });
});
