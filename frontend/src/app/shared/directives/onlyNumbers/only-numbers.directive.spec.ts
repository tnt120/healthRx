import { ElementRef } from '@angular/core';
import { OnlyNumbersDirective } from './only-numbers.directive';

describe('OnlyNumbersDirective', () => {
  let elementRefMock: ElementRef;

  beforeEach(() => {
    // Tworzymy mock ElementRef, np. z inputem
    elementRefMock = new ElementRef(document.createElement('input'));
  });

  it('should create an instance', () => {
    // Tworzymy instancję dyrektywy z mockiem ElementRef
    const directive = new OnlyNumbersDirective(elementRefMock);
    expect(directive).toBeTruthy();
  });
});
