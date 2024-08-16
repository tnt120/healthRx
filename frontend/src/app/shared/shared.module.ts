import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DividerComponent } from './components/divider/divider.component';
import { MyMaterialModule } from '../material';
import { ForbiddenRegexValidatorDirective } from './directives/forbiddenRegexValidator/forbidden-regex-validator.directive';
import { RepeatPasswordValidatorDirective } from './directives/repeatPasswordValidator/repeat-password-validator.directive';
import { ParameterTailComponent } from './components/parameter-tail/parameter-tail.component';
import { SettingsComponent } from './components/settings/settings.component';



@NgModule({
  declarations: [
    DividerComponent,
    ForbiddenRegexValidatorDirective,
    RepeatPasswordValidatorDirective,
    ParameterTailComponent,
    SettingsComponent
  ],
  imports: [
    CommonModule,
    MyMaterialModule
  ],
  exports: [
    DividerComponent,
    ForbiddenRegexValidatorDirective,
    RepeatPasswordValidatorDirective,
    ParameterTailComponent,
    SettingsComponent
  ]
})
export class SharedModule { }
