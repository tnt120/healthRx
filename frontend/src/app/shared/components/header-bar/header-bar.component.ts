import { Component, inject, input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { map, Observable } from 'rxjs';
import { UserResponse } from '../../../core/models/user/user-response.model';
@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrl: './header-bar.component.scss'
})
export class HeaderBarComponent implements OnInit {
  private readonly store = inject(Store);

  user$: Observable<UserResponse> = this.store.select('user');

  userName$!: Observable<string>;

  title = input.required<string>();

  ngOnInit(): void {
    this.userName$ = this.user$.pipe(
      map(user => user.firstName)
    )
  }
}
