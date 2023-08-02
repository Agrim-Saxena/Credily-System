export class Route{

    /**
     * Auth
     */
    static AUTH_ROUTE = '/auth/login';

    static FORGOT_PASSWORD = '/auth/forgot';
    static RESET_PASSWORD = '/auth/reset-password';

    /**
     * Dashboard
     */
    static DASHBOARD = '/expirables/all-provider-expirable';

    /**
     * Provider
     */
    static PROVIDER_PERSONAL_INFO = '/provider-onboarding/personal-information';
    static PROVIDER_CAQH_INFO = '/provider-onboarding/caqh-information';
    static PROVIDER_BASIC_INFO = '/provider-onboarding/basic-information';
    static PROVIDER_STATE_LICENSE = '/provider-onboarding/state-license';
    static PROVIDER_DEA_INFO = '/provider-onboarding/dea';
    static PROVIDER_SPECIALTIES_INFO = '/provider-onboarding/specialties';
    static PROVIDER = '/staff/providers';

    /**
     * Enterprise 
     */
     static ENTERPRISE_DASHBOARD = '/staff/facility';

     /**
      * Expirable
      */
     static EXPIRABLE_ALL_PROVIDER = '/expirables/all-provider-expirable';
     static EXPIRABLE_PROVIDER = '/expirables/provider-dashboard';
     static EXPIRABLE_PROVIDER_DASHBOARD = '/expirables/provider-dashboard';
     static ROLE_PERMISSION = "/roles-permission/user-role";
     static EXPIRABLE_PROVIDER_VAULT = '/expirables/document-vault';
     static EXPIRABLE_PROVIDER_AUDIT = '/expirables/audittrail-details';
     static EXPIRABLE_PROVIDER_NOTIFICATION_VIEW = '/provider/provider-notification-details';
     static EXPIRABLE_PROVIDER_NOTIFICATION_MANAGE = '/provider/provider-notification';

     /**
      * Provider Profile
      */

     static PROVIDER_PROFILE = '/provider-profile/personal-information'

}