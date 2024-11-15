import { getTestBed } from '@angular/core/testing';
import { BrowserDynamicTestingModule, platformBrowserDynamicTesting } from '@angular/platform-browser-dynamic/testing';

declare const require: any;

// Initialize the Angular testing environment without zone.js
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);

// Find all the tests.
import './app/app.component.spec';
// Możesz też dodać inne specyficzne testy, jak np. dla serwisów, dyrektyw itp.

(window as any).global = window;
