package com.scd.fileservice.data;

import com.scd.fileservice.common.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Service
public class FileRedisData {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public long saveFileId(String fileId){
        return stringRedisTemplate.opsForSet().add(CommonConstant.FILES, fileId);
    }

    public boolean existsFileId(String fileId){
        return stringRedisTemplate.opsForSet().isMember(CommonConstant.FILES, fileId);
    }

    public Set<String> findAllFileIds(){
        return stringRedisTemplate.opsForSet().members(CommonConstant.FILES);
    }

    public void saveFileInfo(String fileId, Map<String,String> map){
        String fileKey = String.format(CommonConstant.FILE_INFO, fileId);
        stringRedisTemplate.opsForHash().putAll(fileKey, map);
    }

    public Object findFileStoreType(String fileId){
        String fileKey = String.format(CommonConstant.FILE_INFO, fileId);
        return stringRedisTemplate.opsForHash().get(fileKey, CommonConstant.FILEINFO.storetype);
    }

    public Map<Object,Object> findFileInfo(String fileId){
        String filekey = String.format(CommonConstant.FILE_INFO, fileId);
        return stringRedisTemplate.opsForHash().entries(filekey);
    }

    public List<Object> findAllFileInfo(){
        Set<String> fileIds = findAllFileIds();
        List<Object> objectList = stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
            @Nullable
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                for(String fileId : fileIds){
                    findFileInfo(fileId);
                }
                return null;
            }
        });
        return objectList;
    }

    public void saveBreakInfo(String fileId, Map<?,?> breakInfoMap){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        stringRedisTemplate.opsForHash().putAll(breakInfoKey, breakInfoMap);
    }

    public Map<Object, Object> findBreakFileInfo(String fileId){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        return stringRedisTemplate.opsForHash().entries(breakInfoKey);
    }

    public void initBreakRecord(String fileId, int chunks){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        String[] chunkArr = new String[chunks];
        for(int i=0; i < chunks; i++){
            chunkArr[i] = "0";
        }
        stringRedisTemplate.opsForList().rightPushAll(breakRecordKey, chunkArr);
    }

    public void uploadChunk(String fileId, int index){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        stringRedisTemplate.opsForList().set(breakRecordKey, index, CommonConstant.CHUNK_UPLOADED);
    }
}
