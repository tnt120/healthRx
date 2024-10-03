import { Component, input, Input, model, ChangeDetectionStrategy, ChangeDetectorRef, inject, HostBinding, signal } from '@angular/core';

@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  styleUrl: './tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TabComponent {
  private readonly cdRef = inject(ChangeDetectorRef);

  headerTitle = model.required<string>();

  private _active = signal<boolean>(false);

  @HostBinding('class.active')
  get isActive(): boolean {
    return this._active();
  }

  @Input()
  get active(): boolean {
    return this._active();
  }

  set active(val: boolean) {
    setTimeout(() => {
      if (val !== this._active()) {
        this._active.set(val);
        this.cdRef.detectChanges();
      }
    });
  }

  padding = input.required<string>();
}
