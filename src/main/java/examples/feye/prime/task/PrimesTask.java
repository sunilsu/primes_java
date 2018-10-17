package examples.feye.prime.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import examples.feye.prime.dao.PrimesDAO;

@Service
public class PrimesTask {
	
	@Autowired
	private PrimesDAO primesDao;
	
	@Autowired
	ThreadPoolTaskExecutor asyncExecutor;
	
	private final Logger log = LoggerFactory.getLogger(PrimesTask.class);
	
	public boolean isPrime(int n) {
		if (n < 2)
			return false;
		if (n % 2 == 0 && n > 2)
			return false;
		for (int i = 3; i < (int) Math.sqrt(n) + 1; i += 2)
			if (n % i == 0)
				return false;
		return true;
	}
		
	@Async("asyncExecutor")
	public void startPrimesListTask(int startNum, int endNum, String jobId) {
		log.info(startNum + ", " + endNum + ", " + jobId);
		String result = null;
		List<Integer> primes = Collections.synchronizedList(new ArrayList<Integer>());
		CountDownLatch latch = new CountDownLatch(endNum - startNum + 1);
		for (int i = startNum; i <= endNum; i++) {
			// asyncExecutor.submit(new PrimeRunnable(i, primes));
			final int n = i;
			asyncExecutor.submit(() -> {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				if (isPrime(n)) {
					primes.add(n);
				}
				latch.countDown();
				Thread.yield();
			});
		}
		try {
			latch.await();
			Collections.sort(primes);
			result = primes.stream().map(Object::toString).collect(Collectors.joining(", "));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}

		primesDao.saveResult(jobId, result);
	}
}
