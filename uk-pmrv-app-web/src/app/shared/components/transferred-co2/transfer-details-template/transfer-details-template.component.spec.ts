import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';
import { TransferDetailsTemplateComponent } from './transfer-details-template.component';

describe('TransferDetailsTemplateComponent', () => {
  let component: TransferDetailsTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-transfer-details-template
        (formSubmit)="onSubmit($event)"
        [transfer]="transfer"
        [isEditable]="isEditable"
        [heading]="heading"
        [returnToLink]="returnToLink"
        [showBackLink]="showBackLink"
      ></app-transfer-details-template>
    `,
  })
  class TestComponent {
    transfer = {
      installationDetailsType: 'INSTALLATION_EMITTER',
      installationEmitter: {
        emitterId: '34',
        email: 'newemitter@trasys.gr',
      },
    };
    isEditable = true;
    heading = 'This is heading';
    hideSubmit = false;
    showBackLink = true;
    returnToLink = 'Inherent CO2';
    form: UntypedFormGroup;
    onSubmit: (form: FormGroup) => any | jest.SpyInstance<void, [FormGroup]>;
  }

  class Page extends BasePage<TestComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }

    get inputForm() {
      return this.query<HTMLFormElement>('form');
    }

    get installationEmitterID() {
      return this.inputForm.querySelector<HTMLInputElement>('#emitterId');
    }

    get email() {
      return this.inputForm.querySelector<HTMLInputElement>('#email');
    }

    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [KeycloakService],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(TransferDetailsTemplateComponent)).componentInstance;
    hostComponent.onSubmit = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the nested form', () => {
    expect(page.heading.textContent.trim()).toEqual('This is heading');
    expect(page.submitButton).toBeTruthy();
    expect(page.inputForm).toBeTruthy();
    expect(page.installationEmitterID.value).toEqual('34');
    expect(page.email.value).toEqual('newemitter@trasys.gr');
  });
});
