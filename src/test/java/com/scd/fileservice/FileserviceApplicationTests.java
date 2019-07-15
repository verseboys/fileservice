package com.scd.fileservice;

import com.jcraft.jsch.ChannelSftp;
import com.mongodb.client.gridfs.GridFSBucket;
import com.scd.filesdk.common.PoolType;
import com.scd.filesdk.tools.SingletonPoolTool;
import com.scd.filesdk.pool.ChannelSftpPool;
import com.scd.filesdk.pool.ChannelSftpPoolCreater;
import com.scd.filesdk.util.FileUtil;
import com.scd.fileservice.utils.FileUploadUtil;
import com.scd.test.mongo.MongoTask;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = {"com.scd.filesdk","com.scd.fileservice"})
public class FileserviceApplicationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileserviceApplicationTests.class);

	@Autowired
	private FileUploadUtil fileUploadUtil;

	@Autowired
	private GridFSBucket gridFSBucket;

	@Test
	public void contextLoads() {
	}

	private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

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

	@Test
	public void testMultiThreadMongo() throws Exception {
		String basePath = "C:\\Users\\chengdu\\Desktop\\filetype";
		List<String> filePaths = new ArrayList<>(10);
		FileUtil.getFilePaths(basePath, filePaths);
		// 添加 gridFSBucket 属性
		MongoTask.setGridFSBucket(gridFSBucket);
		ExecutorService threadPool = Executors.newFixedThreadPool(filePaths.size());
		List<Future<String>> futureList = new ArrayList<>(filePaths.size());
		for(String filepath : filePaths){
			FileInputStream fileInputStream = new FileInputStream(filepath);
			String fileName = FileUtil.getFileName(filepath);
			MongoTask mongoTask = new MongoTask(fileName, fileInputStream);
			Future<String> stringFuture = threadPool.submit(mongoTask);
			futureList.add(stringFuture);
		}
		// get 阻塞等待
		for(Future<String> stringFuture : futureList){
			System.out.println(stringFuture.get());
		}
		LOGGER.info("child thread over ");
		threadPool.shutdown();
	}

	@Autowired
	private ChannelSftpPoolCreater channelSftpPoolCreater;

	@Test
	public void testPoolMulti() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(5);
		for(int i = 0; i < 5; i++){
			cachedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						channelSftpPoolCreater.createChannelSftpPool();
					}finally {
						countDownLatch.countDown();
					}
				}
			});
		}
		countDownLatch.await();
		LOGGER.info("child thread over");
	}

	@Test
	public void testSfpPool() throws InterruptedException, ExecutionException {
		List<Future<ChannelSftpPool>> futureList = new ArrayList<>(10);
		for(int i = 0; i <10; i++) {
			Future<ChannelSftpPool> future = cachedThreadPool.submit(new Callable<ChannelSftpPool>() {
				@Override
				public ChannelSftpPool call() throws Exception {
						return channelSftpPoolCreater.createChannelSftpPool();
					}
				}
			);
			futureList.add(future);
		}
		for(int i = 0; i < futureList.size() - 1; i++){
			LOGGER.info("Task result {}", futureList.get(i).get());
		}
		LOGGER.info("child thread over");
	}


	@Test
	public void testPoolFactory(){
		GenericObjectPool genericObjectPool = SingletonPoolTool.createPool(PoolType.SFTP);
		ChannelSftp channelSftp = null;
		try {
			channelSftp = (ChannelSftp) genericObjectPool.borrowObject();
			SingletonPoolTool.showPoolInfo(PoolType.SFTP);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			genericObjectPool.returnObject(channelSftp);
			SingletonPoolTool.showPoolInfo(PoolType.SFTP);
		}
	}

}
