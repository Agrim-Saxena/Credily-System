import { Component, OnInit } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Constant } from 'src/app/constants/Constant';
import { Route } from 'src/app/constants/Route';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { Expirable } from 'src/app/models/Expirable';
import { Provider } from 'src/app/models/Provider';
import { DataService } from 'src/app/services/data.service';
import { ProviderService } from 'src/app/services/provider.service';
@Component({
  selector: 'app-all-provider-expirable',
  templateUrl: './all-provider-expirable.component.html',
  styleUrls: ['./all-provider-expirable.component.css']
})
export class AllProviderExpirableComponent implements OnInit {
  
  databaseHelper: DatabaseHelper = new DatabaseHelper();

  dropdownSettingsName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  providerNameList: any[] = new Array();
  selectedProvider: any[] = new Array();

  dropdownSettingsSpecialty!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  specialtyList: any[] = new Array();
  selectedSpecialty: any[] = new Array();

  dropdownSettingsStatus!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  statusList: any[] = new Array();
  selectedStatus: any[] = new Array();

  dropdownSettingsStateName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  stateNameList: any[] = new Array();
  selectedStateName: any[] = new Array();

  readonly Constant = Constant;
  readonly Route = Route;
  provider: Provider = new Provider();
  expirable: Expirable = new Expirable();

  uuid: any;
  type: any;
  enableSearch: boolean = false;

  specialtyFilterToggle: boolean = false;
  statusFilterToggle: boolean = false;
  stateFilterToggle: boolean = false;
  nameFilterToggle: boolean = false;
  providerUUIDs: any[] = new Array();
  databaseHelper2: DatabaseHelper = new DatabaseHelper();
  expirableLoadingToggle: boolean = false
  expirableLoadingToggle2: boolean = false
  expirableLoadingToggleAll: boolean = false
  expirableList: any[] = new Array();
  expirableTypeList: any[] = new Array();
  expirableTypeLists: any[] = new Array();
  expirationCount: number = 0

