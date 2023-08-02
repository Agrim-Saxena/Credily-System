import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EnterpriseDashboardComponent } from './enterprise-dashboard.component';

const routes: Routes = [
  {
    path: '', component: EnterpriseDashboardComponent,
    children: [
      {path: 'dashboard', component: DashboardComponent}
    ]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EnterpriseDashboardRoutingModule { }
