import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { UserEvent } from '@testing-library/user-event/setup/setup';
import produce from 'immer';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../../../store';
import { EmpUkEtsStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';
import { AbbreviationsPageComponent } from './abbreviations-page.component';

describe('AbbreviationsPageComponent', () => {
  let store: RequestTaskStore;
  let fixture: ComponentFixture<AbbreviationsPageComponent>;
  let user: UserEvent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbbreviationsPageComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: AbbreviationsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    user = userEvent.setup();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.requestTaskItem = {
          requestTask: {
            type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
            payload: EmpUkEtsStoreDelegate.INITIAL_STATE as EmpRequestTaskPayloadUkEts,
          },
        };
      }),
    );

    fixture = TestBed.createComponent(AbbreviationsPageComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it(`should display correct page heading`, () => {
    expect(screen.getByText('Abbreviations and definitions')).toBeInTheDocument();
  });

  it('should show form errors', async () => {
    await user.click(screen.getByRole('radio', { name: /Yes/ }));
    fixture.detectChanges();
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();

    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Enter an abbreviation or term used/)).toHaveLength(2);
    expect(screen.getAllByText(/Enter a definition/)).toHaveLength(2);
  });

  it('should show error when yes or no has not been selected', async () => {
    await user.click(screen.getByRole('button', { name: /Continue/ }));
    fixture.detectChanges();
    expect(screen.getByRole('alert')).toBeVisible();
    expect(screen.getAllByText(/Select yes or no/)).toHaveLength(2);
  });
});
