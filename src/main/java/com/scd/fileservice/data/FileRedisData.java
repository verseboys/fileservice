package com.scd.fileservice.data;

import com.scd.fileservice.common.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    public Long saveFileId(String fileId){
        return stringRedisTemplate.opsForSet().add(CommonConstant.FILES, fileId);
    }

    public Boolean existsFileId(String fileId){
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

    public void saveBreakInfo(String fileId, Map<String,String> breakInfoMap){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        stringRedisTemplate.opsForHash().putAll(breakInfoKey, breakInfoMap);
    }

    public void updateBreakInfoStatus(String fileId, boolean status){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        stringRedisTemplate.opsForHash().put(breakInfoKey,
                CommonConstant.BREAKINFO.filestatus.getValue(), String.valueOf(status));
    }

    public boolean existsFileStatus(String fileId){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        return stringRedisTemplate.opsForHash().hasKey(breakInfoKey, CommonConstant.BREAKINFO.filestatus.getValue());
    }

    public Map<Object, Object> findBreakFileInfo(String fileId){
        String breakInfoKey = String.format(CommonConstant.FILE_BREAK_INFO, fileId);
        return stringRedisTemplate.opsForHash().entries(breakInfoKey);
    }

    /**
     * 初始化记录为0，
     * 最终期望结果为1
     * @param fileId
     * @param chunks
     */
    public void initBreakRecord(String fileId, int chunks){
        if(chunks == 0){
            return ;
        }
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        String breakAllKey = String.format(CommonConstant.FILE_BREAK_EXPECTED, fileId);
        StringBuffer record = new StringBuffer("");
        StringBuffer expecteds = new StringBuffer("");
        for(int i=0; i < chunks; i++){
            record.append(CommonConstant.CHUNK_NOT_UPLOADED);
            expecteds.append(CommonConstant.CHUNK_UPLOADED);
        }
        stringRedisTemplate.opsForValue().set(breakRecordKey, record.toString());
        stringRedisTemplate.opsForValue().set(breakAllKey, expecteds.toString());
    }

    public boolean existsBreakRecord(String fileId){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        if(StringUtils.isEmpty(stringRedisTemplate.opsForValue().get(breakRecordKey))){
            return false;
        }
        return true;
    }

    public String findBreakRecord(String fileId){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        return stringRedisTemplate.opsForValue().get(breakRecordKey);
    }

    public Boolean deleteBreakRecord(String fileId){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        return stringRedisTemplate.delete(breakRecordKey);
    }

    public String findBreakExpected(String fileId){
        String breakAllKey = String.format(CommonConstant.FILE_BREAK_EXPECTED, fileId);
        return stringRedisTemplate.opsForValue().get(breakAllKey);
    }

    public Boolean deleteBreakExpected(String fileId){
        String breakAllKey = String.format(CommonConstant.FILE_BREAK_EXPECTED, fileId);
        return stringRedisTemplate.delete(breakAllKey);
    }

    public void setuploadedChunk(String fileId, int index){
        String breakRecordKey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        stringRedisTemplate.opsForValue().set(breakRecordKey, CommonConstant.CHUNK_UPLOADED, index);
    }


    public void saveBreakAddress(String fileId, String remotePath){
        String breakAddressKey = String.format(CommonConstant.FILE_BREAK_ADDRESS, fileId);
        stringRedisTemplate.opsForList().rightPush(breakAddressKey, remotePath);
    }

    public List<String> findBreakAddress(String fileId, int chunks){
        String breakAddressKey = String.format(CommonConstant.FILE_BREAK_ADDRESS, fileId);
        return stringRedisTemplate.opsForList().range(breakAddressKey, 0, chunks - 1);
    }

}
