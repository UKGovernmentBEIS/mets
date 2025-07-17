import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { BdrModule } from '@tasks/bdr/bdr.module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import {
  BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRApplicationSubmitRequestTaskPayload,
  BDRApplicationVerificationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { BdrService } from '../services/bdr.service';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: CommonTasksStore;
  let bdrService: BdrService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BdrModule, RouterTestingModule, TaskStatusPipe],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);
    bdrService = TestBed.inject(BdrService);
    pipe = new TaskStatusPipe(bdrService);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve statuses', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'BDR_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'BDR_APPLICATION_SUBMIT_PAYLOAD',
            bdr: {},
            bdrSectionsCompleted: {},
          } as BDRApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('baseline'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('sendReport'))).resolves.toEqual('cannot start yet');
  });

  it('should resolve statuses for verifier', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'BDR_APPLICATION_VERIFICATION_SUBMIT',
          payload: {
            payloadType: 'BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
            bdr: {},
            verificationSectionsCompleted: {},
          } as BDRApplicationVerificationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('baseline'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('opinionStatement'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('overallDecision'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('sendReport'))).resolves.toEqual('cannot start yet');
  });

  it('should resolve statuses for regulator', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT',
          payload: {
            payloadType: 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
            bdr: {},
            regulatorReviewSectionsCompleted: {},
          } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('BDR'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('OPINION_STATEMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('OVERALL_DECISION'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('outcome'))).resolves.toEqual('cannot start yet');
  });
});
