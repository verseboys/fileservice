# fileservice
file upload download

> 1.支持多种存储服务器上传、下载 
>
> 2.支持大文件切片上传
>
> 3.存储记录信息使用 redis记录， 文件id可用于与业务数据库关联
>
> 4.目前支持上传文件到 Local、SFTP、FTP、Mongo
>
> 5.支持集群环境部署(Local 类型暂不支持)
>
> 6.小文件推荐使用 MongoDB 存储



# 应用简介



>框架  ：SpringBoot 
>
>数据库 ：Redis
>
>支持上传的服务器 :  SFTP、FTP、Mongo, 也可以存储到部署服务器



##   数据库存储说明

文件信息存储使用Redis ，键值对信息如下

| 键    | type | value      | 说明       |
| :---: | :--: | :----------------: | :--------: |
| files | set  | id | 存储文件id |
|file:{id}|hash|file_name,file_size,file_address,upload_type,upload_time,store_type|存储文件详细信息|
|file:breakinfo:{id}|hash|file_status,chunk_size,chunk_num|存储断点上传信息，切片大小，是否已上传完成.一共有多少块|
|file:breakrecord:{id}|string|010|存储上传块记录，0代表未上传，1代表已上传|
|file:breakexpected:{id}|string|111|存储数据块校验结果|
|file:breakaddress:{id}|list|[address1,address2]|存储切片上传地址|

###  相关键值对样例

```java
127.0.0.1:6379[15]> keys *
 1) "files"
 2) "file:b5b82257-7225-40c7-b265-a19b568e3b2a"
 3) "file:17b56d93-00b1-4ed2-8d38-487e1df95ba6"
 4) "file:0fa4c055-22a1-4c3c-883f-bab1b0e52981"
 5) "file:5b83b8a7-6177-4bd7-ba8f-02975d31d3f0"
 6) "file:8cb3e485-f7ff-4ae5-8ec7-34a720405e7d"
 7) "file:6fc01407-8e52-4781-a91e-981eb2f17cb2"
 8) "file:2aea0d84-ac7e-4b62-b227-72f8a15a53ca"
 9) "file:a856e924-a3d3-4a80-a599-51ab4235f172"
10) "file:e3109bee-b5a6-4927-8918-1ad155002590"
127.0.0.1:6379[15]> type files
set
127.0.0.1:6379[15]> smembers files
1) "0fa4c055-22a1-4c3c-883f-bab1b0e52981"
2) "6fc01407-8e52-4781-a91e-981eb2f17cb2"
3) "5b83b8a7-6177-4bd7-ba8f-02975d31d3f0"
4) "17b56d93-00b1-4ed2-8d38-487e1df95ba6"
5) "2aea0d84-ac7e-4b62-b227-72f8a15a53ca"
6) "a856e924-a3d3-4a80-a599-51ab4235f172"
7) "b5b82257-7225-40c7-b265-a19b568e3b2a"
8) "8cb3e485-f7ff-4ae5-8ec7-34a720405e7d"
9) "e3109bee-b5a6-4927-8918-1ad155002590"
127.0.0.1:6379[15]> hgetall file:8cb3e485-f7ff-4ae5-8ec7-34a720405e7d
 1) "upload_time"
 2) "1561192490317"
 3) "upload_type"
 4) "sftp"
 5) "file_name"
 6) "jasypt-dev-config.png"
 7) "file_address"
 8) "/home/test/sftpupload/2019-06-22/jasypt-dev-config.png"
 9) "store_type"
10) "all"
11) "file_size"
12) "58133"
127.0.0.1:6379[15]>   

```


# 内存

```

时间: 
2019-06-30 19:54:07
已用: 
   405,771 KB
已提交: 
   805,888 KB
最大值: 
 1,839,104 KB
GC 时间: 
PS MarkSweep上的       0.288 秒 (3收集)
PS Scavenge上的       0.653 秒 (34收集)
```

# 连接池

​       SFTP 连接池、FTP 连接池， apache commons-pool2实现，

避免频繁、创建销毁连接对象，空闲检测



#  问题

​	ftp 断点续传 使用连接池之后 存在一些问题

​	欢迎 大家 pull request 解决问题



# 规划

​      1.断点续传客户端，每一次只下载一个分片，有客户端记录合并了那些分片文件

​      2.增加 fastdfs  hdfs

## FASTDFS 文件服务搭建
[https://blog.csdn.net/weixin_43279476/article/details/87789715](https://blog.csdn.net/weixin_43279476/article/details/87789715)
