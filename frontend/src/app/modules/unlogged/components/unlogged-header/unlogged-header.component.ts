import { Component, input } from '@angular/core';

@Component({
  selector: 'app-unlogged-header',
  templateUrl: './unlogged-header.component.html',
  styleUrl: './unlogged-header.component.scss'
})
export class UnloggedHeaderComponent {
  isAuth = input.required<boolean>();
}
