package examples.feye.prime.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import examples.feye.prime.dao.PrimesDAO;
import examples.feye.prime.task.PrimesTask;

/***
 * 
 * Entrypoint for POST/GET requests
 *
 */
@RestController
public class PrimesController {

	@Autowired
	PrimesDAO primesDao;
	
	@Autowired
	PrimesTask primesTask;
	
	private final Logger log = LoggerFactory.getLogger(PrimesController.class);

	@RequestMapping(value = "/primes", method = RequestMethod.POST)
	public String getPrimes(@RequestParam("start_num") int startNum, @RequestParam("end_num") int endNum) {
		String jobId = primesDao.getJobIdFor(startNum, endNum);
		if (jobId == null) {
			jobId = primesDao.saveJob(startNum, endNum);
			primesTask.startPrimesListTask(startNum, endNum, jobId);
		}
		return jobId;
	}
	
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	public String getResult(@RequestParam("id") String id) throws Exception {
		log.info("Result for " + id);
		String result = primesDao.getResult(id);
		return result;
	}

}
