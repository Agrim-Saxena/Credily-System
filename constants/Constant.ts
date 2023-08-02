import * as moment from "moment";

export class Constant{
    
    static EMPTY_STRINGS = [null, undefined, "", "N/A", "n/a", " ", "null"];

	static currentDate = moment().valueOf();
	static nintyDaysMilisecond=7776000000;

	static today = moment().format('YYYY-MM-DD');
    /**
	 * Authentication constants
	 */
	static PASSWORD_INVISIBLE = "password"
	static PASSWORD_VISIBLE = "text"
	static AUTHORIZATION = "Authorization"
	static BEARER = "Bearer"
	static TOKEN = "idToken"
	static REFRESH_TOKEN = "refreshToken"
	static FCM_TOKEN = "fcmToken"
	static ROLE = "role"
	static ROLE_TYPE = "roleType"
	static CLIENT_BUSINESS_UUID = "clientBusinessUuid"
	static ACCOUNT_UUID = "accountUuid"
	static USER_UUID = "userUuid"
	static USER_EMAIL = "email"
	static USER_IS_PASSWORD_CHANGE = "isReset"
	static IS_INTERNAL_ROLE= "isInternalRole";
	static CLIENT = "client"
	static MODULE = "module"
	static DOC_TYPE_EXPIRABLE = "EXPIRABLE";
	static DOC_TYPE_ATTACHMENT = "ATTACHMENT"

	/**
	 * Role
	 */
	static ROLE_ENTERPRISE = ['Super Admin', 'Enterprise Leader'];
	static DISABLE_ROLE_ACCESS = [3, 4, 5];
	static TYPE_ENTERPRISE = ['ENTERPRISE'];
	static ROLE_CLIENT = ['Client', 'CLIENT'];
	static ROLE_PROVIDER = 'Provider';
	static ROLE_PROVIDER_LIST:any[] = ['Provider', 'PROVIDER'];
	static SUPER_ADMIN = 'Super Admin';
	static ENTERPRISE_USER = 'Enterprise User';

	/**
	 * privilege constant
	 */
	static VIEW_PRIVILEGE = 1;
	static MANAGE_PRIVILEGE = 2;

	/**
	 * Module
	 */

	static DASHBOARD = 'Dashboard';
	static TASKS = 'Tasks';
	static EXPIRABLES = 'Expirables';
	static CREDENTIALING = 'Credentialing';
	static PRIVILEGING = 'Privileging';
	static VIRTUAL_BOARDS_COMMITTEE = 'Virtual Boards/Committee';
	static STAFF = 'Staff';
	static ENROLLMENT = 'Enrollment';

	/**
	 * Expirable
	 */
	static up_To_Date = 'upToDate';
	static about_To_Expire = 'aboutToExpire';
	static expired = 'expired';

	/**
	 * On-Going-Monitoring
	 */
	static MONITORING_STATUS_PENDING = 'Pending'
	static MONITORING_STATUS_FOUND = 'Found'
	static MONITORING_STATUS_NOT_FOUND = 'Not Found'
	
  	static shareWhatsAppUrl: string;
  	static shareGmailUrl: string;

	static FIREBASE_INPROCESS = "INPROCESS";
	static FIREBASE_COMPLETED = "COMPLETED";
	static FIREBASE_MONITORING_STATUS = "monitoring";

	/**
	 * Attachment
	 */

	static ATTACHMENT_UP_TO_DATE = "upToDate"
	static ATTACHMENT_EXPIRING_SOON = "aboutToExpire"
	static ATTACHMENT_EXPIRED = "expired"
}