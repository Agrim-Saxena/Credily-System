package com.credv3.caqh;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.credv3.caqh.models.CAQHHospital;
import com.credv3.caqh.models.CAQHProvider;
import com.credv3.caqh.models.Degree;
import com.credv3.caqh.models.Education;
import com.credv3.caqh.models.Insurance;
import com.credv3.caqh.models.Practice;
import com.credv3.caqh.models.ProviderAddresses;
import com.credv3.caqh.models.ProviderCDSCaqh;
import com.credv3.caqh.models.ProviderDEACaqh;
import com.credv3.caqh.models.ProviderLicense;
import com.credv3.caqh.models.ProviderMedicaidCaqh;
import com.credv3.caqh.models.ProviderMedicareCaqh;
import com.credv3.caqh.models.Specialty;
import com.credv3.caqh.models.TimeGap;
import com.credv3.caqh.models.WorkHistory;
import com.credv3.common.entities.CAQHRosterBatch;
import com.credv3.common.entities.EmploymentRecords;
import com.credv3.common.entities.Provider;
import com.credv3.common.entities.ProviderAddress;
import com.credv3.common.entities.ProviderCaqhInfo;
import com.credv3.common.entities.ProviderCds;
import com.credv3.common.entities.ProviderCertification;
import com.credv3.common.entities.ProviderDea;
import com.credv3.common.entities.ProviderHospitalAffiliation;
import com.credv3.common.entities.ProviderInsuranceInformation;
import com.credv3.common.entities.ProviderMedicaid;
import com.credv3.common.entities.ProviderMedicare;
import com.credv3.common.entities.ProviderPracticeDetail;
import com.credv3.common.entities.ProviderProfessionalLicense;
import com.credv3.common.entities.ProviderProfessionalSchoolInfo;
import com.credv3.common.entities.ProviderSpeciality;
import com.credv3.common.entities.ProviderUndergraduateEducation;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.HelperExtension;
import com.credv3.helper.ResponseEntityObject;
import com.credv3.helper.RestTemplateHandler;
import com.credv3.stateAndCity.StateJPARepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CAQHHandler extends CentralService {
	
	
	HelperExtension helperExtension = new HelperExtension();

	@Autowired
	private CAQHRosterBatchesRepository cAQHRosterBatchRepository;
	
	@Autowired
	private RestTemplateHandler restTemplateHandler;
	
	@Autowired
	public StateJPARepo stateJPARepo;
	
	public void queryProviderWithNPI(String npi, UUID providerUuid) {
		QueryNPIResponse queryNPIResponse = restTemplateHandler.queryProviderWithNPI(npi);
		com.fasterxml.jackson.databind.ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = null;
		try {
			
			json = ow.writeValueAsString(queryNPIResponse);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject(json);
		if (jsonObject.get("provider_found_flag").equals("Y")) {
			addProviderToRoster(queryNPIResponse.getCaqh_provider_id(), providerUuid);
		}
	}

	public String addProviderToRoster(String caqhId, UUID providerUUID) {
		
		Provider provider = providerJpaRepo.findByUuid(providerUUID);
		ProviderCaqhInfo caqh = providerCaqhInfoJPARepo.findTop1ByProvider_Uuid(providerUUID);
		if(caqh!=null) {
			caqh.setCaqhId(caqhId);
			providerCaqhInfoJPARepo.save(caqh);
		}
		
		String url = "https://proview.caqh.org/RosterAPI/API/Roster?";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("product", "PV"));
		url += URLEncodedUtils.format(params, "UTF-8");

		// setup HTTP Auth
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bikhamsaas", "Bikham@1991_");
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);

		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		try {
			JsonArray body = Json.createArrayBuilder().add(Json.createObjectBuilder().add("provider",
					Json.createObjectBuilder().add("first_name", "Test").add("middle_name", "").add("last_name", "Caas")
							.add("name_suffix", "").add("gender", "").add("address1", "").add("address2", "")
							.add("city", "").add("state", "").add("zip", "").add("zip_extn", "").add("phone", "")
							.add("fax", "").add("email", "").add("practice_state", "").add("birthdate", "")
							.add("ssn", "").add("short_ssn", "").add("dea", "").add("upin", "").add("type", "")
							.add("tax_id", "").add("npi", "").add("license_state", "").add("license_number", ""))
					.add("caqh_provider_uuid", caqhId).add("po_provider_uuid", "").add("last_recredential_date", "")
					.add("next_recredential_date", "").add("delegation_flag", "").add("application_type", "")
					.add("affiliation_flag", "").add("organization_id", "1561").add("region_id", "")).build();

			HttpPost request = new HttpPost(url);
			StringEntity bodyParams = new StringEntity(body.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(bodyParams);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			JsonReader jsonReader = Json.createReader(new StringReader(responseString));
			JsonObject responseJson = jsonReader.readObject();
			jsonReader.close();
			String batchId = responseJson.getString("batch_Id");
			CAQHRosterBatch cAQHRosterBatch = new CAQHRosterBatch();
			cAQHRosterBatch.setBatchId(batchId);
			cAQHRosterBatch.setProvider(provider);
			cAQHRosterBatch.setStatus("Inprocess");
			cAQHRosterBatchRepository.save(cAQHRosterBatch);
			return responseJson.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("CAQHHandler.addProviderToRoster() : " + ex.getMessage());
			return "Error";
		}
	}
	
	
	@Scheduled(cron = "0 */1 * ? * *")
	public void fetchBatchResult() {
		List<String> statusList = new ArrayList<>();
		statusList.add("Saved");
		List<CAQHRosterBatch> cAQHRosterBatchList = cAQHRosterBatchRepository.findByStatusNotIn(statusList);
		for (CAQHRosterBatch cAQHRosterBatch : cAQHRosterBatchList) {
			BatchStatusResponse batchStatusResponse = restTemplateHandler.getBatchResult(cAQHRosterBatch.getBatchId());

			if (batchStatusResponse.getBatch_status().equalsIgnoreCase("Complete")) {
				try {
					saveProviderData(cAQHRosterBatch);
				} catch (Exception e) {
					cAQHRosterBatch.setStatus("Exception : " + e.getMessage());
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
				}
			} else {
				cAQHRosterBatch.setStatus(batchStatusResponse.getBatch_status());
				cAQHRosterBatchRepository.save(cAQHRosterBatch);
			}

		}
	}

	public ResponseEntity<Object> saveProviderData(CAQHRosterBatch cAQHRosterBatch) {
		Provider provider = providerJpaRepo.findByUuid(cAQHRosterBatch.getProvider().getUuid());
		ProviderCaqhInfo caqh = providerCaqhInfoJPARepo.findTop1ByProvider_Uuid(cAQHRosterBatch.getProvider().getUuid());
		String url = "https://proview.caqh.org/credentialingapi/api/v7/entities?";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("caqhProviderId", caqh.getCaqhId()));
		params.add(new BasicNameValuePair("attestationDate", "10/02/2021"));
		params.add(new BasicNameValuePair("organizationId", "1561"));
		url += URLEncodedUtils.format(params, "UTF-8");
		// setup HTTP Auth
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bikhamsaas", "Bikham@1991_");
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() != 200) {

				if (response.getStatusLine().getStatusCode() == 295) {
					cAQHRosterBatch.setStatus("No Provider Attestation data found");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "No Provider Attestation data found"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 294) {
					cAQHRosterBatch.setStatus("Provider is not current and complete");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider is not current and complete"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 293) {
					cAQHRosterBatch.setStatus("Provider has not given authorization");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider has not given authorization"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 292) {
					cAQHRosterBatch.setStatus("Provider is not active on the roster");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider is not active on the roster"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 204) {
					cAQHRosterBatch.setStatus("Provider/Organization (ID) not found");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider/Organization (ID) not found"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 400) {
					cAQHRosterBatch.setStatus(
							"Missing required parameter -> Organization ID/CAQH Provider ID/Attestation date.");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Missing required parameter like -> Organization ID/CAQH Provider ID/Attestation date."), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 401) {
					cAQHRosterBatch.setStatus("{'error':'Authentication failed.'}");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Authentication failed.'}"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 500) {
					cAQHRosterBatch.setStatus("{'error':'Internal error occurred.  Please try again later'}");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Internal error occurred.  Please try again later'}"), HttpStatus.OK);
				} else {
					cAQHRosterBatch.setStatus("{'error':'Internal error occurred.  Please try again later'}");
					cAQHRosterBatchRepository.save(cAQHRosterBatch);
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Internal error occurred.  Please try again later'}"), HttpStatus.OK);
				}
			}

			String responseString = EntityUtils.toString(entity);
			JSONObject jsonObject = XML.toJSONObject(responseString);
			JsonReader jsonReader = Json.createReader(new StringReader(jsonObject.toString()));
			JsonObject json = jsonReader.readObject();
			JsonObject caqhProviderDetail = json.getJsonObject("Provider");
			CompletableFuture.runAsync(() -> {
				try {
					getProviderData(cAQHRosterBatch.getProvider().getUuid());
				} catch (Exception e) {
					// TODO: Change this
					e.printStackTrace();
				}
			});
			cAQHRosterBatch.setStatus("Saved");
			cAQHRosterBatchRepository.save(cAQHRosterBatch);

			return new ResponseEntity<>(new ResponseEntityObject<>(true, "success", caqhProviderDetail), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	

	public void getProviderData(UUID providerUUID) {
		Provider provider = providerJpaRepo.findByUuid(providerUUID);
		ProviderCaqhInfo caqhInfo = providerCaqhInfoJPARepo.findTop1ByProvider_Uuid(providerUUID);
		String url = "https://proview.caqh.org/credentialingapi/api/v7/entities?";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("caqhProviderId", caqhInfo.getCaqhId()));
		params.add(new BasicNameValuePair("attestationDate", "10/02/2021"));
		params.add(new BasicNameValuePair("organizationId", "1561"));
		url += URLEncodedUtils.format(params, "UTF-8");
		// setup HTTP Auth
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bikhamsaas", "Bikham@1991_");
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
//			System.out.println(response.getStatusLine());
//			System.out.println(response.getStatusLine().getStatusCode());
			String responseString = EntityUtils.toString(entity);
			JSONObject jsonObject = XML.toJSONObject(responseString);
			JsonReader jsonReader = Json.createReader(new StringReader(jsonObject.toString()));
			JsonObject json = jsonReader.readObject();
//			System.out.println(json.toString());
			JsonObject caqhProviderDetail = json.getJsonObject("Provider");
//			List<CAQHProvider> cAQHProviders = cAQHProviderRepository.findByProvider_Uuid(provider.getId());
//			if (!cAQHProviders.isEmpty()) {
//				cAQHProviderRepository.deleteAll(cAQHProviders);
//			}
			if (caqhProviderDetail != null) {
				CAQHProvider caqhProvider = new CAQHProvider();
				Gson gson = new Gson();
				gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

				JsonElement je = new Gson().fromJson(caqhProviderDetail.toString(), JsonElement.class);
				com.google.gson.JsonObject jo = je.getAsJsonObject();
				Object providerLicenseResponseCheck = caqhProviderDetail.get("ProviderLicense");
				Object practicecheckResponse = caqhProviderDetail.get("Practice");
				Object timeGapResponseCheck = caqhProviderDetail.get("TimeGap");
				Object workHistoryResponseCheck = caqhProviderDetail.get("WorkHistory");
				Object educationResponseCheck = caqhProviderDetail.get("Education");
				Object degreeResponseCheck = caqhProviderDetail.get("Degree");
				Object specialtyResponseCheck = caqhProviderDetail.get("Specialty");
				Object insuranceResponseCheck = caqhProviderDetail.get("Insurance");
				Object providerAddressResponseCheck = caqhProviderDetail.get("ProviderAddress");
				Object cdsResponseCheck = caqhProviderDetail.get("ProviderCDS");
				Object deaResponseCheck = caqhProviderDetail.get("ProviderDEA");
				Object providerMedicareCheck = caqhProviderDetail.get("ProviderMedicare");
				Object providerMedicaidCheck = caqhProviderDetail.get("ProviderMedicaid");
				Object hospitalResponseCheck = caqhProviderDetail.get("Hospital");
				
				List<Practice> practices = new ArrayList<>();
				List<Specialty> specialties = new ArrayList<>();
				List<WorkHistory> workHistories = new ArrayList<>();
				List<Education> educations = new ArrayList<>();
				List<Insurance> insurances = new ArrayList<>();
				List<Degree> degrees = new ArrayList<>();
				List<TimeGap> timeGaps = new ArrayList<>();
				List<ProviderLicense> providerLicenses = new ArrayList<>();
				List<ProviderAddresses> providerAddresses = new ArrayList<>();
				List<ProviderCDSCaqh> providerCDSes = new ArrayList<>();
				List<ProviderDEACaqh> providerDeas = new ArrayList<>();
				List<ProviderMedicareCaqh> providerMedicares = new ArrayList<>();
				List<ProviderMedicaidCaqh> providerMedicaids = new ArrayList<>();
				List<CAQHHospital> caqhHospitals = new ArrayList<>();

				if (providerAddressResponseCheck != null && providerAddressResponseCheck.toString().startsWith("[")) {

				} else if (providerAddressResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderAddresses providerAddress = gson2.fromJson(jo.get("ProviderAddress"),
							ProviderAddresses.class);
					if (providerAddress != null) {
						providerAddresses.add(providerAddress);
					}
					jo.remove("ProviderAddress");

				}
				if (cdsResponseCheck != null && cdsResponseCheck.toString().startsWith("[")) {

				} else if (cdsResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderCDSCaqh providerCDS = gson2.fromJson(jo.get("ProviderCDS"), ProviderCDSCaqh.class);
					if (providerCDS != null) {
						providerCDSes.add(providerCDS);
					}
					jo.remove("ProviderCDS");

				}
				
				if (deaResponseCheck != null && deaResponseCheck.toString().startsWith("[")) {

				} else if (deaResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderDEACaqh providerDEA = gson2.fromJson(jo.get("ProviderDEA"), ProviderDEACaqh.class);
					if (providerDEA != null) {
						providerDeas.add(providerDEA);
					}
					jo.remove("ProviderDEA");

				}
				
				if (providerMedicaidCheck != null && providerMedicaidCheck.toString().startsWith("[")) {

				} else if (providerMedicaidCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderMedicaidCaqh providerMedicaidCaqh = gson2.fromJson(jo.get("ProviderMedicaid"), ProviderMedicaidCaqh.class);
					if (providerMedicaidCaqh != null) {
						providerMedicaids.add(providerMedicaidCaqh);
					}
					jo.remove("ProviderMedicaid");

				}

				
				if (providerMedicareCheck != null && providerMedicareCheck.toString().startsWith("[")) {

				} else if (providerMedicareCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderMedicareCaqh providerMedicare = gson2.fromJson(jo.get("ProviderMedicare"), ProviderMedicareCaqh.class);
					if (providerMedicare != null) {
						providerMedicares.add(providerMedicare);
					}
					jo.remove("ProviderMedicare");

				}
				
				if (practicecheckResponse != null && practicecheckResponse.toString().startsWith("[")) {

				} else if (practicecheckResponse != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					Practice practice = gson2.fromJson(jo.get("Practice"), Practice.class);
					if (practice != null) {
						practices.add(practice);
					}
					jo.remove("Practice");

				}
				
				if (hospitalResponseCheck != null && hospitalResponseCheck.toString().startsWith("[")) {

				} else if (hospitalResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					CAQHHospital cAQHHospital = gson2.fromJson(jo.get("Hospital"), CAQHHospital.class);
					if (cAQHHospital != null) {
						caqhHospitals.add(cAQHHospital);
					}
					jo.remove("Hospital");

				}

				if (providerLicenseResponseCheck != null && providerLicenseResponseCheck.toString().startsWith("[")) {

				} else if (providerLicenseResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					ProviderLicense providerLicense = gson2.fromJson(jo.get("ProviderLicense"), ProviderLicense.class);
					if (providerLicense != null) {
						providerLicenses.add(providerLicense);
					}
					jo.remove("ProviderLicense");

				}

				if (specialtyResponseCheck != null && specialtyResponseCheck.toString().startsWith("[")) {

				} else if (specialtyResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					Specialty specialty = gson2.fromJson(jo.get("Specialty"), Specialty.class);
					if (specialty != null) {
						specialties.add(specialty);
					}
					jo.remove("Specialty");

				}
				
				
				if (workHistoryResponseCheck != null && workHistoryResponseCheck.toString().startsWith("[")) {

				} else if (workHistoryResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					WorkHistory workHistory = gson2.fromJson(jo.get("WorkHistory"), WorkHistory.class);
					if (workHistory != null) {
						workHistories.add(workHistory);
					}
					jo.remove("WorkHistory");

				}
				if (educationResponseCheck != null && educationResponseCheck.toString().startsWith("[")) {

				} else if (educationResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					Education education = gson2.fromJson(jo.get("Education"), Education.class);
					if (education != null) {
						educations.add(education);
					}
					jo.remove("Education");
				}
				if (degreeResponseCheck != null && degreeResponseCheck.toString().startsWith("[")) {

				} else if (degreeResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					Degree degree = gson2.fromJson(jo.get("Degree"), Degree.class);
					if (degree != null) {
						degrees.add(degree);
					}
					jo.remove("Degree");
				}
				if (insuranceResponseCheck != null && insuranceResponseCheck.toString().startsWith("[")) {

				} else if (insuranceResponseCheck != null) {

					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					Insurance insurance = gson2.fromJson(jo.get("Insurance"), Insurance.class);
					if (insurance != null) {
						insurances.add(insurance);
					}
					jo.remove("Insurance");

				}
				if (timeGapResponseCheck != null && timeGapResponseCheck.toString().startsWith("[")) {

				} else if (timeGapResponseCheck != null) {
					Gson gson2 = new Gson();
					gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
					TimeGap timeGap = gson2.fromJson(jo.get("TimeGap"), TimeGap.class);
					if (timeGap != null) {
						timeGaps.add(timeGap);
					}
					jo.remove("TimeGap");
				}
				
				

				/**
				 * Map JSON to CAQHProvider
				 */
				caqhProvider = gson.fromJson(jo, CAQHProvider.class);
				
				
				
				
				if (caqhProvider.getProviderAddress() == null || caqhProvider.getProviderAddress().isEmpty()) {
					caqhProvider.setProviderAddress(providerAddresses);
				}
				
				if (caqhProvider.getProviderCDS() == null || caqhProvider.getProviderCDS().isEmpty()) {
					caqhProvider.setProviderCDS(providerCDSes);
				}

				if (caqhProvider.getProviderDEA() == null || caqhProvider.getProviderDEA().isEmpty()) {
					caqhProvider.setProviderDEA(providerDeas);
				}
				
				if (caqhProvider.getProviderMedicaid() == null || caqhProvider.getProviderMedicaid().isEmpty()) {
					caqhProvider.setProviderMedicaid(providerMedicaids);
				}
				
				if (caqhProvider.getProviderMedicare() == null || caqhProvider.getProviderMedicare().isEmpty()) {
					caqhProvider.setProviderMedicare(providerMedicares);
				}
				
				if (caqhProvider.getPractice() == null || caqhProvider.getPractice().isEmpty()) {
					caqhProvider.setPractice(practices);
				}
				
				if (caqhProvider.getHospital() == null || caqhProvider.getHospital().isEmpty()) {
					caqhProvider.setHospital(caqhHospitals);
				}

				if (caqhProvider.getProviderLicense() == null || caqhProvider.getProviderLicense().isEmpty()) {
					caqhProvider.setProviderLicense(providerLicenses);
				}
				if (caqhProvider.getWorkHistory() == null || caqhProvider.getWorkHistory().isEmpty()) {
					caqhProvider.setWorkHistory(workHistories);
				}
				if (caqhProvider.getEducation() == null || caqhProvider.getEducation().isEmpty()) {
					caqhProvider.setEducation(educations);
				}
				if (caqhProvider.getDegree() == null || caqhProvider.getDegree().isEmpty()) {
					caqhProvider.setDegree(degrees);
				}
				if (caqhProvider.getInsurance() == null || caqhProvider.getInsurance().isEmpty()) {
					caqhProvider.setInsurance(insurances);
				}
				if (caqhProvider.getTimeGap() == null || caqhProvider.getTimeGap().isEmpty()) {
					caqhProvider.setTimeGap(timeGaps);
				}
				if (caqhProvider.getSpecialty() == null || caqhProvider.getSpecialty().isEmpty()) {
					caqhProvider.setSpecialty(specialties);
				}
				if (!caqhProvider.getSpecialty().isEmpty()) {
					for (Specialty spec : caqhProvider.getSpecialty()) {
						if (spec.getSpecialty() != null) {
							spec.setSpecialtyName(spec.getSpecialty().getSpecialtyName());
						}
					}
				}

				provider.setFirstName(caqhProvider.getFirstName());
				provider.setLastName(caqhProvider.getLastName());
				provider.setMiddleName(caqhProvider.getMiddleName());
				provider.setDob(caqhProvider.getBirthDate());
				provider.setBirthCity(caqhProvider.getBirthCity());
				provider.setBirthStateCode(caqhProvider.getBirthState());
				provider.setBirthStateName(null);
				if (caqhProvider.getBirthCountry() != null) {
					provider.setBirthCountry(caqhProvider.getBirthCountry().getCountryName());
				}else {
					provider.setBirthCountry("United States");
				}
				provider.setSsn(String.valueOf(caqhProvider.getSSN()));
				
				if(caqhProvider.getCitizenshipStatus()!=null) {
					if(caqhProvider.getCitizenshipStatus().equalsIgnoreCase("United States")) {
						provider.setUsCitizen(1);
					}
				}
				if (caqhProvider.getGender() != null) {
					provider.setGender(caqhProvider.getGender().getGenderDescription());
				}
				
				StringBuilder stringBuilder=new StringBuilder();
				
				if (!helperExtension.isNullOrEmpty(provider.getFirstName())) {
					stringBuilder.append(provider.getFirstName());
				} 
				
				if (!helperExtension.isNullOrEmpty(provider.getLastName())) {
					stringBuilder.append(provider.getLastName());
				} 
				
				if(!helperExtension.isNullOrEmpty(provider.getPhone())){
					stringBuilder.append(provider.getPhone());
				}
				
				if(!helperExtension.isNullOrEmpty(provider.getEmail())) {
					stringBuilder.append(provider.getEmail());
				}
				
				if(!helperExtension.isNullOrEmpty(provider.getNpi())) {
					stringBuilder.append(provider.getNpi());
				}
				
				provider.setTag(stringBuilder.toString());
				
				
				
				
				caqhProvider.setProvider(provider);
				
				try {
					
					if (caqhProvider.getProviderAddress() != null && caqhProvider.getProviderAddress().size() > 0) {
						providerAddressJPARepo.deleteByProvider_Uuid(providerUUID);
						for (ProviderAddresses add : caqhProvider.getProviderAddress()) {
							ProviderAddress providerAddress = new ProviderAddress();
							providerAddress.setProvider(provider);
							providerAddress.setAddressLine1(add.getAddress());
							providerAddress.setCity(add.getCity());
							providerAddress.setStateCode(add.getState());
							if(!helperExtension.isNullOrEmpty(add.getState())) {
								providerAddress.setStateName(stateJPARepo.getStateNameFromStateCode(add.getState()));								
							}
							providerAddress.setZipcode(add.getPostalCode());
							providerAddress.setCountry("United State");
							providerAddress.setCounty(add.getCounty());
							if (providerAddressJPARepo.existsByProvider_UuidAndIsFlag(providerUUID, 1)) {
								providerAddress.setType("Secondary");
							} else {
								providerAddress.setType("Primary");
							}
							providerAddressJPARepo.save(providerAddress);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getProviderCDS() != null && caqhProvider.getProviderCDS().size() > 0) {
						
						for (ProviderCDSCaqh cds : caqhProvider.getProviderCDS()) {
							
							if (!providerCdsJpaRepository.existsByCdsNumberAndProvider_Uuid(cds.getCDSNumber(), provider.getUuid())) {
								
								ProviderCds providerCds = new ProviderCds();
								providerCds.setProvider(provider);
								providerCds.setCdsNumber(cds.getCDSNumber());
								providerCds.setExpirationDate(cds.getExpirationDate());
								providerCds.setIssueDate(cds.getIssueDate());
								providerCds.setStateCode(cds.getState());
								if(!helperExtension.isNullOrEmpty(cds.getState())) {
									providerCds.setStateName(stateJPARepo.getStateNameFromStateCode(cds.getState()));								
								}
								providerCdsJpaRepository.save(providerCds);
							}
							
						}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getProviderDEA() != null && caqhProvider.getProviderDEA().size() > 0) {
						
						for (ProviderDEACaqh providerLicense : caqhProvider.getProviderDEA()) {
							
							if (!providerDeaJPARepo.existsByDeaNumAndProvider_Uuid(providerLicense.getDEANumber(), provider.getUuid())) {
								ProviderDea dea = new ProviderDea();
								dea.setDeaNum(providerLicense.getDEANumber());
								dea.setStateCode(providerLicense.getState());
								if(!helperExtension.isNullOrEmpty(providerLicense.getState())) {
									dea.setStateName(stateJPARepo.getStateNameFromStateCode(providerLicense.getState()));								
								}
								dea.setIssueDate(providerLicense.getIssueDate());
								dea.setExpirationDate(providerLicense.getExpirationDate());
								dea.setProvider(provider);
								providerDeaJPARepo.save(dea);
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getProviderMedicaid() != null && caqhProvider.getProviderMedicaid().size() > 0) {
						
						for (ProviderMedicaidCaqh providerMedicaid : caqhProvider.getProviderMedicaid()) {
							
							if (!providerMedicaidJpaRepo.existsByMedicaidIdAndProvider_Uuid(providerMedicaid.getMedicaidNumber(), provider.getUuid())) {
								ProviderMedicaid medicaid = new ProviderMedicaid();
								medicaid.setProvider(provider);
								medicaid.setMedicaidId(providerMedicaid.getMedicaidNumber());
								medicaid.setStateCode(providerMedicaid.getState());
								if(!helperExtension.isNullOrEmpty(providerMedicaid.getState())) {
									medicaid.setStateName(stateJPARepo.getStateNameFromStateCode(providerMedicaid.getState()));								
								}
								
								providerMedicaidJpaRepo.save(medicaid);
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getProviderMedicare() != null && caqhProvider.getProviderMedicare().size() > 0) {
						
						for (ProviderMedicareCaqh medicare : caqhProvider.getProviderMedicare()) {
							if (!providerMedicareJpaRepo.existsByMedicarePtanAndProvider_Uuid(medicare.getMedicareNumber(), provider.getUuid())) {
								ProviderMedicare care = new ProviderMedicare();
								care.setProvider(provider);
								care.setMedicarePtan(medicare.getMedicareNumber());
								care.setStateCode(medicare.getState());
								if(!helperExtension.isNullOrEmpty(medicare.getState())) {
									care.setStateName(stateJPARepo.getStateNameFromStateCode(medicare.getState()));								
								}
								
								providerMedicareJpaRepo.save(care);
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getPractice() != null && caqhProvider.getPractice().size() > 0) {
						
						practiceDetailJpaRepo.deleteByProvider_Uuid(providerUUID);
						
						for (Practice pr : caqhProvider.getPractice()) {
							
							ProviderPracticeDetail practiceDetails = new ProviderPracticeDetail();
							practiceDetails.setProvider(provider);
							practiceDetails.setAddressLine1(pr.getAddress());
							practiceDetails.setAddressLine2(pr.getAddress2());
							practiceDetails.setCity(pr.getCity());
							practiceDetails.setCounty(pr.getCounty());
							practiceDetails.setStateCode(pr.getState());
							if(!helperExtension.isNullOrEmpty(pr.getState())) {
								practiceDetails.setStateName(stateJPARepo.getStateNameFromStateCode(pr.getState()));								
							}
							practiceDetails.setZipcode(pr.getZip());
							practiceDetails.setZipCodeExtension(pr.getExtZip());
							practiceDetails.setCountry("United State");
							
							if(pr.getAddressType().getAddressTypeDescription()!=null) {
								
								if(pr.getAddressType().getAddressTypeDescription().equalsIgnoreCase("Primary Practice")) {
									practiceDetails.setIsPrimaryPractice(1);
								}
							}
							
							
							practiceDetails.setPhoneExtension("1");
							practiceDetails.setPracticePhoneNumber(pr.getPhoneNumber());
							
							practiceDetails.setPracticefaxNumber(pr.getFaxNumber());	
							practiceDetails.setPracticeLocationEmail(pr.getEmailAddress());
							practiceDetails.setPracticeLocationName(pr.getAddress());
							practiceDetails.setPracticeLocationWebsite(pr.getPracticeWebsite());
							
							practiceDetails.setAffiliationDescription(pr.getAffliationDescription());
							practiceDetails.setProviderStartDate(pr.getStartDate());
							
//							if(pr.getLimitation()!=null) {
//								
//								practiceDetails.setIsAgeLimitation(pr.getLimitation().get(0).getAgeLimitationFlag());
//								
//								if(!pr.getLimitation().get(0).getGenderLimitation().getGenderLimitationDescription().equalsIgnoreCase("Not Applicable")) {
//									practiceDetails.setIsGenderLimitation(1);								
//								}
//								
//							}
							
							if(pr.getElectronicBillingFlag()==1) {
								practiceDetails.setOffersElectronicBilling(1);
							}
							
							practiceDetailJpaRepo.save(practiceDetails);
							
						}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getSpecialty() != null && caqhProvider.getSpecialty().size() > 0) {
						
						ProviderSpeciality providerPrimarySpecialtyEntity = providerSpecialityJPARepo.findTop1ByProvider_UuidAndTypeIgnoreCaseAndIsFlag(providerUUID, "Primary", 1);
						
						if (providerPrimarySpecialtyEntity == null) {	
							providerPrimarySpecialtyEntity = new ProviderSpeciality();
							providerPrimarySpecialtyEntity.setType("Primary");
							providerPrimarySpecialtyEntity.setIsPrimary(1);
							providerPrimarySpecialtyEntity.setProvider(provider);
						}
						
						providerPrimarySpecialtyEntity.setCode(caqhProvider.getSpecialty().get(0).getNUCCTaxonomyCode());
						providerPrimarySpecialtyEntity.setDescription(caqhProvider.getSpecialty().get(0).getSpecialtyName());
						providerPrimarySpecialtyEntity.setBoardCertified(caqhProvider.getSpecialty().get(0).getBoardCertifiedFlag());
						providerPrimarySpecialtyEntity.setNameOfCertifyingBoard(caqhProvider.getSpecialty().get(0).getSpecialtyBoardName());
						providerPrimarySpecialtyEntity.setInitialCertificateDate(caqhProvider.getSpecialty().get(0).getCertificationDate());
						providerPrimarySpecialtyEntity.setLastRecertificationDate(caqhProvider.getSpecialty().get(0).getRecertificationDate());
						
						providerSpecialityJPARepo.save(providerPrimarySpecialtyEntity);
						
					}
					if (caqhProvider.getSpecialty() != null && caqhProvider.getSpecialty().size() > 1) {
						ProviderSpeciality providerSecondarySpecialtyEntity = providerSpecialityJPARepo.findTop1ByProvider_UuidAndTypeIgnoreCaseAndIsFlag(providerUUID, "Secondary", 1);
						
						if (providerSecondarySpecialtyEntity == null) {
							providerSecondarySpecialtyEntity = new ProviderSpeciality();
							providerSecondarySpecialtyEntity.setProvider(provider);
							providerSecondarySpecialtyEntity.setType("Secondary");	
						}
						
						providerSecondarySpecialtyEntity.setCode(caqhProvider.getSpecialty().get(1).getNUCCTaxonomyCode());
						providerSecondarySpecialtyEntity.setDescription(caqhProvider.getSpecialty().get(1).getSpecialtyName());
						providerSecondarySpecialtyEntity.setBoardCertified(caqhProvider.getSpecialty().get(1).getBoardCertifiedFlag());
						providerSecondarySpecialtyEntity.setNameOfCertifyingBoard(caqhProvider.getSpecialty().get(1).getSpecialtyBoardName());
						providerSecondarySpecialtyEntity.setInitialCertificateDate(caqhProvider.getSpecialty().get(1).getCertificationDate());
						providerSecondarySpecialtyEntity.setLastRecertificationDate(caqhProvider.getSpecialty().get(1).getRecertificationDate());
						
						providerSpecialityJPARepo.save(providerSecondarySpecialtyEntity);
						
					}
					if (caqhProvider.getSpecialty() != null && caqhProvider.getSpecialty().size() > 2) {
						ProviderSpeciality providerSecondarySpecialtyEntity = providerSpecialityJPARepo.findTop1ByProvider_UuidAndTypeIgnoreCaseAndIsFlag(providerUUID, "Additional", 1);
						
						if (providerSecondarySpecialtyEntity == null) {
							providerSecondarySpecialtyEntity = new ProviderSpeciality();
							providerSecondarySpecialtyEntity.setProvider(provider);
							providerSecondarySpecialtyEntity.setType("Additional");
						}
						
						providerSecondarySpecialtyEntity.setCode(caqhProvider.getSpecialty().get(2).getNUCCTaxonomyCode());
						providerSecondarySpecialtyEntity.setDescription(caqhProvider.getSpecialty().get(2).getSpecialtyName());
						providerSecondarySpecialtyEntity.setBoardCertified(caqhProvider.getSpecialty().get(2).getBoardCertifiedFlag());
						providerSecondarySpecialtyEntity.setNameOfCertifyingBoard(caqhProvider.getSpecialty().get(2).getSpecialtyBoardName());
						providerSecondarySpecialtyEntity.setInitialCertificateDate(caqhProvider.getSpecialty().get(2).getCertificationDate());
						providerSecondarySpecialtyEntity.setLastRecertificationDate(caqhProvider.getSpecialty().get(2).getRecertificationDate());
						
						providerSpecialityJPARepo.save(providerSecondarySpecialtyEntity);
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				

				try {
					
					if (caqhProvider.getProviderCertification() != null
							&& caqhProvider.getProviderCertification().size() > 0) {
						ProviderCertification providerCertification = providerCertificationJpaRepository
								.findTop1ByProvider_Uuid(provider.getUuid());
						
						if (providerCertification == null) {
							providerCertification = new ProviderCertification();
						}
						providerCertification.setCreatedDate(new Date());
						if (caqhProvider.getProviderCertification().get(0).getCertificationFlag() == 1) {
							providerCertification.setIsCpr(1);
						}
						if (caqhProvider.getProviderCertification().get(1).getCertificationFlag() == 1) {
							providerCertification.setIsBls(1);
						}
						if (caqhProvider.getProviderCertification().get(2).getCertificationFlag() == 1) {
							providerCertification.setIsAcls(1);
						}
						if (caqhProvider.getProviderCertification().get(3).getCertificationFlag() == 1) {
							providerCertification.setIsAlso(1);
						}
						if (caqhProvider.getProviderCertification().get(4).getCertificationFlag() == 1) {
							providerCertification.setIsCorec(1);
						}
						if (caqhProvider.getProviderCertification().get(5).getCertificationFlag() == 1) {
							providerCertification.setIsNals(1);
						}
						if (caqhProvider.getProviderCertification().get(6).getCertificationFlag() == 1) {
							providerCertification.setIsAtls(1);
						}
						if (caqhProvider.getProviderCertification().get(7).getCertificationFlag() == 1) {
							providerCertification.setIsNrp(1);
						}
						if (caqhProvider.getProviderCertification().get(8).getCertificationFlag() == 1) {
							providerCertification.setIsPals(1);
						}
						
						providerCertificationJpaRepository.save(providerCertification);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				try {
					
					if (caqhProvider.getInsurance() != null && caqhProvider.getInsurance().size() > 0) {
						ProviderInsuranceInformation providerInsuranceInformation = providerInsuranceJpaRepo
								.findTop1ByProvider_Uuid(providerUUID);
						
						if (providerInsuranceInformation == null) {
							providerInsuranceInformation = new ProviderInsuranceInformation();
							providerInsuranceInformation.setProvider(provider);
						}
						
						providerInsuranceInformation.setCreatedDate(new Date());
						providerInsuranceInformation.setPolicyNumber(caqhProvider.getInsurance().get(0).getPolicyNumber());
						providerInsuranceInformation
						.setCurrentEffectiveDate(caqhProvider.getInsurance().get(0).getStartDate());
						providerInsuranceInformation
						.setCurrentExpirationDate(caqhProvider.getInsurance().get(0).getEndDate());
						providerInsuranceInformation
						.setCarrierOrSelfIsuredName(caqhProvider.getInsurance().get(0).getInsuranceCarrierName());
						providerInsuranceInformation.setAddressLine1(caqhProvider.getInsurance().get(0).getAddress());
						providerInsuranceInformation.setCity(caqhProvider.getInsurance().get(0).getCity());
						providerInsuranceInformation.setStateCode(caqhProvider.getInsurance().get(0).getState());
						if(!helperExtension.isNullOrEmpty(caqhProvider.getInsurance().get(0).getState())) {
							providerInsuranceInformation.setStateName(stateJPARepo.getStateNameFromStateCode(caqhProvider.getInsurance().get(0).getState()));								
						}
						providerInsuranceInformation.setZipcode(caqhProvider.getInsurance().get(0).getPostalCode());
						if (caqhProvider.getInsurance().get(0).getCountry() != null) {
							providerInsuranceInformation
							.setCountry(caqhProvider.getInsurance().get(0).getCountry().getCountryName());
						}
						
						providerInsuranceInformation.setPolicyNumber(caqhProvider.getInsurance().get(0).getPolicyNumber());
						providerInsuranceInformation
						.setHaveUnlimitedCoverage(caqhProvider.getInsurance().get(0).getUnlimitedCoverageFlag());
						if (caqhProvider.getInsurance().get(0).getInsuranceCoverageType() != null) {
							providerInsuranceInformation.setTypeOfCoverage(caqhProvider.getInsurance().get(0)
									.getInsuranceCoverageType().getInsuranceCoverageTypeDescription());
						}
						
						providerInsuranceInformation.setCoverageAmountPerOccurance(
								String.valueOf(caqhProvider.getInsurance().get(0).getCoverageAmountOccurrence()));
						providerInsuranceInformation.setAggregateCoverageAmount(
								String.valueOf(caqhProvider.getInsurance().get(0).getCoverageAmountAggregate()));
						providerInsuranceInformation
						.setIndividualCoverage(caqhProvider.getInsurance().get(0).getIndividualCoverageFlag());
						providerInsuranceJpaRepo.save(providerInsuranceInformation);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				
				try {
					
					if (caqhProvider.getEducation() != null && caqhProvider.getEducation().size() > 0) {
						for (Education education : caqhProvider.getEducation()) {
							if (education.getEducationTypeName() != null) {
								
								if (education.getEducationTypeName().equalsIgnoreCase("Professional School")) {
									ProviderProfessionalSchoolInfo providerEducationalInfo = new ProviderProfessionalSchoolInfo();
									providerEducationalInfo.setProfessionalSchool(education.getInstitutionName());
									if (education.getDegree() != null) {
										providerEducationalInfo.setDegree(education.getDegree().getDegreeAbbreviation());
									}
									providerEducationalInfo.setCreatedDate(new Date());
									providerEducationalInfo.setProfessionalSchoolStartDate(education.getStartDate());
									providerEducationalInfo.setProfessionalSchoolEndDate(education.getEndDate());
									providerEducationalInfo.setGraduationDate(education.getCompletionDate());
									providerEducationalInfo.setStateCode(education.getState());
									if(!helperExtension.isNullOrEmpty(education.getState())) {
										providerEducationalInfo.setStateName(stateJPARepo.getStateNameFromStateCode(education.getState()));								
									}
									if (education.getCountry() != null) {
										providerEducationalInfo.setCountry(education.getCountry().getCountryName());
									}
									providerEducationalInfo.setProvider(provider);
									providerProfessionalSchoolInfoJPARepo.save(providerEducationalInfo);
									
								} else if (education.getEducationTypeName().equalsIgnoreCase("Undergraduate School")) {
									ProviderUndergraduateEducation providerEducationalInfo = new ProviderUndergraduateEducation();
									providerEducationalInfo.setUndergraduateSchool(education.getInstitutionName());
									if (education.getDegree() != null) {
										providerEducationalInfo.setDegree(education.getDegree().getDegreeAbbreviation());
									}
									providerEducationalInfo.setCreatedDate(new Date());
									
									providerEducationalInfo.setUndergraduateSchoolStartDate(education.getStartDate());
									providerEducationalInfo.setUndergraduateSchoolEndDate(education.getEndDate());
									providerEducationalInfo.setUndergraduateSchoolStartDate(education.getStartDate());
									providerEducationalInfo.setStateCode(education.getState());
									if(!helperExtension.isNullOrEmpty(education.getState())) {
										providerEducationalInfo.setStateName(stateJPARepo.getStateNameFromStateCode(education.getState()));								
									}
									if (education.getCountry() != null) {
										providerEducationalInfo.setCountry(education.getCountry().getCountryName());
									}
									providerEducationalInfo.setProvider(provider);
									providerUndergraduateEducationJPARepo.save(providerEducationalInfo);
									
								}
							}
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				
				try {
					
					if (caqhProvider.getWorkHistory() != null && caqhProvider.getWorkHistory().size() > 0) {
						for (WorkHistory workHistory : caqhProvider.getWorkHistory()) {
							
							EmploymentRecords employment = new EmploymentRecords();
							employment.setCreatedDate(new Date());
							employment.setUpdatedDate(new Date());
							employment.setEmployerName(workHistory.getEmployerName());
							employment.setAddress1(workHistory.getAddress());
							employment.setCountry(workHistory.getCountry().getCountryName());
							employment.setCity(workHistory.getCity());
							employment.setStateCode(workHistory.getState());
							if(!helperExtension.isNullOrEmpty(workHistory.getState())) {
								employment.setStateName(stateJPARepo.getStateNameFromStateCode(workHistory.getState()));								
							}
							employment.setZipcode(workHistory.getPostalCode());
							employment.setPhone(workHistory.getPhoneNumber());
							employment.setFax(workHistory.getFaxNumber());
							employment.setStartDate(workHistory.getStartDate());
							employment.setEndDate(workHistory.getEndDate());
							if (!helperExtension.isNullOrEmpty(workHistory.getExitExplanation())
									&& workHistory.getEndDate() != null) {
								employment.setIsCurrentEmployer(1);
							}
							employment.setDepartureReason(workHistory.getExitExplanation());
							employment.setProvider(provider);
							employmentRecordsJpaRepo.save(employment);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					if (caqhProvider.getProviderLicense() != null && caqhProvider.getProviderLicense().size() > 0) {

						for (ProviderLicense providerLicense : caqhProvider.getProviderLicense()) {
							boolean licExist = providerProfessionalLicenseJpaRepo.existsByLicenseNumberAndProvider_Uuid(
									providerLicense.getLicenseNumber(), provider.getUuid());
							if (!licExist) {
								ProviderProfessionalLicense license = new ProviderProfessionalLicense();
								license.setCreatedDate(new Date());
								license.setUpdatedDate(new Date());
								license.setLicenseNumber(providerLicense.getLicenseNumber());
								license.setStateCode(providerLicense.getState());
								if(!helperExtension.isNullOrEmpty(providerLicense.getState())) {
									license.setStateName(stateJPARepo.getStateNameFromStateCode(providerLicense.getState()));								
								}
								license.setIssueDate(providerLicense.getIssueDate());
								license.setExpirationDate(providerLicense.getExpirationDate());
								license.setLicenseType(providerLicense.getLicenseType());
								license.setProvider(provider);
								providerProfessionalLicenseJpaRepo.save(license);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					
					if (caqhProvider.getHospital() != null && caqhProvider.getHospital().size() > 0) {
						
						providerHospitalAffilationJpaRepo.deleteByProvider_Uuid(providerUUID);
						
						for (CAQHHospital hospital : caqhProvider.getHospital()) {
							
							ProviderHospitalAffiliation he = new ProviderHospitalAffiliation();
							
							he.setProvider(provider);
							
							if(hospital.getHospitalName()!=null) {
								if(hospital.getHospitalName().equalsIgnoreCase("Other")){
									he.setHospitalName(hospital.getNonAHAHospitalName());
									he.setAdmittingHospitalName(hospital.getNonAHAHospitalName());
								}else {
									he.setHospitalName(hospital.getHospitalName());
									he.setAdmittingHospitalName(hospital.getHospitalName());
								}
							}
							
							
							he.setStateCode(hospital.getState());
							if(!helperExtension.isNullOrEmpty(hospital.getState())) {
								he.setStateName(stateJPARepo.getStateNameFromStateCode(hospital.getState()));								
							}
							he.setStartDate(hospital.getStartDate());
							he.setEndDate(hospital.getEndDate());	
							
							
							if (hospital.getAdmittingArrangements() > 0) {
								
								he.setAdmittingArrangements(1);
								he.setAdmittingPrivilegeStatus(hospital.getStaffCategory().toLowerCase());
								he.setWhoAdmitsForYou(hospital.getWhoAdmitsForyou());
								he.setFirstName(hospital.getFirstName());
								he.setLastName(hospital.getLastName());
								he.setPhoneNumber(hospital.getPhoneNumber());
								he.setEmailAddress(hospital.getEmailAddress());
								he.setIsSameAsAdmittingProviderSpecialty(hospital.getIsProviderSpecialtySameAsYourSpecialty());
								he.setAdmittingArrangementDesc(hospital.getAdmittingArrangementDesc());
								he.setGroupName(hospital.getGroupName());
								he.setIndividualNpi(hospital.getIndividualNpi());
								he.setOrganisationNpi(hospital.getOrganisationNpi());
								
								
							} else if (hospital.getNonAdmittingAffiliations() > 0) {
								
								he.setNonAdmittingAffiliations(1);
								he.setNonAdmittingAffiliationDesc(hospital.getNonAdmittingAffiliationDesc());
								he.setNonAdmittingPrivilegeStatus(hospital.getStaffCategory().toLowerCase());
								
							} else {
								
								he.setAdmittingPrivileges(1);
								he.setAdmittingPrivilegeStatus(hospital.getStaffCategory().toLowerCase());
								he.setAdmittingPrivilegeType(hospital.getPrivilegeDescription().split(" ")[0].toLowerCase());
								if(hospital.getHospitalAffiliationType()!=null) {
									if(hospital.getHospitalAffiliationType().getHospitalAffiliationTypeDescription()!=null && hospital.getHospitalAffiliationType().getHospitalAffiliationTypeDescription().equalsIgnoreCase("Primary")) {
										he.setIsPrimaryHospital(1);
									}	
								}
								
							}
							
							providerHospitalAffilationJpaRepo.save(he);
							
						}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				

				
				
				try {
					if (caqhProviderDetail.containsKey("Disclosure")) {
						JsonArray disclosures = caqhProviderDetail.getJsonArray("Disclosure");
						for (int i = 0; i < disclosures.size(); i++) {
							JsonObject obj = disclosures.getJsonObject(i);
							if (obj.getJsonNumber("DisclosureAnswerFlag") != null) {

								int disclosureAnswerFlag = obj.getJsonNumber("DisclosureAnswerFlag").intValue();
								if (disclosureAnswerFlag == 1) {
									JsonObject questionObject = obj.getJsonObject("DisclosureQuestion");

									if (questionObject.containsKey("DisclosureSummary")) {
										String questionString = questionObject.getString("DisclosureSummary");
//										DisclosureQuestion disclosureQuestion = disclosureQuestionRepositpory
//												.findByQuestion(questionString);
//										if (disclosureQuestion != null) {
//											DisclosureAnswer disclosureAnswer = new DisclosureAnswer();
//											JsonString answer = obj.getJsonString("DisclosureExplanation");
//											disclosureAnswer.setAnswer(answer.getString());
//											disclosureAnswer.setProvider(provider);
//											disclosureAnswer.setDisclosureQuestion(disclosureQuestion);
//											disclosureAnswerRepositpory.save(disclosureAnswer);
//										}
									}

								}
							}

						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					log.error("error while saving Disclosure Data for provider Id: " + provider.getUuid() + " "
							+ e.getMessage());
				}
				

				/**
				 * save provider profile data fetched from CAQH.
				 */
				providerJpaRepo.save(provider);
				
			}

		} catch (Exception e) {
			CAQHRosterBatch cAQHRosterBatch = cAQHRosterBatchRepository.findTop1ByProvider_Uuid(providerUUID);
			cAQHRosterBatch.setStatus("Exception : " + e.getMessage());
			cAQHRosterBatchRepository.save(cAQHRosterBatch);
			e.printStackTrace();

		}
	}
	
	
	/**
	 * @author D K Bind
	 * @param providerUUID
	 * @return
	 */
	public ResponseEntity<Object> getCAQHStatus(UUID providerUUID) {
		
		CAQHRosterBatch cAQHRosterBatch = cAQHRosterBatchRepository.findTop1ByProvider_Uuid(providerUUID);
		
		if(cAQHRosterBatch==null) {
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, "CAQH status not found.") , HttpStatus.OK);
		}
		return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, cAQHRosterBatch.getStatus()), HttpStatus.OK);
	}
	
	
	
	
	
	
	/********************************** Testing Code Section Start ******************************/
	
	
	
	
	
	
	public void reUploadCaqhData() {
		
		List<Provider> providers = providerJpaRepo.findAll();

		CompletableFuture.runAsync(() -> {
			for (Provider provider : providers) {
				try {
					queryProviderWithNPI(provider.getNpi(), provider.getUuid());
				} catch (Exception e) {
					// TODO: Change this
					System.out.println(
							"Exception at CAQHHandler reUploadData() for provider UUID : " + provider.getUuid());
//					e.printStackTrace();
				}
			}
		});

	}

	
	public ResponseEntity<Object> fetchProfile(UUID providerUUID) {
		
		try {
		
			if(helperExtension.isNullOrEmpty(providerUUID)) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider uuid is empty."), HttpStatus.OK);
			}
			
			CAQHRosterBatch cAQHRosterBatch = cAQHRosterBatchRepository.findTop1ByProvider_Uuid(providerUUID);
			
			if (cAQHRosterBatch.getStatus().equalsIgnoreCase("Complete") || cAQHRosterBatch.getStatus().equalsIgnoreCase("Saved")) {
				return getProviderCaqhData(providerUUID);
			} else if (cAQHRosterBatch.getStatus().equalsIgnoreCase("In_Process") || cAQHRosterBatch.getStatus().equalsIgnoreCase("Inprocess")) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, "Profile fetch is in progress", cAQHRosterBatch), HttpStatus.OK);
			} else {
				return getProviderCaqhData(providerUUID);
			}
		} catch (Exception e) {
			return null;
		}
	}
	

	public ResponseEntity<Object> getProviderCaqhData(UUID providerUUID) {
		Provider provider = providerJpaRepo.findByUuid(providerUUID);
		ProviderCaqhInfo caqh = providerCaqhInfoJPARepo.findTop1ByProvider_Uuid(providerUUID);
		String url = "https://proview.caqh.org/credentialingapi/api/v7/entities?";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("caqhProviderId", caqh.getCaqhId()));
		params.add(new BasicNameValuePair("attestationDate", "10/02/2021"));
		params.add(new BasicNameValuePair("organizationId", "1561"));
		url += URLEncodedUtils.format(params, "UTF-8");
		// setup HTTP Auth
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("bikhamsaas", "Bikham@1991_");
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				if (response.getStatusLine().getStatusCode() == 295) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "No Provider Attestation data found"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 294) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider is not current and complete"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 293) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider has not given authorization"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 292) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider is not active on the roster"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 204) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Provider/Organization (ID) not found"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 400) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "Missing required parameter like -> Organization ID/CAQH Provider ID/Attestation date."), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 401) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Authentication failed.'}"), HttpStatus.OK);
				} else if (response.getStatusLine().getStatusCode() == 500) {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Internal error occurred.  Please try again later'}"), HttpStatus.OK);
				} else {
					return new ResponseEntity<>(new ResponseEntityObject<>(false, "{'error':'Internal error occurred.  Please try again later'}"), HttpStatus.OK);
				}
			}
			String responseString = null;
			try {
				responseString = EntityUtils.toString(entity);
			} catch(Exception e) {
				
			}
			
			JSONObject jsonObject = null;
			try {
				jsonObject = XML.toJSONObject(responseString);
			} catch(Exception e) {
				
			}
			
			JsonReader jsonReader = Json.createReader(new StringReader(jsonObject.toString()));
			JsonObject json = jsonReader.readObject();
			JsonObject caqhProviderDetail = json.getJsonObject("Provider");

			CAQHProvider caqhProvider = new CAQHProvider();
			Gson gson = new Gson();
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

			JsonElement je = new Gson().fromJson(caqhProviderDetail.toString(), JsonElement.class);
			com.google.gson.JsonObject jo = je.getAsJsonObject();
			Object providerLicenseResponseCheck = caqhProviderDetail.get("ProviderLicense");
			Object practicecheckResponse = caqhProviderDetail.get("Practice");
			Object timeGapResponseCheck = caqhProviderDetail.get("TimeGap");
			Object workHistoryResponseCheck = caqhProviderDetail.get("WorkHistory");
			Object educationResponseCheck = caqhProviderDetail.get("Education");
			Object degreeResponseCheck = caqhProviderDetail.get("Degree");
			Object specialtyResponseCheck = caqhProviderDetail.get("Specialty");
			Object insuranceResponseCheck = caqhProviderDetail.get("Insurance");
			Object hospitalResponseCheck = caqhProviderDetail.get("Hospital");
			Object providerAddressResponseCheck = caqhProviderDetail.get("ProviderAddress");
			Object cdsResponseCheck = caqhProviderDetail.get("ProviderCDS");
			Object deaResponseCheck = caqhProviderDetail.get("ProviderDEA");
			Object providerMedicaidCheck = caqhProviderDetail.get("ProviderMedicaid");
			Object providerMedicareCheck = caqhProviderDetail.get("ProviderMedicare");
			List<Practice> practices = new ArrayList<>();
			List<Specialty> specialties = new ArrayList<>();
			List<WorkHistory> workHistories = new ArrayList<>();
			List<Education> educations = new ArrayList<>();
			List<Insurance> insurances = new ArrayList<>();
			List<Degree> degrees = new ArrayList<>();
			List<TimeGap> timeGaps = new ArrayList<>();
			List<ProviderLicense> providerLicenses = new ArrayList<>();
			List<CAQHHospital> caqhHospitals = new ArrayList<>();
			List<ProviderAddress> providerAddresses = new ArrayList<>();
			List<ProviderCDSCaqh> providerCDSes = new ArrayList<>();
			List<ProviderDEACaqh> providerDeas = new ArrayList<>();
			List<ProviderMedicaidCaqh> providerMedicaids = new ArrayList<>();
			List<ProviderMedicareCaqh> providerMedicares = new ArrayList<>();

			if (providerAddressResponseCheck != null && providerAddressResponseCheck.toString().startsWith("[")) {

			} else if (providerAddressResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderAddress providerAddress = gson2.fromJson(jo.get("ProviderAddress"), ProviderAddress.class);
				if (providerAddress != null) {
					providerAddresses.add(providerAddress);
				}
				jo.remove("ProviderAddress");

			}
			
			if (cdsResponseCheck != null && cdsResponseCheck.toString().startsWith("[")) {

			} else if (cdsResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderCDSCaqh providerCDS = gson2.fromJson(jo.get("ProviderCDS"), ProviderCDSCaqh.class);
				if (providerCDS != null) {
					providerCDSes.add(providerCDS);
				}
				jo.remove("ProviderCDS");

			}
			
			if (deaResponseCheck != null && deaResponseCheck.toString().startsWith("[")) {

			} else if (deaResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderDEACaqh providerDEA = gson2.fromJson(jo.get("ProviderDEA"), ProviderDEACaqh.class);
				if (providerDEA != null) {
					providerDeas.add(providerDEA);
				}
				jo.remove("ProviderDEA");

			}
			
			if (providerMedicaidCheck != null && providerMedicaidCheck.toString().startsWith("[")) {

			} else if (providerMedicaidCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderMedicaidCaqh providerMedicaidCaqh = gson2.fromJson(jo.get("ProviderMedicaid"), ProviderMedicaidCaqh.class);
				if (providerMedicaidCaqh != null) {
					providerMedicaids.add(providerMedicaidCaqh);
				}
				jo.remove("ProviderMedicaid");

			}

			
			if (providerMedicareCheck != null && providerMedicareCheck.toString().startsWith("[")) {

			} else if (providerMedicareCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderMedicareCaqh providerMedicare = gson2.fromJson(jo.get("ProviderMedicare"), ProviderMedicareCaqh.class);
				if (providerMedicare != null) {
					providerMedicares.add(providerMedicare);
				}
				jo.remove("ProviderMedicare");

			}

			if (providerLicenseResponseCheck != null && providerLicenseResponseCheck.toString().startsWith("[")) {

			} else if (providerLicenseResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				ProviderLicense providerLicense = gson2.fromJson(jo.get("ProviderLicense"), ProviderLicense.class);
				if (providerLicense != null) {
					providerLicenses.add(providerLicense);
				}
				jo.remove("ProviderLicense");

			}

			if (specialtyResponseCheck != null && specialtyResponseCheck.toString().startsWith("[")) {

			} else if (specialtyResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				Specialty specialty = gson2.fromJson(jo.get("Specialty"), Specialty.class);
				if (specialty != null) {
					specialties.add(specialty);
				}
				jo.remove("Specialty");

			}

			if (practicecheckResponse != null && practicecheckResponse.toString().startsWith("[")) {

			} else if (practicecheckResponse != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				
//				String jsonInString = new Gson().toJson(practicecheckResponse);
//				JSONObject mJSONObject = new JSONObject(jsonInString);
				
				Practice practice = gson2.fromJson(jo.get("Practice"), Practice.class);
				if (practice != null) {
					practices.add(practice);
				}
				jo.remove("Practice");

			}
			
			if (hospitalResponseCheck != null && hospitalResponseCheck.toString().startsWith("[")) {

			} else if (hospitalResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				CAQHHospital cAQHHospital = gson2.fromJson(jo.get("Hospital"), CAQHHospital.class);
				if (cAQHHospital != null) {
					caqhHospitals.add(cAQHHospital);
				}
				jo.remove("Hospital");

			}
			
			if (workHistoryResponseCheck != null && workHistoryResponseCheck.toString().startsWith("[")) {

			} else if (workHistoryResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				WorkHistory workHistory = gson2.fromJson(jo.get("WorkHistory"), WorkHistory.class);
				if (workHistory != null) {
					workHistories.add(workHistory);
				}
				jo.remove("WorkHistory");

			}
			if (educationResponseCheck != null && educationResponseCheck.toString().startsWith("[")) {

			} else if (educationResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				Education education = gson2.fromJson(jo.get("Education"), Education.class);
				if (education != null) {
					educations.add(education);
				}
				jo.remove("Education");
			}
			if (degreeResponseCheck != null && degreeResponseCheck.toString().startsWith("[")) {

			} else if (degreeResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				Degree degree = gson2.fromJson(jo.get("Degree"), Degree.class);
				if (degree != null) {
					degrees.add(degree);
				}
				jo.remove("Degree");
			}
			if (insuranceResponseCheck != null && insuranceResponseCheck.toString().startsWith("[")) {

			} else if (insuranceResponseCheck != null) {

				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				Insurance insurance = gson2.fromJson(jo.get("Insurance"), Insurance.class);
				if (insurance != null) {
					insurances.add(insurance);
				}
				jo.remove("Insurance");

			}
			if (timeGapResponseCheck != null && timeGapResponseCheck.toString().startsWith("[")) {

			} else if (timeGapResponseCheck != null) {
				Gson gson2 = new Gson();
				gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
				TimeGap timeGap = gson2.fromJson(jo.get("TimeGap"), TimeGap.class);
				if (timeGap != null) {
					timeGaps.add(timeGap);
				}
				jo.remove("TimeGap");
			}

			caqhProvider = gson.fromJson(jo, CAQHProvider.class);
			
			caqhProvider.setProviderCDS(providerCDSes);
			caqhProvider.setPractice(practices);
			caqhProvider.setHospital(caqhHospitals);

			return new ResponseEntity<>(new ResponseEntityObject<>(true, "success", caqhProvider), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	

}
