package com.credv3.attachment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.credv3.auditTrail.AuditTrailHandler;
import com.credv3.auditTrail.AuditTrailRequest;
import com.credv3.common.entities.Attachment;
import com.credv3.common.entities.Expirable;
import com.credv3.common.entities.Provider;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ConstantKey;
import com.credv3.helper.DatabaseHelper;
import com.credv3.helper.RequestInterceptorResponse;
import com.credv3.helper.ResponseEntityObject;

@Component
public class AttachmentHandler extends CentralService {
	@Autowired
	private AttachmentRepositoryImpl attchmentImpl;
	@Autowired
	private AuditTrailHandler auditTrailHandler;

	public ResponseEntity<Object> createAttachment(AttachmentRequest request, RequestInterceptorResponse req) {
		try {

			Attachment attachment = new Attachment();
			attachment.setFileName(request.getFileName());
			attachment.setFileType(request.getFileType());
			attachment.setFileUrl(request.getFileUrl());
			attachment.setIssueDate(request.getIssueDate());
			attachment.setExpirationDate(request.getExpirationDate());
			attachment.setDescription(request.getDescription());
			attachment.setIsTrackable(request.getIsTrackable());
			attachment.setStateCode(request.getStateCode());
			attachment.setStateName(request.getStateName());
			Provider provider = providerJpaRepo.findByUuid(request.getProviderUUID());
			if (provider != null) {
				attachment.setProvider(provider);
				attachment.setClientBusinessInfo(provider.getClientBusinessInfo());
			}
			attachmentJpaRepository.save(attachment);
			try {
				AuditTrailRequest auditTrailRequest = new AuditTrailRequest();
				auditTrailRequest.setActionType("Created");
				auditTrailRequest.setDescription(request.getFileType());
				auditTrailRequest.setActionFrom("Document Created");;
				auditTrailRequest.setUserAccountUuid(req.getAccountUuid());
				auditTrailRequest.setProviderUuid(request.getProviderUUID());
				auditTrailRequest.setClientBusinessUuid(req.getClientBusinessUuid());
				auditTrailHandler.createAuditTrail(auditTrailRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (request.getIsTrackable() == 1) {
				Expirable expirable = new Expirable();
				expirable.setTag(provider.getFirstName() + " " + provider.getLastName() + " " + request.getFileType());
				expirable.setFrequency("Weekly");
				expirable.setProvider(provider);
				expirable.setDocType(request.getFileType());
				expirable.setEffectiveDate(request.getIssueDate());
				expirable.setExpirationDate(request.getExpirationDate());
				expirable.setIsNotify(request.getIsTrackable());
				expirable.setImagaeUrl(request.getFileUrl());
				expirable.setStateCode(request.getStateCode());
				expirable.setStateName(request.getStateName());
				expirable.setNotificationDate(new Date());
				expirable.setCreatedDate(new Date());
				expirable.setUpdatedDate(new Date());
				expirable.setClientBusinessInfo(provider.getClientBusinessInfo());
				expirableJpaRepo.save(expirable);
			}

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> getProviderAttachment(UUID providerUUID) {
		try {
			List<Attachment> attachments = attachmentJpaRepository.findTop10ByProvider_Uuid(providerUUID);

			AttachmentDTOMapper enterpriseUserDTOMapper = Mappers.getMapper(AttachmentDTOMapper.class);
			List<AttachmentDTO> enterpriseUserDTO = enterpriseUserDTOMapper.map(attachments);

			return new ResponseEntity<>(enterpriseUserDTO, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> findByUUID(UUID providerUUID, DatabaseHelper databaseHelper,RequestInterceptorResponse req, String type, int isTrackable) {

		try {
			
			List<UUID> userUuids = new ArrayList<>();
 			
			if(req.getRoleType().equalsIgnoreCase(ConstantKey.CLIENT) && helperExtension.isNullOrEmpty(req.getClientBusinessUuid())) {
				Map<String, Object> resp = new HashMap<>();
				resp.put(ConstantExtension.RETURN_OBJECT_KEY, null);
				resp.put("totalItems", 0l);

				return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
			}
			
			if(req.getRole().equalsIgnoreCase("Enterprise User")){
				userUuids.add(req.getUserUuid());
			}
//			else if(req.getRole().equalsIgnoreCase("Enterprise Leader")) {
//				userUuids = leaderAndUserMappingJpaRepo.findByEnterpriseLeader_Uuid(req.getUserUuid().toString());
//			}
			
			List<Attachment> list = attchmentImpl.getList(providerUUID, req.getClientBusinessUuid(), userUuids, databaseHelper, type,isTrackable);

			AttachmentDTOMapper enterpriseUserDTOMapper = Mappers.getMapper(AttachmentDTOMapper.class);
			List<AttachmentDTO> enterpriseUserDTO = enterpriseUserDTOMapper.map(list);
			
			for (AttachmentDTO attachmentDTO : enterpriseUserDTO) {
				Attachment attachment = attachmentJpaRepository.findById(attachmentDTO.getId()).orElse(null);
				Provider provider = attachment.getProvider();
				
//				ProviderDTOMapper mapper = Mappers.getMapper(ProviderDTOMapper.class);
//				ProviderDTO providerDTO = mapper.map(provider);
				
				ProviderAttachmentDTO providerDTO = new ProviderAttachmentDTO();

				providerDTO.setFirstName(provider.getFirstName());
				providerDTO.setLastName(provider.getLastName());
				providerDTO.setNpi(provider.getNpi());
				providerDTO.setEmail(provider.getEmail());
				providerDTO.setPhone(provider.getPhone());
				
				String specialty = providerSpecialityJPARepo.getDescription(provider.getUuid().toString());
				providerDTO.setSpecialty(specialty);
				attachmentDTO.setProvider(providerDTO);
			}

			long list1 = attchmentImpl.getCount(providerUUID, req.getClientBusinessUuid(), userUuids, databaseHelper, type,isTrackable);

			Map<String, Object> resp = new HashMap<>();
			resp.put(ConstantExtension.RETURN_OBJECT_KEY, enterpriseUserDTO);
			resp.put("totalItems", list1);

			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<Object> deleteByAttachmentId(long id) {
		try {
			if(id<=0) {
				return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INVALID_ID), HttpStatus.OK);
			}
			
			documentShareJpaRepository.deleteByDocument_Id(id);
			
			attachmentJpaRepository.deleteById(id);
			
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_DELETED), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<Object> updateAttachment(AttachmentRequest attachmentrequest) {
		try {
			Map<String, Object> resp = new HashMap<>();
			Attachment attachment = attachmentJpaRepository.findById(attachmentrequest.getId()).orElse(null);
			if (attachment != null) {
				attachment.setFileName(attachmentrequest.getFileName());
				attachment.setFileType(attachmentrequest.getFileType());
				attachment.setFileUrl(attachmentrequest.getFileUrl());
				attachment.setIssueDate(attachmentrequest.getIssueDate());
				attachment.setExpirationDate(attachmentrequest.getExpirationDate());
				attachment.setDescription(attachmentrequest.getDescription());
				attachment.setIsTrackable(attachmentrequest.getIsTrackable());
				attachment.setStateCode(attachmentrequest.getStateCode());
				attachment.setStateName(attachmentrequest.getStateName());
				attachmentJpaRepository.save(attachment);
				resp.put(ConstantExtension.RETURN_OBJECT_KEY, ConstantExtension.UPDATED);
				return new ResponseEntity<>(resp, HttpStatus.OK);
			}
			resp.put(ConstantExtension.RETURN_OBJECT_KEY, ConstantExtension.DOC_NOT_EXIST);
			return new ResponseEntity<>(resp, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> findAttachmentById(long id) {

		try {
			Attachment attachment = attachmentJpaRepository.findById(id).orElse(null);

			AttachmentDTOMapper enterpriseUserDTOMapper = Mappers.getMapper(AttachmentDTOMapper.class);
			AttachmentDTO attachmentDTO = enterpriseUserDTOMapper.map(attachment);

			Map<String, Object> resp = new HashMap<>();
			resp.put(ConstantExtension.RETURN_OBJECT_KEY, attachmentDTO);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(ConstantExtension.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	public int getRandomOtp() {
		Random rnd = new Random();
		int otp = 0;
		otp = rnd.nextInt(9999);
		int le = String.valueOf(otp).length();
		if (le < 4) {
			otp = otp * 10;
		}
		return otp;
	}

	public ResponseEntity<Object> saveOtp(long id) throws Exception {
		int otp = getRandomOtp();
		Attachment attachment = attachmentJpaRepository.findById(id).orElse(null);
		attachment.setOtp(String.valueOf(otp));
		attachmentJpaRepository.save(attachment);
		Map<String, Object> resp = new HashMap<>();
		resp.put("status", "otp generated");
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	public ResponseEntity<Object> matchOtp(long id, String otp) {
		try {
			Map<String, Object> resp = new HashMap<>();
			Attachment attachment = attachmentJpaRepository.findById(id).orElse(null);
			if (attachment.getOtp().equals(otp)) {
				resp.put(ConstantExtension.RETURN_OBJECT_KEY, "verified");
				AttachmentDTOMapper enterpriseUserDTOMapper = Mappers.getMapper(AttachmentDTOMapper.class);
				AttachmentDTO attachmentDTO = enterpriseUserDTOMapper.map(attachment);
				resp.put("object", attachmentDTO);
			} else {
				resp.put(ConstantExtension.RETURN_OBJECT_KEY, "unauthorised");
			}
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(ConstantExtension.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	public ResponseEntity<Object> getOtp(long id) {
		try {
			Map<String, Object> resp = new HashMap<>();
			Attachment attachment = attachmentJpaRepository.findById(id).orElse(null);

			resp.put(ConstantExtension.RETURN_OBJECT_KEY, attachment.getOtp());

			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(ConstantExtension.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	

}
