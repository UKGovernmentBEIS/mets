import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { AuthStore } from '@core/store';

import { FeeHistoryEntryDTO, FeeRowDTO, SettingsService } from 'pmrv-api';

import { FeesService } from './fees.service';

describe('FeesService', () => {
  let service: FeesService;
  let authStore: AuthStore;
  let getFees: jest.Mock;
  let updateFee: jest.Mock;
  let cancelScheduledFeeUpdate: jest.Mock;
  let getFeeHistory: jest.Mock;

  const dto = (overrides: Partial<FeeRowDTO>): FeeRowDTO => ({
    id: 1,
    requestType: 'PERMIT_SURRENDER',
    ...overrides,
  });

  const historyDto = (overrides: Partial<FeeHistoryEntryDTO>): FeeHistoryEntryDTO => ({
    createdAt: '2026-06-21T11:20:00.000Z',
    changedBy: 'Regulator England',
    actionType: 'IMMEDIATE_UPDATE',
    requestType: 'NER',
    oldAmount: '1403',
    newAmount: '1443',
    ...overrides,
  });

  beforeEach(() => {
    getFees = jest.fn();
    updateFee = jest.fn();
    cancelScheduledFeeUpdate = jest.fn();
    getFeeHistory = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        { provide: SettingsService, useValue: { getFees, updateFee, cancelScheduledFeeUpdate, getFeeHistory } },
      ],
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
          id: 1,
          requestType: 'PERMIT_VARIATION',
          feeType: undefined,
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

  describe('getFee', () => {
    it('returns the row matching the given key', (done) => {
      getFees.mockReturnValue(
        of([dto({ requestType: 'NER', amount: '1' }), dto({ requestType: 'PERMIT_VARIATION', amount: '2' })]),
      );

      service.getFee('PERMIT_VARIATION').subscribe((row) => {
        expect(row.workflow).toBe('Permit variation (GHGE and HSE)');
        done();
      });
    });

    it('returns undefined when no row matches the given key', (done) => {
      getFees.mockReturnValue(of([dto({ requestType: 'NER', amount: '1' })]));

      service.getFee('PERMIT_VARIATION').subscribe((row) => {
        expect(row).toBeUndefined();
        done();
      });
    });
  });

  describe('updateFee', () => {
    it('calls the settings service with the account domain, id, feeType and fee update payload', (done) => {
      updateFee.mockReturnValue(of({}));

      service.updateFee({ id: 42, feeType: 'FIXED', amount: 8000, effectiveDate: '2026-07-02' }).subscribe(() => {
        expect(updateFee).toHaveBeenCalledWith('INSTALLATION', 42, 'FIXED', {
          amount: '8000',
          effectiveDate: '2026-07-02',
        });
        done();
      });
    });
  });

  describe('cancelScheduledFeeUpdate', () => {
    it('calls the settings service with the account domain, id and feeType', (done) => {
      cancelScheduledFeeUpdate.mockReturnValue(of({}));

      service.cancelScheduledFeeUpdate(42, 'FIXED').subscribe(() => {
        expect(cancelScheduledFeeUpdate).toHaveBeenCalledWith('INSTALLATION', 42, 'FIXED');
        done();
      });
    });

    it('errors without calling the settings service when feeType is missing', (done) => {
      service.cancelScheduledFeeUpdate(42, undefined).subscribe({
        error: (error) => {
          expect(error.message).toBe('feeType is required to cancel a scheduled fee update');
          expect(cancelScheduledFeeUpdate).not.toHaveBeenCalled();
          done();
        },
      });
    });
  });

  describe('getFeeHistory', () => {
    it('requests a zero-indexed page for the current account domain', (done) => {
      getFeeHistory.mockReturnValue(of({ history: [], totalItems: 0 }));

      service.getFeeHistory(1, 30).subscribe(() => {
        expect(getFeeHistory).toHaveBeenCalledWith('INSTALLATION', 0, 30);
        done();
      });
    });

    it('defaults to a page size of 30', (done) => {
      getFeeHistory.mockReturnValue(of({ history: [], totalItems: 0 }));

      service.getFeeHistory(3).subscribe(() => {
        expect(getFeeHistory).toHaveBeenCalledWith('INSTALLATION', 2, 30);
        done();
      });
    });

    it('defaults total/records to 0/[] when the response omits them', (done) => {
      getFeeHistory.mockReturnValue(of({}));

      service.getFeeHistory(1).subscribe((page) => {
        expect(page).toEqual({ records: [], total: 0 });
        done();
      });
    });

    it('maps an IMMEDIATE_UPDATE entry', (done) => {
      getFeeHistory.mockReturnValue(
        of({
          history: [historyDto({ actionType: 'IMMEDIATE_UPDATE', changedBy: 'Regulator England' })],
          totalItems: 1,
        }),
      );

      service.getFeeHistory(1).subscribe((page) => {
        expect(page.records).toEqual([
          {
            createdAtDisplay: '21 Jun 2026, 12:20pm',
            changedByDisplay: 'Regulator England',
            actionType: 'IMMEDIATE_UPDATE',
            workflow: 'New entrant reserve (GHGE)',
            oldAmount: 1403,
            newAmount: 1443,
            effectiveDate: null,
          },
        ]);
        done();
      });
    });

    it('maps a SCHEDULED_UPDATE entry, keeping the effective date', (done) => {
      getFeeHistory.mockReturnValue(
        of({ history: [historyDto({ actionType: 'SCHEDULED_UPDATE', effectiveDate: '2026-07-21' })], totalItems: 1 }),
      );

      service.getFeeHistory(1).subscribe((page) => {
        expect(page.records[0].actionType).toBe('SCHEDULED_UPDATE');
        expect(page.records[0].effectiveDate).toBe('2026-07-21');
        done();
      });
    });

    it('maps a CANCEL_SCHEDULED entry', (done) => {
      getFeeHistory.mockReturnValue(
        of({ history: [historyDto({ actionType: 'CANCEL_SCHEDULED', effectiveDate: '2026-07-21' })], totalItems: 1 }),
      );

      service.getFeeHistory(1).subscribe((page) => {
        expect(page.records[0].actionType).toBe('CANCEL_SCHEDULED');
        expect(page.records[0].changedByDisplay).toBe('Regulator England');
        done();
      });
    });

    it('maps a SYSTEM_APPLIED entry to "System", overriding any changedBy value', (done) => {
      getFeeHistory.mockReturnValue(
        of({
          history: [
            historyDto({ actionType: 'SYSTEM_APPLIED', changedBy: 'Regulator England', effectiveDate: '2026-07-21' }),
          ],
          totalItems: 1,
        }),
      );

      service.getFeeHistory(1).subscribe((page) => {
        expect(page.records[0].changedByDisplay).toBe('System');
        done();
      });
    });

    it('falls back to a humanized workflow label when requestType/feeType is not on the Fees page', (done) => {
      getFeeHistory.mockReturnValue(of({ history: [historyDto({ requestType: 'BDR' })], totalItems: 1 }));

      service.getFeeHistory(1).subscribe((page) => {
        expect(page.records[0].workflow).toBe('Bdr');
        done();
      });
    });
  });
});
