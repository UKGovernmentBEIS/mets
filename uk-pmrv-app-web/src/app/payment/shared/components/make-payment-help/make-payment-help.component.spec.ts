import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { MakePaymentHelpComponent } from './make-payment-help.component';

describe('MakePaymentHelpComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;
  let hostElement: HTMLElement;

  @Component({
    selector: 'app-test-wrapper-component',
    template: `<app-make-payment-help
      [competentAuthority$]="competentAuthority$"
      [requestType$]="requestType$"
      [requestTaskType$]="requestTaskType$"
      [defaultHelp]="defaultHelp"
    ></app-make-payment-help>`,
  })
  class TestWrapperComponent {
    competentAuthority$: Observable<RequestInfoDTO['competentAuthority']>;
    requestType$: Observable<RequestInfoDTO['type']>;
    requestTaskType$: Observable<RequestTaskDTO['type']>;
    defaultHelp: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestWrapperComponent, MakePaymentHelpComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display default when not variation', () => {
    component.competentAuthority$ = of('ENGLAND');
    component.requestType$ = of('PERMIT_ISSUANCE');
    component.requestTaskType$ = of('PERMIT_ISSUANCE_MAKE_PAYMENT');
    component.defaultHelp = 'default help';
    fixture.detectChanges();
    expect(hostElement.textContent).toContain('default');
  });

  it('should display default when variation and not scotland or wales', () => {
    component.competentAuthority$ = of('ENGLAND');
    component.requestType$ = of('PERMIT_VARIATION');
    component.requestTaskType$ = of('PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT');
    component.defaultHelp = 'default help';
    fixture.detectChanges();
    expect(hostElement.textContent).toContain('default');
  });

  it('should display scotland info when variation and scotland ', () => {
    component.competentAuthority$ = of('SCOTLAND');
    component.requestType$ = of('PERMIT_VARIATION');
    component.requestTaskType$ = of('PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT');
    component.defaultHelp = 'default help';
    fixture.detectChanges();
    expect(hostElement.textContent).toContain(
      'There are charges for variation, please contact SEPA to find out amount to be paid',
    );
    expect(hostElement.textContent).not.toContain('default');
  });

  it('should display wales info when variation and wales ', () => {
    component.competentAuthority$ = of('WALES');
    component.requestType$ = of('PERMIT_VARIATION');
    component.requestTaskType$ = of('PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT');
    component.defaultHelp = 'default help';
    fixture.detectChanges();
    expect(hostElement.textContent).toContain(
      'There are charges for significant variations, please contact NRW to find out the amount to be paid.',
    );
    expect(hostElement.textContent).not.toContain('default');
  });
});
