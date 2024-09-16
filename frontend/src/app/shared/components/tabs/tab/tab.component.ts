import { Component, input, Input, model, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, inject } from '@angular/core';

@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  styleUrl: './tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabComponent {
  private readonly cdRef = inject(ChangeDetectorRef);

  headerTitle = model.required<string>();

  private _active = false;

  @Input()
  get active(): boolean {
    return this._active;
  }

  set active(val: boolean) {
    if (val !== this._active) {
      this._active = val;
      this.cdRef.detectChanges();
    }
  }

  padding = input.required<string>();
}
