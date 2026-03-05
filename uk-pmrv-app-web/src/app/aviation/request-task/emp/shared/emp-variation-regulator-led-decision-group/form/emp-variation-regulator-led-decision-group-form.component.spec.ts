import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import { EmpVariationRegulatorLedDecisionGroupFormComponent } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/form/emp-variation-regulator-led-decision-group-form.component';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { screen } from '@testing-library/dom';
import userEvent from '@testing-library/user-event';

@Component({
  selector: 'app-mock-parent',
  template: `
    <form [formGroup]="form">
      <app-emp-variation-regulator-led-decision-group-form></app-emp-variation-regulator-led-decision-group-form>
    </form>
  `,
  standalone: true,
  imports: [ReactiveFormsModule, EmpVariationRegulatorLedDecisionGroupFormComponent],
  providers: [EmpVariationRegulatorLedDecisionGroupFormProvider],
})
class MockParentComponent {
  form = this.formProvider.form;
  constructor(readonly formProvider: EmpVariationRegulatorLedDecisionGroupFormProvider) {}
}

describe('EmpVariationRegulatorLedDecisionGroupFormComponent', () => {
  let fixture: ComponentFixture<MockParentComponent>;
  const user = userEvent.setup();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockParentComponent],
      providers: [{ provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore }],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setIsEditable(true);

    fixture = TestBed.createComponent(MockParentComponent);
    fixture.detectChanges();
  });

  it('should give the option to add a new item', async () => {
    const addButton = screen.getByRole('button', { name: 'Add item' });
    expect(addButton).toBeInTheDocument();

    await user.click(addButton);
    fixture.detectChanges();

    expect(screen.getByText(/Item 1/)).toBeInTheDocument();
    expect(addButton).not.toBeInTheDocument();

    const addAnotherButton = screen.getByRole('button', { name: 'Add another item' });
    expect(addAnotherButton).toBeInTheDocument();

    await user.click(addAnotherButton);
    fixture.detectChanges();

    expect(screen.getByText(/Item 2/)).toBeInTheDocument();
  });

  it('should give user option to remove any item', async () => {
    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Add item' }));
    fixture.detectChanges();

    await user.click(screen.getByRole('button', { name: 'Add another item' }));
    fixture.detectChanges();

    expect(screen.queryAllByRole('button', { name: /Remove/ })).toHaveLength(2);

    await user.click(screen.getAllByRole('button', { name: /Remove/ })[0]);
    fixture.detectChanges();

    await user.click(screen.getAllByRole('button', { name: /Remove/ })[0]);
    fixture.detectChanges();

    expect(screen.queryByRole('button', { name: /Remove/ })).not.toBeInTheDocument();

    expect(screen.getByRole('button', { name: 'Add item' })).toBeInTheDocument();
  });
});
