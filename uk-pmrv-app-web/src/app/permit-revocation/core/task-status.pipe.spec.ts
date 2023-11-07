import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockTaskState } from '@permit-revocation/testing/mock-state';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: PermitRevocationStore;

  const effectiveDate = (format: string): string => {
    const add28Days = moment().add(28, 'd');
    const setHours = add28Days.set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    setHours.toISOString();

    const effectiveDate = moment(setHours).format(format);
    return effectiveDate;
  };

  const today = (daysAdded?: number): string => {
    return moment().add(daysAdded, 'd').format('YYYY-MM-DD');
  };

  const validPermitRevocation = {
    reason: 'Because i have to',
    activitiesStopped: true,
    stoppedDate: today(),
    effectiveDate: effectiveDate('YYYY-MM-DD'),
    surrenderRequired: true,
    surrenderDate: today(),
    annualEmissionsReportRequired: true,
    annualEmissionsReportDate: today(),
    feeCharged: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskStatusPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => (pipe = new TaskStatusPipe(store)));

  it('should create', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve apply status', async () => {
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('not started');

    store.setState({
      ...store.getState(),
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
        effectiveDate: moment().add(27, 'd').format('YYYY-MM-DD'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });

  it('should resolve status in needs review if fee date is the same date as effective date', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        effectiveDate: effectiveDate('YYYY-MM-DD'),
        feeDate: effectiveDate('YYYY-MM-DD'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });

  it('should resolve status in needs review if effective date of the permit revocation notice is not 28 days after today and fee date is the same date as effective date', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...validPermitRevocation,
        feeDate: moment().add(27, 'd').format('YYYY-MM-DD'),
        effectiveDate: moment().add(27, 'd').format('YYYY-MM-DD'),
      },
    });
    await expect(firstValueFrom(pipe.transform('REVOCATION_APPLY'))).resolves.toEqual('needs review');
  });
});
