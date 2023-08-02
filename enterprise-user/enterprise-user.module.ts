import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EnterpriseUserRoutingModule } from './enterprise-user-routing.module';
import { EnterpriseUserComponent } from './enterprise-user.component';
import { UsersComponent } from './users/users.component';
import { HeaderModule } from '../header/header.module';
import { SharedModule } from '../modules/shared/shared/shared.module';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    EnterpriseUserComponent,
    UsersComponent
  ],
  imports: [
    CommonModule,
    EnterpriseUserRoutingModule,
    HeaderModule,
    FormsModule,
    SharedModule
  ]
})
export class EnterpriseUserModule { }
