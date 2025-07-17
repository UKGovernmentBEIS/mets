import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { asyncData, BasePage } from '@testing';

import { MiReportsService } from 'pmrv-api';

import { MiReportsModule } from '../mi-reports.module';
import { mockVerificationBodiesUsersMiReportResult } from '../testing/mock-data';
import { VerificationBodiesUsersComponent } from './verification-bodies-users.component';

class Page extends BasePage<VerificationBodiesUsersComponent> {
  get table() {
    return this.query<HTMLDivElement>('.govuk-table');
  }

  get executeButton() {
    return this.query<HTMLButtonElement>('button');
  }
}

describe('VerificationBodiesUsersComponent', () => {
  let component: VerificationBodiesUsersComponent;
  let fixture: ComponentFixture<VerificationBodiesUsersComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsService>>;

  beforeEach(async () => {
    miReportsService = {
      generateReport: jest.fn().mockReturnValue(asyncData(mockVerificationBodiesUsersMiReportResult)),
      getCurrentUserMiReports: jest.fn().mockReturnValue(asyncData(null)),
    };
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, MiReportsModule],
      declarations: [VerificationBodiesUsersComponent],
      providers: [{ provide: MiReportsService, useValue: miReportsService }, DestroySubject],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificationBodiesUsersComponent);
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
        'Sample Verification Body Organisation',
        'Pending',
        '1111111',
        'false',
        'false',
        'false',
        'true',
        'Verifier admin',
        'VerifierFirst1 VerifierLast1',
        'g@r',
        '111111',
        'Pending',
        '06 Νοεμβρίου 2022 09:23:26',
      ],
      ...[
        'VerificationBody2',
        'Pending',
        '222222',
        'true',
        'false',
        'false',
        'false',
        'Verifier admin',
        'First2 Last2',
        'last2@o.com',
        '222222',
        'Pending',
        '07 Νοεμβρίου 2022 09:23:26',
      ],
    ]);
  });
});
