package com.credv3.webcrawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public interface S3Services {
	public ByteArrayOutputStream downloadFile(String keyName);
	public void uploadFile(String keyName, MultipartFile file);
	public void putObject(String keyName,ByteArrayInputStream bi,ObjectMetadata ob) throws IOException;
	public List<String> listFiles();
	public void deleteFileFromS3Bucket(String keyName);

}