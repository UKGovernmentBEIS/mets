import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';

import { EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { SubmissionPageComponent } from '../submission-page';

describe('SubmissionSuccessComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();

    TestBed.inject(RequestTaskStore).setRequestTaskItem({
      requestTask: {
        id: 1,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
        payload: <EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload>{
          payloadType: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD',
          empSectionsCompleted: {},
        },
      },
    });
    const fixture = await TestBed.createComponent(SubmissionPageComponent);
    fixture.detectChanges();
    return fixture;
  }

  it('should create', async () => {
    const fixture = await setup();
    expect(fixture.componentInstance).toBeTruthy();
  });
});
