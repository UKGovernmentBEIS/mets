import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared/shared.module';
import { ReviewSummaryComponent } from './review-summary.component';

describe('ReviewSummaryComponent', () => {
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({ template: `<app-review-summary [item]="item" [taskId]="taskId"></app-review-summary>` })
  class TestComponent {
    item = {
      legalEntity: {
        type: 'LIMITED_COMPANY',
        holdingCompany: {
          name: 'TEST_HOLDING_COMPANY',
          registrationNumber: 'TEST_REG_NUM',
          address: {
            line1: 'TEST_ADDR_L1',
            city: 'TEST_CITY',
            postcode: '99999',
          },
        },
      },
      location: { type: 'ONSHORE' },
      competentAuthority: 'ENGLAND',
    };
    taskId = 321;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, ReviewSummaryComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show holding company details when existing', () => {
    const nameLabel = fixture.debugElement.query(By.css('#hc-name > dt')).nativeElement;
    const name = fixture.debugElement.query(By.css('#hc-name > dd')).nativeElement;
    const regnumLabel = fixture.debugElement.query(By.css('#hc-regnum > dt')).nativeElement;
    const regNum = fixture.debugElement.query(By.css('#hc-regnum > dd')).nativeElement;
    const addressLabel = fixture.debugElement.query(By.css('#hc-address > dt')).nativeElement;
    const address = fixture.debugElement.query(By.css('#hc-address > dd')).nativeElement;

    expect(nameLabel.textContent.trim()).toStrictEqual('Holding company name');
    expect(name.textContent.trim()).toStrictEqual('TEST_HOLDING_COMPANY');
    expect(regnumLabel.textContent.trim()).toStrictEqual('Holding company registration number');
    expect(regNum.textContent.trim()).toStrictEqual('TEST_REG_NUM');
    expect(addressLabel.textContent.trim()).toStrictEqual('Holding company address');
    expect(address.textContent.replace(/\s+/g, '')).toStrictEqual('TEST_ADDR_L1TEST_CITY99999');
  });
});
