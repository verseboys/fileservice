# fileservice
file upload download

> 1.支持多种存储服务器上传、下载 local(上传到本地服务器)、ftp(转发到ftp文件服务器)、sftp(转发到sftp文件服务器)、支持断点续传
>
> 2.存储记录信息使用 redis记录， 文件id可用与业务数据库关联



##   数据库存储说明

文件信息存储使用Redis ，键值对信息如下

| 键    | type | value      | 说明       |
| :---: | :--: | :----------------: | :--------: |
| files | set  | id | 存储文件id |
|file:{id}|hash|file_name,file_size,file_address,upload_type,upload_time,store_type|存储文件详细信息|
|file:breakinfo:{id}|hash|chunk_size,file_status|存储断点上传信息，切片大小，是否已长传完成|
|file:break:record:{id}|list|[0,1,0]|存储上传块记录，0代表未上传，1代表已上传|

###  相关键值对样例