  expirableSearch = new Subject<string>();
  constructor(public dataService: DataService,
    public router: Router,
    private providerSerivce: ProviderService

  ) {
    this.expirableSearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.databaseHelper.currentPage=1;
        this.getAllExpirables();
      });

   }

  ngOnInit() {
    
    this.dropdownSettingsName = {
      singleSelection: false,
      text: 'Select Provider',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsSpecialty = {
      singleSelection: true,
      text: 'Select Specialty',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsStatus = {
      singleSelection: true,
      text: 'Select Status',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.dropdownSettingsStateName = {
      singleSelection: false,
      text: 'Select State',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

    this.getAllExpirables();
    this.getExpirableCount();


    var temp1: { id: string, itemName: string } = { id: 'upToDate', itemName: 'Up To Date' };
    this.statusList.push(temp1);
    var temp2: { id: string, itemName: string } = { id: 'aboutToExpire', itemName: 'About To Expire' };
    this.statusList.push(temp2);
    var temp3: { id: string, itemName: string } = { id: 'expired', itemName: 'Expired' };
    this.statusList.push(temp3);
  }

  pageChanged(event: any) {
    if (event != this.databaseHelper.currentPage) {
      this.databaseHelper.currentPage = event;
      this.getAllExpirables();
    }

  }
  pageChangedFilter(event: any) {
    if (event != this.databaseHelper2.currentPage) {
      this.databaseHelper2.currentPage = event;
      this.getFilteredExpirables();
    }

  }

  //------------------Filter Start------------------------>>

  filterByName() {
    this.nameFilterToggle = !this.nameFilterToggle;
    this.getAllProviderName();
    this.credentialType = '';
  }

  filterByState() {
    this.stateFilterToggle = !this.stateFilterToggle;
    this.getExpirableState();
    this.credentialType = '';
  }

  filterByStatus() {
    this.statusFilterToggle = !this.statusFilterToggle;
    this.getExpirableState();
    this.credentialType = '';
  }

  filterBySpecialty() {
    this.specialtyFilterToggle = !this.specialtyFilterToggle;
    this.getExpirableSpecialty();
    this.credentialType = '';
  }

  getExpirableSpecialty() {
    this.specialtyList = [];
    this.providerSerivce.getExpirableSpecialty().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.description, itemName: element.description }
        this.specialtyList.push(temp);
      })
      this.specialtyList = JSON.parse(JSON.stringify(this.specialtyList));
    })
  }

  getExpirableState() {
    this.stateNameList = [];
    this.providerSerivce.getExpirableState().subscribe(response => {
      response.forEach((element: any) => {
        if (element.stateCode != null || element.stateName != null) {
          var temp: { id: string, itemName: string } = { id: element.stateCode, itemName: element.stateName }
          this.stateNameList.push(temp);
        }
      })
      this.stateNameList = JSON.parse(JSON.stringify(this.stateNameList));
    })
  }

  getAllProviderName() {
    this.providerNameList = [];
    this.providerSerivce.getAllProviderName().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.uuid, itemName: element.firstName + " " + element.lastName }
        this.providerNameList.push(temp);
      });
      this.providerNameList = JSON.parse(JSON.stringify(this.providerNameList));
    })
  }

  selectProvider(event: any) {

  }

  specialty: any;
  selectSpecialty(event: any) {
    if (this.specialtyFilterToggle) {
      this.specialty = '';
      this.credentialType = '';
      if (event[0] != undefined) {
        this.specialty = event[0].itemName;
        this.getAllExpirables();
      } else {
        this.specialty = '';
        this.getAllExpirables();
      }
      this.specialtyFilterToggle = false;
    }
  }

  selectStatus(event: any) {
    if (this.statusFilterToggle) {
      this.credentialType = '';
      if (event[0] != undefined) {
        this.credentialType = event[0].id;
        this.getAllExpirables();
      } else {
        this.credentialType = '';
        this.getAllExpirables();
      }

      this.statusFilterToggle = false;
    }
  }

  states: any[] = new Array();
  selectState(event: any) {

  }


  //------------------Filter End------------------------<<
  // searchExpirable(){
  //   debugger
  //   this.expirableList = [];
  //   this.getAllExpirables();
  // }

  totalItemsCount: number = 0;
  getAllExpirables() {
    debugger
    this.expirableLoadingToggle = true
    if (this.nameFilterToggle) {
      this.providerUUIDs = [];
      this.credentialType = '';
      if (this.selectedProvider.length > 0) {
        this.selectedProvider.forEach((element: any) => {
          this.providerUUIDs.push(element.id);
        })
      } else {
        this.providerUUIDs = [];
      }
      this.nameFilterToggle = false;
    }

    if (this.stateFilterToggle) {
      this.states = [];
      this.credentialType = '';
      if (this.selectedStateName.length > 0) {
        this.selectedStateName.forEach((element: any) => {
          this.states.push(element.itemName);
        })
      } else {
        this.states = [];
      }

      this.stateFilterToggle = false;
    }
    this.providerSerivce.getAllExpirables(this.credentialType, this.databaseHelper, this.providerUUIDs, this.states, this.specialty, 1).subscribe((response) => {
      this.totalItemsCount = response.totalItems;
      this.expirableList = response.object;
      if (this.expirableList.length > 0) {
        this.enableSearch = true
      }
      if (this.expirableList != undefined) {
        this.expirableList.forEach((e: any) => {
          const diff: any = moment(e.expirationDate).diff(moment().format('YYYY-MM-DD'), 'days')
          if (diff >= 90) {
            e.expirationCount = 0;
          } else if (diff > 0 && diff <= 90) {
            e.expirationCount = 1;
          } else if (diff < 0) {
            e.expirationCount = 2;
          }
        });
      }

      this.expirableLoadingToggle = false

    }, error => {
      this.expirableLoadingToggle = false
    })
  }

  totalItemsCount2: number = 0;
  getFilteredExpirables() {
    debugger
    this.providerUUIDs = [];
    this.states = [];
    this.specialty = '';
    this.expirableLoadingToggle2 = true;
    this.isAllSelected = false;
    this.ids=[];
    this.providerSerivce.getAllExpirables(this.credentialType, this.databaseHelper, this.providerUUIDs, this.states, this.specialty, 0).subscribe((response) => {
      this.totalItemsCount2 = response.totalItems;
      this.expirableTypeLists = response.object;

      this.expirableTypeLists.forEach(element => {
        element.isChecked = false;
      });

      this.expirableLoadingToggle2 = false

    }, error => {
      this.expirableLoadingToggle2 = false
    })
  }

  upToDate: string = ''
  aboutToExpire: string = ''
  expired: string = ''
  getExpirableCount() {
    this.uuid = this.dataService.getClientBussinessInfoUUID();
    this.providerSerivce.getExpirableCount(this.uuid, this.providerUUIDs).subscribe((response) => {
      this.upToDate = response.upToDate;
      this.aboutToExpire = response.aboutToExpire
      this.expired = response.expired
    }, (error: any) => {
      this.dataService.showToast('Something went wrong!');
    })
  }

  credentialType: string = '';
  cartToggle: boolean = false;
  toggleCartFunc(type: string) {
    debugger
    this.credentialType = type;
    this.getFilteredExpirables()
    this.cartToggle = !this.cartToggle;
  }
  togglecloseCartFunc() {
    this.cartToggle = false;
    this.notificationSent =false;

  }
  notificationToggle:boolean = false
  notificationSent:boolean = false
  sendingNotification:boolean = false
  sendProviderNotification(){
    debugger
    this.sendingNotification = true;
    this.providerSerivce.sendProviderNotification(this.credentialType,this.ids).subscribe((response)=>{
      this.notificationSent = true;
      this.onUpdateSuccess();
      this.sendingNotification = false;
    }, error=>{
      this.sendingNotification = false;
    })
  }
  onUpdateSuccess() {
    debugger
    setTimeout( () => {
      this.notificationSent=false;
      this.ids = [];
      this.expirableTypeLists.forEach(element => {
        element.isChecked = false;
      });
    }, 5000);
  }

  ids:any[] = new Array();
  isAllSelected:boolean = false;
  count:number = 0;
  selectAll(){
    debugger
   if(!this.isAllSelected){
    this.isAllSelected=true;
    this.ids = [];
    this.expirableTypeLists.forEach(element => {
      element.isChecked = true;
      this.ids.push(element.id);
      // var i = this.ids.findIndex(e=>e==element.id);
      // if(i == -1){
      //   this.ids.push(element.id);
      // }
    });
   }
   else {
    this.isAllSelected = false;
    this.expirableTypeLists.forEach(element => {
      element.isChecked = false;
    });
    this.ids = [];
   }
  }

  selectOne(obj:any){
    debugger
    var i = this.ids.findIndex(e=>e==obj.id);
    if(!obj.isChecked){
      obj.isChecked = true;
      if(i == -1){
        this.ids.push(obj.id);
      }
    }else{
      obj.isChecked = false;
      this.isAllSelected = false;
      if(i > -1){
        this.ids.splice(i,1);
      }
    }
    if(this.ids.length == this.expirableTypeLists.length){
      this.isAllSelected=true;
    }else{
      this.isAllSelected=false;
    }
  }

  routeToProviderDashboard(providerUuid:any) {

    let navigationExtras: NavigationExtras = {
      queryParams: { "uuid": providerUuid },
    };
    this.router.navigate([this.Route.EXPIRABLE_PROVIDER_DASHBOARD], navigationExtras);

  }

}
