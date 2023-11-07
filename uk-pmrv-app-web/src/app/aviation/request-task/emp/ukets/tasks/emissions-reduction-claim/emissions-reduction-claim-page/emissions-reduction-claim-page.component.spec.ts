import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub } from '@testing';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimPageComponent } from './emissions-reduction-claim-page.component';

describe('EmissionsReductionClaimPageComponent', () => {
  let fixture: ComponentFixture<EmissionsReductionClaimPageComponent>;
  const user = userEvent.setup();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmissionsReductionClaimPageComponent],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: EmissionsReductionClaimFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    TestBed.inject(RequestTaskStore).setRequestTaskItem({
      requestTask: { type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT' },
    });

    fixture = TestBed.createComponent(EmissionsReductionClaimPageComponent);
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Emissions reduction claim')).toBeInTheDocument();
  });

  it('should show error when yes or no has not been selected', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();
    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Select yes or no/)).toHaveLength(2);
  });
});
