package com.scd.fileservice;

import com.scd.fileservice.utils.FileUploadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = {"com.scd.fileservice"})
public class FileserviceApplicationTests {

	@Autowired
	private FileUploadUtil fileUploadUtil;

	@Test
	public void contextLoads() {
	}

	@Test
	public void uploadFileInputStream() throws Exception{
		String filePath = "C:/Users/chengdu/Desktop/filetype/database.PNG";
		InputStream inputStream = new FileInputStream(filePath);
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		String filename = "database.PNG";
		byte[] bytes = new byte[inputStream.available()];
		inputStream.read(bytes);
		ByteArrayResource byteArrayResource = new ByteArrayResource(bytes,"aa.png"){
			@Override
			public String getFilename(){
				return filename;
			}
		};
//		InputStreamResource inputStreamResource = new InputStreamResource(inputStream, ""){
//			@Override
//			public String getFilename(){
////				return this.getDescription();
//				return filename;
//			}
//		};
//		paramMap.add("file",inputStreamResource);
		paramMap.add("file", byteArrayResource);
		paramMap.add("name","scd");
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap);
		String url = "http://localhost:8081/mongodbstu/single/fileupload.do";
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
		System.out.println(result.getBody());
	}

	@Test
	public void testFilePath(){
		String filePath = "C:/Users/chengdu/Desktop/filetype/database.PNG";
		fileUploadUtil.uploadFileByPath(filePath);
	}

	@Test
	public void testFileInputStream1() throws IOException{
		String filePath = "C:/Users/chengdu/Desktop/filetype/database.PNG";
		InputStream inputStream = new FileInputStream(filePath);
		fileUploadUtil.uploadFileByInputStream(inputStream, "aa.png");
	}

	@Test
	public void testFileInputStream2() throws IOException{
		String filePath = "C:/Users/chengdu/Desktop/filetype/database.PNG";
		InputStream inputStream = new FileInputStream(filePath);
		fileUploadUtil.uploadFileByInputStreamEx(inputStream, "aa.png");
	}
}
