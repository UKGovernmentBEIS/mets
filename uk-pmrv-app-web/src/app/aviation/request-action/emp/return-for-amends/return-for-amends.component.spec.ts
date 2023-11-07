import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub } from '@testing';
import { screen } from '@testing-library/angular';

import { EmpReturnForAmendsComponent } from './return-for-amends.component';

describe('EmpReturnForAmendsComponent', () => {
  let component: EmpReturnForAmendsComponent;
  let fixture: ComponentFixture<EmpReturnForAmendsComponent>;

  const route: ActivatedRouteStub = new ActivatedRouteStub({ taskId: '123' });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, RequestActionTaskComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    const aviationStore = TestBed.inject(RequestActionStore);

    aviationStore.setState({
      requestActionItem: {
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS',
        payload: {
          reviewAttachments: { '52d7a1e1-88ab-4fc9-9494-43739e5ba8d3': 'test.png' },
          reviewGroupDecisions: {
            EMISSIONS_REDUCTION_CLAIM: {
              type: 'OPERATOR_AMENDS_NEEDED',
              details: {
                requiredChanges: [
                  {
                    reason: 'test reason',
                  },
                ],
              },
            },
          },
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(EmpReturnForAmendsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show correct page header, section header, changes required and NO submit button', () => {
    expect(screen.getByText(/Returned to operator for changes/)).toBeTruthy();
    expect(screen.getByText(/Emissions reduction claim/)).toBeTruthy();
    expect(screen.getByText(/Changes required/)).toBeTruthy();
    expect(screen.queryByRole('button', { name: /Confirm and complete/ })).not.toBeInTheDocument();
  });
});
