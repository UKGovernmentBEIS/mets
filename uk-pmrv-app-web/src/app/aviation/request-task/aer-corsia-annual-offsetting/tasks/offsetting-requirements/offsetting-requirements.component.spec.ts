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

import { AviationAerCorsiaAnnualOffsetting } from 'pmrv-api';

import { AnnualOffsettingRequirementsFormProvider } from '../../aer-corsia-annual-offsetting-form.provider';
import { annualOffsettingMockBuild } from '../../mocks/mock-annual-offsetting';
import { AnnualOffsettingRequirementsComponent } from './offsetting-requirements.component';

describe('OffsettingRequirementsComponent', () => {
  let component: AnnualOffsettingRequirementsComponent;
  let fixture: ComponentFixture<AnnualOffsettingRequirementsComponent>;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let user: UserEvent;

  const initiateOffsettingRequirements: AviationAerCorsiaAnnualOffsetting = {
    calculatedAnnualOffsetting: null,
    schemeYear: 2023,
    sectorGrowth: null,
    totalChapter: null,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnualOffsettingRequirementsComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AnnualOffsettingRequirementsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      annualOffsettingMockBuild({
        aviationAerCorsiaAnnualOffsetting: initiateOffsettingRequirements,
      }) as any,
    );

    fixture = TestBed.createComponent(AnnualOffsettingRequirementsComponent);
    component = fixture.componentInstance;
    component.formProvider.setFormValue(initiateOffsettingRequirements);
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
      .spyOn(store.aerCorsiaAnnualOffsetting, 'saveOffsettingRequirement')
      .mockReturnValue(of({}));

    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter total chapter 3 emissions/)).toHaveLength(2);

    await user.type(inputs[0], '4444');
    await user.type(inputs[1], '1.98');

    component.onSubmit();

    expect(saveOffsettingRequirement).toHaveBeenCalledTimes(1);
    expect(saveOffsettingRequirement).toHaveBeenCalledWith(component.form.value, 'in progress');

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  });
});
