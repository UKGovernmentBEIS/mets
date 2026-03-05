import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';

import { AviationAccountFormComponent } from '@aviation/accounts/components';
import { AviationAccountFormModel } from '@aviation/accounts/services';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { AviationAccountCreationDTO } from 'pmrv-api';

describe('AccountFormComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  class Page extends BasePage<TestComponent> {
    get allInputs() {
      return this.queryAll<HTMLInputElement>('input').map((el) => el.name);
    }
  }

  @Component({
    template: `
      <form [formGroup]="formGroup">
        <app-aviation-account-form></app-aviation-account-form>
      </form>
    `,
  })
  class TestComponent {
    formGroup = new FormGroup<AviationAccountFormModel>({
      name: new FormControl<string | null>(null),
      emissionTradingScheme: new FormControl<AviationAccountCreationDTO['emissionTradingScheme'] | null>(null),
      sopId: new FormControl<number | null>(null),
      crcoCode: new FormControl<string | null>(null),
      commencementDate: new FormControl<string | null>(null),
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [AviationAccountFormComponent, TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show all applicable inputs', () => {
    expect(page.allInputs).toHaveLength(8);
  });
});
