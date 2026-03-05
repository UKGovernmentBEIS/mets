import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PipesModule } from '@shared/pipes/pipes.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { EmissionSource } from 'pmrv-api';

import { EmissionSourceTableComponent } from './emission-source-table.component';

describe('EmissionSourceTableComponent', () => {
  @Component({
    template: `
      <app-emission-source-table [data]="data" [isEditable]="isEditable"></app-emission-source-table>
    `,
  })
  class TestComponent {
    data: EmissionSource[] = [
      {
        id: '17182688689830.9659447356127433',
        reference: 'Rerum facere dolore',
        description: 'Saepe quis quaerat d',
      },
      {
        id: '17182688750190.9296288245096855',
        reference: 'Quia voluptate conse',
        description: 'Dolor at iure est n',
      },
    ];
    isEditable = true;
    noBottomBorder = false;
  }

  let component: EmissionSourceTableComponent;
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
    component = fixture.debugElement.query(By.directive(EmissionSourceTableComponent)).componentInstance;
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
        RouterLink,
        RouterTestingModule,
        EmissionSourceTableComponent,
      ],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();

    expect(component).toBeTruthy();
  });

  it('should display the data', () => {
    createComponent();

    expect(page.rows).toEqual([
      [],
      ['Rerum facere dolore', 'Saepe quis quaerat d', 'Change', 'Delete'],
      ['Quia voluptate conse', 'Dolor at iure est n', 'Change', 'Delete'],
    ]);

    expect(page.changeLinks.length).toBe(2);
    expect(page.deleteLinks.length).toBe(2);
  });
});
