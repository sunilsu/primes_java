package examples.feye.prime.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import examples.feye.prime.dao.PrimesDAO;
import examples.feye.prime.task.PrimesTask;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PrimesController.class)
public class PimesControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	PrimesDAO primesDao;

	@MockBean
	PrimesTask primesTask;

	/***
	 * Tests a 200 OK response when (start_num, end_num) is already in cache and jobId is returned
	 * @throws Exception
	 */
	@Test
	public void testGetPrimesInCache() throws Exception {
		String jobId = "abc123";
		Mockito.when(primesDao.getJobIdFor(Mockito.anyInt(), Mockito.anyInt())).thenReturn(jobId);

		// Send course as body to /students/Student1/courses
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/primes?start_num=4&end_num=11")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.OK.value(), response.getStatus());

	}
	
	/***
	 * Test Http 200 response when a new (start_num, end_num) is received
	 * @throws Exception
	 */
	@Test
	public void testGetPrimesNew() throws Exception {
		String jobId = "abc123";
		Mockito.when(primesDao.getJobIdFor(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
		Mockito.when(primesDao.saveJob(Mockito.anyInt(), Mockito.anyInt())).thenReturn(jobId);
		Mockito.doNothing().when(primesTask).startPrimesListTask(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString());

		// Send course as body to /students/Student1/courses
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/primes?start_num=4&end_num=11")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertEquals(HttpStatus.OK.value(), response.getStatus());

	}
	
	/***
	 * test HTTP 404 error when JOB ID is not found in cache
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetResult() throws Exception {
		Mockito.when(primesDao.getResult(Mockito.anyString())).thenThrow(JobIdNotFoundException.class);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/result?id=abc123").accept(
				MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		System.out.println(result.getResponse());

		assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
	}

}
