import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css']
})
export class BreadcrumbComponent implements OnInit {
  providerUUID: any;
  planProviderId: any;
  addonProviderId: any;

  constructor(private router: Router,
    private dataService:DataService,
    private _routeParams: ActivatedRoute) { }

  accountUUID:any;

  ngOnInit(): void {
    if (this._routeParams.snapshot.queryParamMap.has('account')) {
      this.accountUUID = this._routeParams.snapshot.queryParamMap.get('account');
    }
    if (this._routeParams.snapshot.queryParamMap.has('provider')) {
      this.providerUUID = this._routeParams.snapshot.queryParamMap.get('provider');
    }
    if (this._routeParams.snapshot.queryParamMap.has('planProviderId')) {
      this.planProviderId = Number(this._routeParams.snapshot.queryParamMap.get('planProviderId'));
    }

    if (this._routeParams.snapshot.queryParamMap.has('addonProviderId')) {
      this.addonProviderId = Number(this._routeParams.snapshot.queryParamMap.get('addonProviderId'));
    }
  }

  @Input()
  destinationLink:any[]= new Array();

  @Input()
  class:any;

  navigate(destination:any){
    debugger
  //   if(destination == 'Accounts'){
  //     this.destinationLink = [];
  //     this.router.navigate(['/account/list']);
  //   }
  //   else if(destination == 'Account Details'){
  //     this.destinationLink = [];
  //     let navigationExtras: NavigationExtras = {
  //       queryParams: { "q": this.dataService.accountUUID },
  //     };
  //     this.router.navigate(['/account/details'], navigationExtras);
  //   }
  //   else if(destination == 'Addons'){
  //     this.destinationLink = [];
  //     let navigationExtras: NavigationExtras = {
  //       queryParams: { "q": this.dataService.accountUUID },
  //     };
  //     this.router.navigate(['/account/payer/addon/list'], navigationExtras);
  //   }
  //   else if(destination == 'Plans & Group Plans'){
  //     this.destinationLink = [];
  //     let navigationExtras: NavigationExtras = {
  //       queryParams: { "q": this.dataService.accountUUID },
  //     };
  //     this.router.navigate(['/account/payer/plan/list'], navigationExtras);
  //   }
  //   else if(destination == 'Tickets'){
  //     this.destinationLink = [];
  //     let navigationExtras: NavigationExtras = {
  //       queryParams: { "account": this.accountUUID,'provider':this.providerUUID,'planProviderId':this.planProviderId,'addonProviderId':this.addonProviderId},
  //     };
  //     this.router.navigate(['/account/ticket/list'], navigationExtras);
  //   }
  //   else if(destination == 'Providers'){
  //     this.destinationLink = [];
  //     let navigationExtras: NavigationExtras = {
  //       queryParams: { "q": this.dataService.accountUUID },
  //     };
  //     this.router.navigate(['/account/provider/list'], navigationExtras);
  //   }
    
    

  }


}
