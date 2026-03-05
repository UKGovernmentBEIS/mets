import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { TransferCO2 } from 'pmrv-api';

import { TransferInstallationSummaryTemplateComponent } from './transfer-installation-summary-template.component';

describe('TransferInstallationSummaryTemplateComponent', () => {
  let component: TransferInstallationSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-transfer-installation-summary-template [transfer]="transfer"></app-transfer-installation-summary-template>
    `,
  })
  class TestComponent {
    transfer = {
      entryAccountingForTransfer: true,
      transferDirection: 'EXPORTED_FOR_PRECIPITATED_CALCIUM',
      installationEmitter: {
        emitterId: '11111',
        email: 'test@mail.com',
      },
    } as TransferCO2;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(
      By.directive(TransferInstallationSummaryTemplateComponent),
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the transfer installation summary', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDivElement>('div')).map((dl) =>
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ),
    ).toEqual([
      ['Yes'],
      [
        'Exported out of our installation and used to produce precipitated calcium carbonate, in which the used CO2 is chemically bound',
      ],
      ['11111'],
      ['test@mail.com'],
    ]);

    hostComponent.transfer = {
      transferType: 'TRANSFER_CO2',
      entryAccountingForTransfer: false,
    };

    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDivElement>('div')).map((dl) =>
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ),
    ).toEqual([['No']]);
  });
});
