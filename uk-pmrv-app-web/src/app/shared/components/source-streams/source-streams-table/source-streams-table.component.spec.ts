import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PipesModule } from '@shared/pipes/pipes.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { SourceStream } from 'pmrv-api';

import { SourceStreamsTableComponent } from './source-streams-table.component';

describe('SourceStreamsTableComponent', () => {
  @Component({
    template: `
      <app-source-streams-table [data]="data" [isEditable]="isEditable"></app-source-streams-table>
    `,
  })
  class TestComponent {
    data: SourceStream[] = [
      {
        id: '17182688689830.9659447356127433',
        reference: 'Rerum facere dolore',
        description: 'SCRAP_TYRES',
        type: 'COMBUSTION_COMMERCIAL_STANDARD_FUELS',
      },
      {
        id: '17182688750190.9296288245096855',
        reference: 'Quia voluptate conse',
        description: 'ACETYLENE',
        type: 'REFINERIES_CATALYTIC_CRACKER_REGENERATION',
      },
    ];
    isEditable = true;
  }

  let component: SourceStreamsTableComponent;
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
    component = fixture.debugElement.query(By.directive(SourceStreamsTableComponent)).componentInstance;
    page = new Page(fixture);

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [CommonModule, GovukComponentsModule, PipesModule, RouterTestingModule, SourceStreamsTableComponent],
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
      ['Rerum facere dolore', 'Scrap Tyres', 'Combustion: Commercial standard fuels', 'Change', 'Delete'],
      ['Quia voluptate conse', 'Acetylene', 'Refineries: Catalytic cracker regeneration', 'Change', 'Delete'],
    ]);

    expect(page.changeLinks.length).toBe(2);
    expect(page.deleteLinks.length).toBe(2);
  });
});
