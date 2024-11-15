import { TestBed } from '@angular/core/testing';

import { WebsocketService } from './websocket.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('WebsocketService', () => {
  let service: WebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()]
    });
    service = TestBed.inject(WebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
