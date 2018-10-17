package examples.feye.prime.dao;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import examples.feye.prime.rest.JobIdNotFoundException;
import examples.feye.prime.rest.PrimesListNoContentException;

@Repository
@Transactional
/***
 * 
 * DAO to access Redis <key,value> data
 *
 */
public class PrimesDAO {

	private static final String PREFIX = "primes";
	private static final String RUNNING = "RUNNING";
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	private final Logger log = LoggerFactory.getLogger(PrimesDAO.class);
	
	/***
	 * If (start_num, end_num) combination exists in cache, return the Id
	 * @param startNum: int, start_num of request
	 * @param endNum, int, end_num of request
	 * @return String, jobId.
	 */
	public String getJobIdFor(int startNum, int endNum) {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		String key = getRequestKey(startNum, endNum);		
		String jobId = valueOps.get(key);
		return jobId;
	}
	
	/***
	 * save <key, jobId> where key is "primes:startNum:endNum" and Id is random UUID
	 * @param startNum int, start_num of request
	 * @param endNum, int, end_num of request
	 * @return UUID string
	 */
	public String saveJob(int startNum, int endNum) {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		String jobId = UUID.randomUUID().toString();
		String key = getRequestKey(startNum, endNum);		
		valueOps.set(key, jobId);
		// Also save the jobId as key and a placeholder value "RUNNING" to indicate 
		// the task is still running
		key = getResultKey(jobId);
		valueOps.set(key, RUNNING);
		return jobId;
	}
	
	/***
	 * Get the result of the job from cache based on jobId
	 * @param jobId
	 * @return
	 * @throws Exception
	 */
	public String getResult(String jobId) throws Exception{
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		String key = getResultKey(jobId);
		String result = valueOps.get(key);
		if (result == null) {
			throw new JobIdNotFoundException("No ID " + jobId);
		}
		else if (result.contains(RUNNING)) {
			throw new PrimesListNoContentException("Job Not Completed");
		}
		else {
			return result;
		}

	}
	
	/***
	 * save the result of task with id == jobId
	 * @param jobId
	 * @param value
	 */
	public void saveResult(String jobId, String value) {
		ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
		String key = getResultKey(jobId);
		valueOps.set(key, value);
	}
	
	/**
	 * Make a key string based on request params
	 * @param startNum: int, start_num of request
	 * @param endNum, int, end_num of request
	 * @return String, key
	 */
	public String getRequestKey(int startNum, int endNum) {
		return PREFIX + ":" + String.valueOf(startNum) + ":" + String.valueOf(endNum);
	}
	
	/***
	 * Make a key string from jobId string
	 * @param jobId
	 * @return
	 */
	public String getResultKey(String jobId) {
		return PREFIX + ":" + String.valueOf(jobId);
	}
}
