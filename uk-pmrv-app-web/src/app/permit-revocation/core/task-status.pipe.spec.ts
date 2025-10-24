import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { mockTaskState } from '@permit-revocation/testing/mock-state';
import { addDays, format, startOfDay } from 'date-fns';

import { InstallationAccountViewService, TasksService } from 'pmrv-api';

import { mockClass, MockType } from '../../../testing';
import { mockedAccountPermit } from '../../accounts/testing/mock-data';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: PermitRevocationStore;
  let accountViewService: MockType<InstallationAccountViewService>;

  const effectiveDate = (formatStr: string): string => format(startOfDay(addDays(new Date(), 28)), formatStr);

  const today = (daysAdded?: number): string => {
    return format(addDays(new Date(), daysAdded ?? 0), 'yyyy-MM-dd');
  };

  const validPermitRevocation = {
    reason: 'Because i have to',
    activitiesStopped: true,
    stoppedDate: today(),
    effectiveDate: effectiveDate('yyyy-MM-dd'),
    surrenderRequired: true,
    surrenderDate: today(),
    annualEmissionsReportRequired: true,
    annualEmissionsReportDate: today(),
    feeCharged: false,
    alrRequired: true,
    alrReportDate: today(),
  };

  beforeEach(async () => {
    accountViewService = mockClass(InstallationAccountViewService);
    accountViewService.getInstallationAccountById.mockReturnValue(of(mockedAccountPermit));

    await TestBed.configureTestingModule({
      imports: [],
      declarations: [TaskStatusPipe],
      providers: [
        provideRouter([]),
        { provide: TasksService, useValue: mockClass(TasksService) },
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: InstallationAccountViewService, useValue: accountViewService },
      ],
    });

    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => (pipe = new TaskStatusPipe(store)));

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve apply status', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: undefined,
    });

    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('not started');
  });

  it('should resolve apply status in progress', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...mockTaskState.permitRevocation,
        reason: 'Because i have to',
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('in progress');
  });

  it('should resolve apply status completed', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: validPermitRevocation,
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('complete');
  });

  it('resolve status in needs review if stoppedDate is tomorrow or surrenderDate, annualEmissionsReportDate is yesterday', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        stoppedDate: today(1),
      },
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');

    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        annualEmissionsReportDate: today(-1),
      },
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');

    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        surrenderDate: today(-1),
      },
      sectionsCompleted: { REVOCATION_APPLY: true },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });

  it('should resolve status in needs review if effective date of the permit revocation notice is not 28 days after today', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        effectiveDate: format(addDays(new Date(), 27), 'yyyy-MM-dd'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });

  it('should resolve status in needs review if fee date is the same date as effective date', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        effectiveDate: effectiveDate('yyyy-MM-dd'),
        feeDate: effectiveDate('yyyy-MM-dd'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });

  it('should resolve status in needs review if effective date of the permit revocation notice is not 28 days after today and fee date is the same date as effective date', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        feeDate: format(addDays(new Date(), 27), 'yyyy-MM-dd'),
        effectiveDate: format(addDays(new Date(), 27), 'yyyy-MM-dd'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });
});
