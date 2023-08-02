import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EnterpriseUserComponent } from './enterprise-user.component';
import { UsersComponent } from './users/users.component';

const routes: Routes = [{ path: '', component: EnterpriseUserComponent,
children:[
  { path: 'user', component: UsersComponent}
] 
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EnterpriseUserRoutingModule { }
