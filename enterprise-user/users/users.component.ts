import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Constant } from 'src/app/constants/Constant';
import { DatabaseHelper } from 'src/app/models/DatabaseHelper';
import { EnterpriseLeader } from 'src/app/models/EnterpriseLeader';
import { EnterpriseLeaderRequest } from 'src/app/models/EnterpriseLeaderRequest';
import { Role } from 'src/app/models/Role';
import { DataService } from 'src/app/services/data.service';
import { EnterpriseUserService } from 'src/app/services/enterprise-user.service';
import { RoleService } from 'src/app/services/role.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  readonly Constant = Constant;

  databaseHelper:DatabaseHelper = new DatabaseHelper();
  eLeaders : EnterpriseLeader[] = new Array();
  fetchingLeader:boolean = false;

  roles : Role[] = new Array();

  @ViewChild('closeLeaderModel') closeLeaderModel !: ElementRef;

  constructor(private dataService:DataService,
    private _roleService:RoleService,
    private _userService:EnterpriseUserService) {

  }

  ngOnInit() {
    this.getEnterpriseLeader();
  }

  getEnterpriseLeader(){
    this.fetchingLeader = true;
    this._userService.getAllEnterpriseLeader().subscribe(response => {
      if(response.status){
        this.eLeaders = response.object;
      }
      this.fetchingLeader = false;
    },error=>{
      this.fetchingLeader = false;
      this.dataService.showToast('Network Error..!!');
    })
  }

  openCreateLeaderModel(){
    this.getRole();
  }

  getRole(){
    this._roleService.getAllRoles("create").subscribe(response=>{
      if(response.status){
        this.roles = response.object;
      }
    },error=>{
      this.dataService.showToast('Network Error..!!');
    })
  }
  eLeaderReq : EnterpriseLeaderRequest = new EnterpriseLeaderRequest();
  savingUser:boolean = false;
  createLeader(){
    console.log('user : ' + JSON.stringify(this.eLeaderReq));
    this.savingUser = true;
    this._userService.createEnterpriseLeader(this.eLeaderReq).subscribe(response=>{
      if(response.status){
        this.getEnterpriseLeader();
        this.closeLeaderModel.nativeElement.click();
      }
      this.savingUser = false;
    },error=>{
      this.savingUser = false;
      this.dataService.showToast('Network Error..!!');
    })
    
  }

  closeCreateModel(){
    this.eLeaderReq = new EnterpriseLeaderRequest();
  }


}
