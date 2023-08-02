import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';
import { Subject } from 'rxjs/internal/Subject';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Constant } from 'src/app/constants/Constant';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { BusinessInfoService } from 'src/app/services/business-info.service';
import { DataService } from 'src/app/services/data.service';
import { ExpirableService } from 'src/app/services/expirable.service';

@Component({
  selector: 'app-all-provider-facility',
  templateUrl: './all-provider-facility.component.html',
  styleUrls: ['./all-provider-facility.component.css']
})
export class AllProviderFacilityComponent implements OnInit {


  readonly Constant = Constant;

  dropdownSettingsName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  providerNameList: any[] = new Array();
  selectedProvider: any[] = new Array();

  dropdownSettingsSpecialty!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  specialtyList: any[] = new Array();
  selectedSpecialty: any[] = new Array();

  dropdownSettingsStateName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  stateNameList: any[] = new Array();
  selectedStateName: any[] = new Array();

  dropdownSettingsStatus!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  statusList: any[] = new Array();
  selectedStatus: any[] = new Array();

  facilitySearch = new Subject<string>();
  constructor(private expirableService: ExpirableService,
    public dataService: DataService
  ) { 
    this.facilitySearch.pipe(
      debounceTime(600),
      distinctUntilChanged())
      .subscribe(value => {
        this.facilityDatabaseHelper.currentPage=1;
        this.getAllFacility();
    });

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

    this.dropdownSettingsAddress = {
      singleSelection: true,
      text: 'Select Address',
      enableSearchFilter: true,
      autoPosition: false,
      badgeShowLimit: 1
    };

  }

  ngOnInit(): void {
    this.getAllFacility();
    this.getFacilityCount();
    this.getFacilityAddress();

    var temp1: { id: string, itemName: string } = { id: 'upToDate', itemName: 'Up To Date' };
    this.statusList.push(temp1);
    var temp2: { id: string, itemName: string } = { id: 'aboutToExpire', itemName: 'About To Expire' };
    this.statusList.push(temp2);
    var temp3: { id: string, itemName: string } = { id: 'expired', itemName: 'Expired' };
    this.statusList.push(temp3);
  }


  facilityList: any[] = new Array();
  filterFacilityList: any[] = new Array();
  loadingToggle: boolean = false
  specialtyFilterToggle: boolean = false;
  statusFilterToggle: boolean = false;
  filterloadingToggle: boolean = false;
  totalFacilityItems: number = 0;
  totalFilterFacilityItems: number = 0;
  facilityDatabaseHelper: DatabaseHelper = new DatabaseHelper();
  filterFacilityDatabaseHelper: DatabaseHelper = new DatabaseHelper();
  providerUUIDs: any[] = new Array();
  states: any[] = new Array();
  nameFilterToggle: boolean = false;
  enableSearch: boolean = false;
  stateFilterToggle: boolean = false;
  uptoDateCount: number = 0;
  expiringSoonCount: number = 0;
  expiredCount: number = 0;
  specialty: any;

  cartToggle: boolean = false;
  facilityType: string = '';
  // credentialType: string = '';

  toggleCartFunc(type: string) {
    this.facilityType = type;
    this.cartToggle = !this.cartToggle;
    this.getFilteredFacility();
  }
  togglecloseCartFunc() {
    this.cartToggle = false;
  }

  getAllFacility() {
    this.loadingToggle = true
    if (this.nameFilterToggle) {
      this.providerUUIDs = [];
      this.facilityType = '';
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
      this.facilityType = '';
      if (this.selectedStateName.length > 0) {
        this.selectedStateName.forEach((element: any) => {
          this.states.push(element.itemName);
        })
      } else {
        this.states = [];
      }

      this.stateFilterToggle = false;
    }

    this.expirableService.getExpirableByFacility(this.facilityType, this.facilityDatabaseHelper, this.providerUUIDs, this.states, this.specialty, 1, this.addressSearch).subscribe((response) => {
      if (response.object.length > 0) {
        this.enableSearch = true;
      }

      this.facilityList = response.object;

      this.totalFacilityItems = response.totalItems;

      if (this.facilityList != undefined) {
        this.facilityList.forEach((e: any) => {
          const diff: any = moment(e.expirationDate).diff(moment().format('YYYY-MM-DD'), 'days')
          if (diff >= 90) {
            e.attachmentStatus = this.Constant.ATTACHMENT_UP_TO_DATE;
          } else if (diff > 0 && diff <= 90) {
            e.attachmentStatus = this.Constant.ATTACHMENT_EXPIRING_SOON;
          } else if (diff < 0) {
            e.attachmentStatus = this.Constant.ATTACHMENT_EXPIRED;
          }
        });
      }
      this.loadingToggle = false
    }, error => {
      this.loadingToggle = false
    });
  }


