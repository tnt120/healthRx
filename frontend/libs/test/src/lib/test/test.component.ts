import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'lib-test',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './test.component.html',
  styleUrl: './test.component.css',
})
export class TestComponent {}
