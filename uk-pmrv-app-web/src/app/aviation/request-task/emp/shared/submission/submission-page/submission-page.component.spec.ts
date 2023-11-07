import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, mockClass } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { SubmissionPageComponent } from './submission-page.component';

describe('SubmissionPageComponent', () => {
  const route = new ActivatedRouteStub();
  const tasksService = mockClass(TasksService);

  tasksService.processRequestTaskAction.mockReturnValue(of({}));

  async function setup() {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    const router = TestBed.inject(Router);
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

    return {
      detectChanges: fixture.detectChanges.bind(fixture),
      component: fixture.componentInstance,
      router,
      user: userEvent.setup(),
    };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should navigate to success page upon successful submission', async () => {
    const { detectChanges, router, user } = await setup();
    const routerSpy = jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    await user.click(screen.getByRole('button', { name: /Confirm and send/ }));
    detectChanges();

    expect(routerSpy).toHaveBeenCalledWith(['success'], { relativeTo: route });
  });
});
