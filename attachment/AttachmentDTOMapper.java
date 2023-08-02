package com.credv3.attachment;

import java.util.List;

import org.mapstruct.Mapper;

import com.credv3.common.entities.Attachment;

@Mapper
public interface AttachmentDTOMapper {

	List<AttachmentDTO> map(List<Attachment> enterpriseUsers);
	AttachmentDTO map(Attachment enterpriseUser);
	
}
