import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { MiReportsListGuard } from './mi-reports-list.guard';

describe('MiReportsListGuard', () => {
  let guard: MiReportsListGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    guard = TestBed.inject(MiReportsListGuard);
  });

  it('should create', () => {
    expect(guard).toBeTruthy();
  });
});
