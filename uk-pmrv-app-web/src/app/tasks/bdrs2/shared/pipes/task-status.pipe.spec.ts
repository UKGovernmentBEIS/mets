import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { BdrS2Module } from '@tasks/bdrs2/bdrs2.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: CommonTasksStore;
  let bdrs2Service: BdrS2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BdrS2Module, RouterTestingModule, TaskStatusPipe],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);
    bdrs2Service = TestBed.inject(BdrS2Service);
    pipe = new TaskStatusPipe(bdrs2Service);
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
          type: 'BDRS2_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'BDRS2_APPLICATION_SUBMIT_PAYLOAD',
            bdrs2: {},
            bdrs2SectionsCompleted: {},
          } as BDRS2ApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await expect(firstValueFrom(pipe.transform('baseline'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('sendReport'))).resolves.toEqual('cannot start yet');
  });
});