  // totalItemsCount2: number = 0;
  getFilteredFacility() {
    debugger
    this.providerUUIDs = [];
    this.states = [];
    this.specialty = '';
    this.filterloadingToggle = true;

    this.expirableService.getExpirableByFacility(this.facilityType, this.filterFacilityDatabaseHelper, this.providerUUIDs, this.states, this.specialty, 0, '').subscribe((response) => {
      this.totalFilterFacilityItems = response.totalItems;
      this.filterFacilityList = response.object;
      this.filterloadingToggle = false

    }, error => {
      this.filterloadingToggle = false
    })
  }

  pageChanged(event: any) {
    if (event != this.facilityDatabaseHelper.currentPage) {
      this.facilityDatabaseHelper.currentPage = event;
      this.getAllFacility();
    }

  }

  filteredPageChanged(event: any) {
    if (event != this.filterFacilityDatabaseHelper.currentPage) {
      this.filterFacilityDatabaseHelper.currentPage = event;
      this.getFilteredFacility();
    }

  }

  uuid: any;
  getFacilityCount() {
    this.expirableService.getExpirableCount().subscribe((response) => {
      this.uptoDateCount = response.upToDate;
      this.expiringSoonCount = response.aboutToExpire;
      this.expiredCount = response.expired;
    }, (error: any) => {
      this.dataService.showToast('Something went wrong!');
    })
  }




/************************** address dropdown ********************************/



  dropdownSettingsAddress!: { singleSelection: boolean; text: string; enableSearchFilter: boolean; autoPosition: boolean, badgeShowLimit: number; };
  providerAddressList: any[] = new Array();
  selectedProviderAddress: any[] = new Array();
  // selectedProvider: any[] = new Array();
  addressSearch:string='';

  getFacilityAddress() {
    
    this.expirableService.getFacilityAddress(this.addressSearch).subscribe(response => {

      if (response.status && response.object != null) {
        this.providerAddressList = [];
        response.object.forEach((element:any) => {
          var temp: { id: any, itemName: any } = { id: '', itemName: '' };
          temp.id = element;
          temp.itemName = element;
          this.providerAddressList.push(temp);
        });
        this.providerAddressList = JSON.parse(JSON.stringify(this.providerAddressList));
      }
    }, error => {
      // this.loadingClient=false;
    })

  }

  onSearch(event: any) {
    debugger
    this.addressSearch = event.target.value;
    this.getFacilityAddress();
  }
  

  selectAddress(event: any) {

    debugger
    if (event != undefined && event != null && event.length > 0) {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = event[0].id;
      temp.itemName = event[0].itemName;
      this.selectedProviderAddress = [];
      this.selectedProviderAddress.push(temp);
      this.addressSearch = event[0].itemName;
      this.getAllFacility();
    }else{
      this.addressSearch = '';
      this.getAllFacility();
    }

  }


  /************************** Filters ********************************/


  filterByName() {
    this.nameFilterToggle = !this.nameFilterToggle;
    this.getAllProviderName();
    this.facilityType = '';
  }

  filterBySpecialty() {
    this.specialtyFilterToggle = !this.specialtyFilterToggle;
    this.getExpirableSpecialty();
    this.facilityType = '';
  }

  filterByState() {
    this.stateFilterToggle = !this.stateFilterToggle;
    this.getExpirableState();
    this.facilityType = '';
  }

  filterByStatus() {
    this.statusFilterToggle = !this.statusFilterToggle;
    this.getExpirableState();
    this.facilityType = '';
  }

  getAllProviderName() {
    this.providerNameList = [];
    this.expirableService.getAllProviderName().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.uuid, itemName: element.firstName + " " + element.lastName }
        this.providerNameList.push(temp);
      });
      this.providerNameList = JSON.parse(JSON.stringify(this.providerNameList));
    })
  }

  selectSpecialty(event: any) {
    if (this.specialtyFilterToggle) {
      this.specialty = '';
      this.facilityType = '';
      if (event[0] != undefined) {
        this.specialty = event[0].itemName;
        this.getAllFacility();
      } else {
        this.specialty = '';
        this.getAllFacility();
      }
      this.specialtyFilterToggle = false;
    }
  }

  getExpirableSpecialty() {
    this.specialtyList = [];
    this.expirableService.getExpirableSpecialty().subscribe(response => {
      response.forEach((element: any) => {
        var temp: { id: string, itemName: string } = { id: element.description, itemName: element.description }
        this.specialtyList.push(temp);
      })
      this.specialtyList = JSON.parse(JSON.stringify(this.specialtyList));
    })
  }

  getExpirableState() {
    this.stateNameList = [];
    this.expirableService.getExpirableState().subscribe(response => {
      response.forEach((element: any) => {
        if (element.stateCode != null || element.stateName != null) {
          var temp: { id: string, itemName: string } = { id: element.stateCode, itemName: element.stateName }
          this.stateNameList.push(temp);
        }
      })
      this.stateNameList = JSON.parse(JSON.stringify(this.stateNameList));
    })
  }

  selectStatus(event: any) {
    if (this.statusFilterToggle) {
      this.facilityType = '';
      if (event[0] != undefined) {
        this.facilityType = event[0].id;
        this.getAllFacility();
      } else {
        this.facilityType = '';
        this.getAllFacility();
      }

      this.statusFilterToggle = false;
    }
  }

}
