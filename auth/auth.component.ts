import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Constant } from '../constants/Constant';
import { Route } from '../constants/Route';
import { TokenResponse } from '../models/TokenResponse';
import { AuthService } from '../services/auth.service';
import { DataService } from '../services/data.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {

  constructor() {}

  ngOnInit(): void {
    
  }
}
