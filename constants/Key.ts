export class Key {

    /**
     * Server url Key
     */
    // host_url: string = "http://test-1.ap-south-1.elasticbeanstalk.com/";
    host_url: string = "http://localhost:8080/"; 

    /**
     * panel link
     */

    host_panel:string = "http://credilyv3.s3-website.ap-south-1.amazonaws.com/#/";
    // host_panel:string = "http://localhost:4200/#/";

    /**
     * role keys
     */
    role_management = 'api/v3/role-management';
    /**
     * module keys
     */
    get_module = 'api/v3/role-management/module';
    get_module_view = 'api/v3/role-management/view';
    get_client_module = 'api/v3/client-module';
    get_provider_module = 'api/v3/provider-module';
    get_update_client_user_module='api/v3/client-module/user';
    /**
     * role-module keys
     */
    get_role_module = 'api/v3/role-management/role-module';

    /**
     * user account
     */
    user_account_controller = 'api/v3/user-account';
    login_user = 'api/v3/user-account/login';
    reset_pass = 'api/v3/user-account/reset';
    update_token = 'api/v3/user-account/update-token';
    user_details = 'api/v3/user-account/details';
    /**
     * Enterprise Leader
     */
    enterprise_leader = 'api/v3/enterprise-leader';

    /**
     * Enterprise User
     */
    enterprise_user = 'api/v3/enterprise-user';

    /**
     * Client keys
     */
    business_info = 'api/v3/client';
    business_info_owner_update = 'api/v3/client/owner';
    client_user_controller = 'api/v3/client-user';
    client_taxonomy = 'api/v3/client-taxonomy';
    client_provider_address = 'api/v3/provider-address';
    /**
     * Provider Keys
     */
    provider_info = 'api/v3/provider';
    provider_details = 'api/v3/provider/by-uuid';
    get_texonomy = 'api/v3/taxonomy-map/individual';
    invite_info = 'api/v3/invite'

    /**
     * Expirable keys
     */

    create_expirable = 'api/v3/expirable';
    get_expirable = 'api/v3/expirable';
    get_document_type = 'api/v3/document_type';
    get_document_file_type = 'api/v3/document_file_type'
    get_all_expirables = 'api/v3/expirable/client-uuid';
    create_attachment = 'api/v3/attachment';
    provider_attachment='api/v3/attachment/provider';
    provider_attachment_by_page='api/v3/attachment/by-page';
    update_expirable_notification = 'api/v3/expirable/notification';
    update_expirable_frequency = 'api/v3/expirable/notification-frequency';
    audit_trail='api/v3/audit/'
    delete_expirable = 'api/v3/expirable/expirable-delete';
    create_monitoring = 'api/v3/monitoring'
    get_monitoring = 'api/v3/monitoring'
    update_expirable='api/v3/expirable/update'
    get_expirable_count = 'api/v3/expirable/count';

    /**
     * Provider profile Url
     */
    provider_profile = 'api/v3/provider-profile'
    provider_profile_education = 'api/v3/provider-education'
    provider_profile_caqh_status = 'api/v3/caqh/status'
    // Notification url

    all_notification = 'api/v3/notification';

    document_share_controller = 'api/v3/document-share';



}