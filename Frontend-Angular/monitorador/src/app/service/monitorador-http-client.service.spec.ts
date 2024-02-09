import { TestBed } from '@angular/core/testing';

import { MonitoradorHttpClientService } from './monitorador-http-client.service';

describe('MonitoradorHttpClientService', () => {
  let service: MonitoradorHttpClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MonitoradorHttpClientService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
