import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/dist/types/setup/setup';

import { ThreeYearOffsettingRequirementsFormProvider } from '../../aer-corsia-3year-period-offsetting-form.provider';
import {
  AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING,
  INITIATE_OFFSETTING_REQUIREMENTS,
  threeYearPeriodlOffsettingMockBuild,
} from '../../mocks/mock-3year-period-offsetting';
import { ThreeYearOffsettingRequirementsComponent } from './offsetting-requirements.component';

describe('ThreeYearOffsettingRequirementsComponent', () => {
  let component: ThreeYearOffsettingRequirementsComponent;
  let fixture: ComponentFixture<ThreeYearOffsettingRequirementsComponent>;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let formProvider: ThreeYearOffsettingRequirementsFormProvider;
  let user: UserEvent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThreeYearOffsettingRequirementsComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: ThreeYearOffsettingRequirementsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      threeYearPeriodlOffsettingMockBuild({ aviationAerCorsia3YearPeriodOffsetting: INITIATE_OFFSETTING_REQUIREMENTS }),
    );

    formProvider = TestBed.inject<ThreeYearOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER);

    formProvider.setFormValue(INITIATE_OFFSETTING_REQUIREMENTS);

    fixture = TestBed.createComponent(ThreeYearOffsettingRequirementsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to summary page', async () => {
    const inputs = screen.getAllByRole('textbox');
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveOffsettingRequirement = jest
      .spyOn(store.aerCorsia3YearPeriodOffsetting, 'saveOffsettingRequirement')
      .mockReturnValue(of({}));

    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter calculated annual offsetting requirement/)).toHaveLength(6);
    expect(screen.getAllByText(/Enter CEF emissions reductions/)).toHaveLength(6);
    expect(screen.getAllByText(/Select Yes or No/)).toHaveLength(2);

    await user.type(
      inputs[0],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2021'].calculatedAnnualOffsetting.toString(),
    );
    await user.type(
      inputs[1],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2021'].cefEmissionsReductions.toString(),
    );
    await user.type(
      inputs[2],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2022'].calculatedAnnualOffsetting.toString(),
    );
    await user.type(
      inputs[3],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2022'].cefEmissionsReductions.toString(),
    );
    await user.type(
      inputs[4],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2023'].calculatedAnnualOffsetting.toString(),
    );
    await user.type(
      inputs[5],
      AER_CORSIA_THREE_YEAR_PERIOD_OFFSETTING.yearlyOffsettingData['2023'].cefEmissionsReductions.toString(),
    );
    await user.click(screen.getByRole('radio', { name: /Yes/ }));

    component.onSubmit();

    expect(saveOffsettingRequirement).toHaveBeenCalledTimes(1);
    expect(saveOffsettingRequirement).toHaveBeenCalledWith(component.form.value, 'in progress');

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });
});
