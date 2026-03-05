import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PipesModule } from '@shared/pipes/pipes.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { SharedPermitModule } from '../shared-permit.module';
import { MeasurementDevicesTableComponent } from './measurement-devices-table.component';

describe('MeasurementDevicesTableComponent', () => {
  @Component({
    template: `
      <app-measurement-devices-table
        [data]="data"
        [isEditable]="isEditable"
        [noBottomBorder]="noBottomBorder"></app-measurement-devices-table>
    `,
  })
  class TestComponent {
    data: MeasurementDeviceOrMethod[] = [
      {
        id: '17182688689830.9659447356127433',
        reference: 'Rerum facere dolore',
        type: 'WEIGHSCALE',
        measurementRange: 'Saepe quis quaerat d',
        meteringRangeUnits: 'Id rem sed sit cumq',
        uncertaintySpecified: false,
        location: 'Qui voluptate sed il',
      },
      {
        id: '17182688750190.9296288245096855',
        reference: 'Quia voluptate conse',
        type: 'WEIGHBRIDGE',
        measurementRange: 'Dolor at iure est n',
        meteringRangeUnits: 'Beatae aut et aperia',
        uncertaintySpecified: true,
        specifiedUncertaintyPercentage: '1',
        location: 'Qui voluptatem delec',
      },
    ];
    isEditable = true;
    noBottomBorder = false;
  }

  let component: MeasurementDevicesTableComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get rows() {
      return this.queryAll<HTMLDListElement>('tr').map((measurementDevice) =>
        Array.from(measurementDevice.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }

    get changeLinks(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a').filter((el) => el.textContent.trim() === 'Change');
    }

    get deleteLinks(): HTMLAnchorElement[] {
      return this.queryAll<HTMLAnchorElement>('a').filter((el) => el.textContent.trim() === 'Delete');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(MeasurementDevicesTableComponent)).componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [
        CommonModule,
        GovukComponentsModule,
        PipesModule,
        SharedPermitModule,
        RouterTestingModule,
        MeasurementDevicesTableComponent,
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should display the measurement devices', () => {
    createComponent();

    expect(page.rows).toEqual([
      [],
      [
        'Rerum facere dolore',
        'Weighscale',
        'Saepe quis quaerat d',
        'Id rem sed sit cumq',
        'None',
        'Qui voluptate sed il',
        'Change',
        'Delete',
      ],
      [
        'Quia voluptate conse',
        'Weighbridge',
        'Dolor at iure est n',
        'Beatae aut et aperia',
        '± 1 %',
        'Qui voluptatem delec',
        'Change',
        'Delete',
      ],
    ]);

    expect(page.changeLinks.length).toBe(2);
    expect(page.deleteLinks.length).toBe(2);
  });
});
