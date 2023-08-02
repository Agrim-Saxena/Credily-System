import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { USStates } from 'src/app/models/common-json/UsStatesJson';

@Component({
  selector: 'app-state-dropdown',
  templateUrl: './state-dropdown.component.html',
  styleUrls: ['./state-dropdown.component.css']
})
export class StateDropdownComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    this.dropdownSettingsStateCode = {
      singleSelection: true,
      text: 'Select State Code',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsStateName = {
      singleSelection: true,
      text: 'Select State Name',
      enableSearchFilter: true,
      autoPosition: false
    };

    this.dropdownSettingsCityName = {
      singleSelection: true,
      text: 'Select City Name',
      enableSearchFilter: true,
      autoPosition: false
    };
    this.getStateCode();
  }

  @Input()
  stateCodeProp!: string;
  @Output()
  stateCodePropChange = new EventEmitter<string>();

  @Input()
  stateNameProp!: string;
  @Output()
  stateNamePropChange = new EventEmitter<string>();

  @Input()
  cityNameProp!: string;
  @Output()
  cityNamePropChange = new EventEmitter<string>();

  @Input()
  stateCodeClass!: string;
  @Input()
  stateNameClass!: string;
  @Input()
  cityClass!: string;

  @Input()
  requiredClass!: string;

  @Input()
  stateCodeDynamic!: string;
  @Input()
  stateNameDynamic!: string;
  @Input()
  cityNameDynamic!: string;
  @Input()
  stateNameLabelClass!: string;
  @Input()
  cityNameLabelClass!: string;
  @Input()
  stateCodeLabelClass!:String;

  @Input()
  labelStyle!: string;

  @Output()
  callFunction = new EventEmitter<boolean>();

  caller: string | null = null;
  stateCodeList: any[] = new Array();
  stateNameList: any[] = new Array();
  cityNameList: any[] = new Array();

  @Input()
  updatedStateCode: any[] = new Array();

  @Input()
  updatedStateName: any[] = new Array();

  @Input()
  updatedCityName: any[] = new Array();
  checkState: USStates = new USStates();
  @Input() set _updatedStateCode(value: any) {
    debugger
    if ((value == undefined || value == null || value == '')) {
      this.updatedStateCode = [];
      this.updatedStateName = [];
    } else {
      // if (value == undefined || value == null || value == '') {
      //   value = this.updatedStateCode[0];
      // }
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.itemName = value;
      this.updatedStateCode = [];
      this.cities = [];
      this.updatedStateCode.push(temp);

      this.checkState.statesJson.forEach((element: any) => {
        if (value == element.abbreviation) {
          if(  this.stateName==undefined){
            this.stateName = element.name;
          }
          this.updatedStateName = [];
          var tempStateName: { id: any, itemName: any } = { id: '', itemName: '' }
          tempStateName.id = element.name;
          tempStateName.itemName = element.name;
          this.updatedStateName.push(tempStateName);
        }
      })

      new USStates().cityJson.forEach(element => {
        if (this.stateName == element.state) {
          this.cityNameList.push(element.city);
        }
      })
      // this.cityNameList = (new USStates().cityJson as any)[this.stateName];

      if (this.cityNameList != undefined && this.cityNameList != null) {
        this.cityNameList.forEach((element: any) => {
          var temp: { id: any, itemName: any } = { id: '', itemName: '' };
          temp.id = element;
          temp.itemName = element;
          this.cities.push(temp);
        })
      }
    }
  }

  @Input() set _updatedStateName(value: any) {
    debugger
    // value = this.updatedStateName;
    if ((value == undefined || value == null || value == '') && this.updatedStateName.length == 0) {
      this.updatedStateName = [];
      this.updatedStateCode = [];
    }
    else {
      if (value == undefined || value == null || value == '') {
        value = this.updatedStateName[0];
      }
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.id = value;
      temp.itemName = value;
      this.updatedStateName = [];
      this.cities = [];
      this.updatedStateName.push(temp);

      this.checkState.statesJson.forEach((element: any) => {
        if (value == element.name) {
          // this.stateCode = element.abbreviation;
          this.updatedStateCode = [];
          var tempStateName: { id: any, itemName: any } = { id: '', itemName: '' }
          tempStateName.id = element.abbreviation;
          tempStateName.itemName = element.abbreviation;
          this.updatedStateCode.push(tempStateName);
          this.stateCodeProp = tempStateName.itemName;
        }
      })

      new USStates().cityJson.forEach(element => {
        if (this.stateName == element.state) {
          this.cityNameList.push(element.city);
        }
      })

      // this.cityNameList = (new USStates().cityJson as any)[temp.itemName];

      if (this.cityNameList != undefined && this.cityNameList != null) {
        this.cityNameList.forEach((element: any) => {
          var temp: { id: any, itemName: any } = { id: '', itemName: '' };
          temp.id = element;
          temp.itemName = element;
          this.cities.push(temp);
        })
      }
    }
  }

  @Input() set _updatedCityName(value: any) {
    if ((value == undefined || value == null || value == '') ) {
      this.updatedCityName = [];
    } else {
      var temp: { id: any, itemName: any } = { id: '', itemName: '' };
      temp.itemName = value;
      this.updatedCityName = [];
      this.updatedCityName.push(temp);
    }
  }


  functionCall: boolean = false;
  cityName!: string;
  stateName!: string;
  cities: any[] = new Array();
  stateCode!: string;
  stateCodeObj: USStates | null = null;

  dropdownSettingsCityName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean, autoPosition: boolean };
  dropdownSettingsStateCode!: { singleSelection: boolean; text: string; enableSearchFilter: boolean, autoPosition: boolean };
  dropdownSettingsStateName!: { singleSelection: boolean; text: string; enableSearchFilter: boolean, autoPosition: boolean };

  getStateCode() {
    if (this.stateCodeObj == null) {
      this.stateCodeObj = new USStates();
    }
    var stateCodeList: any[] = new Array();;
    var stateNameList: any[] = new Array();;
    this.stateCodeObj.statesJson.forEach((element: any) => {
      var temp: { id: any, itemName: any, stateName: any } = { id: '', itemName: '', stateName: '' };
      temp.id = element.abbreviation
      temp.itemName = element.abbreviation
      temp.stateName = element.name
     stateCodeList.push(temp);

      var temp2: { id: any, itemName: any, stateCode: any } = { id: '', itemName: '', stateCode: '' };
      temp2.id = element.name;
      temp2.itemName = element.name;
      temp2.stateCode = element.abbreviation;
     stateNameList.push(temp2);
    });
    this.stateCodeList = JSON.parse(JSON.stringify(stateCodeList));
    this.stateNameList = JSON.parse(JSON.stringify(stateNameList));
  }

  selectStateCode(event: any) {
    debugger
    if (event.length == 0) {
      this.stateCode = '';
      
      this.updatedStateCode = [];
      this.updatedStateName = [];
      this.sendToCaller();
      this.functionCall = true;
      
    }
    this.cities = [];
    this.updatedCityName = [];
    if (event[0] != undefined) {
      this.stateCode = event[0].itemName;
      var temp: { id: any, itemName: any, stateCode: any } = { id: '', itemName: '', stateCode: '' };
      temp.id = event[0].stateName;
      temp.itemName = event[0].stateName;
      temp.stateCode = event[0].itemName;
      this.stateCode = temp.stateCode;

      this.cityNameList = [];
      if (this.updatedStateCode.length > 0) {
        this.updatedStateName = [];
        this.updatedStateName.push(temp);
        this.stateName = temp.itemName;

        new USStates().cityJson.forEach(element => {
          if (this.stateName == element.state) {
            this.cityNameList.push(element.city);
          }
        })

        // this.cityNameList = (new USStates().cityJson as any)[this.stateName];

        if (this.cityNameList.length > 0) {
          this.cities = [];
          this.cityNameList.forEach((element: any) => {
            var temp4: { id: any, itemName: any } = { id: '', itemName: '' };
            temp4.id = element;
            temp4.itemName = element;
            this.cities.push(temp4);
          })
        }
      }
    }
    this.sendToCaller();
    this.functionCall = true;
  }

  selectStateName(event: any) {
    debugger
    if (event.length == 0) {
      this.stateName = '';
      this.stateCode = '';
      this.updatedStateCode = [];
      this.updatedStateName = [];
      this.functionCall = true;
      this.sendToCaller();
      
    }
    this.cities = [];
    this.updatedCityName = [];
    if (event[0] != undefined) {
      this.stateName = event[0].itemName;
      var temp: { id: any, itemName: any, stateName: any } = { id: '', itemName: '', stateName: '' };
      temp.id = event[0].stateCode;
      temp.itemName = event[0].stateCode;
      temp.stateName = event[0].itemName;
      this.stateName = temp.stateName;
      this.cityNameList = [];
      if (this.updatedStateName.length > 0) {
        this.updatedStateCode = [];
        this.updatedStateCode.push(temp);
        this.stateCode = temp.itemName

        new USStates().cityJson.forEach(element => {
          if (this.stateName == element.state) {
            this.cityNameList.push(element.city);
          }
        })

        // this.cityNameList = (new USStates().cityJson as any)[temp.stateName];

        if (this.cityNameList != undefined) {
          this.cities = [];
          this.cityNameList.forEach((element: any) => {
            var temp3: { id: any, itemName: any } = { id: '', itemName: '' };
            temp3.id = element;
            temp3.itemName = element;
            this.cities.push(temp3);
          })
        }
      }
    }
    this.sendToCaller();
    this.functionCall = true;
  }

  selectCityName(event: any) {
    debugger
    if (event[0] != undefined) {
      this.cityName = event[0].itemName;
      this.sendToCaller();
    }else{
      this.cityName = '';
      this.sendToCaller();
     }
  }

  sendToCaller() {
    debugger
    this.stateNamePropChange.emit(this.stateName);
    this.stateCodePropChange.emit(this.stateCode);
    this.cityNamePropChange.emit(this.cityName);
    this.callFunction.emit(this.functionCall)
  }

}
