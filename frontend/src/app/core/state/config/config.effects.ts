import { inject, Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { UserService } from "../../services/user/user.service";
import { configActions } from "./config.actions";
import { catchError, map, mergeMap, of, switchMap, tap } from "rxjs";

export const configEffect = createEffect((
  actions$ = inject(Actions),
  userService = inject(UserService)
) => {
  return actions$.pipe(
    ofType(configActions.load),
    mergeMap(() => {
      return userService.getInitAndConfig().pipe(
        tap((config) => {
          console.log('Config initialized', config)
        }),
        map(config => configActions.loadSuccess({ config })),
        catchError(err => {
          console.error('Error loading config', err);

          return of(configActions.loadError({ error: err }))
        })
      )
    })
  )
}, { functional: true })
