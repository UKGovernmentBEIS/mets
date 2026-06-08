import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { asyncData, BasePage } from '@testing';

import { MiReportsService } from 'pmrv-api';

import { UsersForServiceAuthorityComponent } from './users-for-service-authority.component';

class Page extends BasePage<UsersForServiceAuthorityComponent> {
  get table() {
    return this.query<HTMLDivElement>('.govuk-table');
  }

  get executeButton() {
    return this.query<HTMLButtonElement>('button');
  }
}

describe('UsersForServiceAuthorityComponent', () => {
  let component: UsersForServiceAuthorityComponent;
  let fixture: ComponentFixture<UsersForServiceAuthorityComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsService>>;

  beforeEach(async () => {
    miReportsService = {
      generateReport: jest.fn().mockReturnValue(
        asyncData({
          reportType: 'LIST_OF_USER_REPORT_ENTRIES',
          columnNames: [
            'User Account ID',
            'Name',
            'User type',
            'User Account status',
            'Contact types',
            'Email',
            'Telephone',
            'Mobile',
            'Last login',
          ],
          results: [
            {
              'User Account ID': 'abab',
              Name: 'operator2 hse',
              'User type': 'operator_admin',
              'User Account status': 'ACTIVE',
              'Contact types': ['SERVICE', 'FINANCIAL', 'PRIMARY', 'SECONDARY'],
              Email: 'operator2@hse.com',
              Telephone: '+441234567890',
              'Last login': '03 Μαρτίου 2026 13:22:59',
            },
          ],
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [UsersForServiceAuthorityComponent],
      providers: [provideRouter([]), { provide: MiReportsService, useValue: miReportsService }, DestroySubject],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UsersForServiceAuthorityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render table rows', () => {
    page.executeButton.click();
    fixture.detectChanges();
    const cells = Array.from(page.table.querySelectorAll('td'));

    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...[
        'abab',
        'operator2 hse',
        'Operator admin',
        'Active',
        'Service, Financial, Primary, Secondary',
        'operator2@hse.com',
        '+441234567890',
        '',
        '03 Μαρτίου 2026 13:22:59',
      ],
    ]);
  });
});
