package com.accenture.lkm.web.test;



//static imports to use the mockito API
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.accenture.lkm.Application;
import com.accenture.lkm.business.bean.Employee;
import com.accenture.lkm.controller.EmployeeController;
import com.accenture.lkm.service.EmployeeServiceImpl;
import com.accenture.lkm.web.custom.test.utils.JSONUtils;

//Following Annotation is used to tell that SpringJunit is used to run the tests
@RunWith(SpringJUnit4ClassRunner.class)

//Following Annotation is replacement of @Configuration annotation
//it is used to point to the files having the configuration and helps to load and start the context
//Context will be cached for all test cases and classes
@SpringBootTest(classes=Application.class)
// No @Transactional Required as database is never hit
public class GetAndGetAllTests {
	//Step1: Step1: Declare Service Layer Mocks and inject to Controller
	@Mock
	EmployeeServiceImpl serviceIMPL;
	// this @mock annotation, instructs the mockito
	// to analyze the class or interface and produce 
	// a test stub (empty methods) with same public methods and signatures
	
	//Step1: Declare Service Layer Mocks and inject to Controller
	@InjectMocks
	EmployeeController controller;
	// this annotation, tells mockito to inject mocked objects
	// into the EmployeeController
	// in our case the serviceIMPL will be supplied to EmployeeController
    protected MockMvc mockMVC;
    @Before
    public void mySetup(){ 	
	    //It is done to initialize mockito annotations for mocking
	    //prepare the objects for testing
    	MockitoAnnotations.initMocks(this);
    	//Step2:  Using Use MockMvcBuilders to create a MockMvc which is replica just of Controller
    	mockMVC = MockMvcBuilders.standaloneSetup(controller).build();
	}
    
    @Test
    public void getAllEmployeesTest() throws Exception{
    	  String uri="/emp/controller/getDetails";
    	  
    	  //Step3: Use MockHttpServletRequestBuilder to create a request
    	  MockHttpServletRequestBuilder request= MockMvcRequestBuilders.get(uri);
    	  
    	  //Step4: Define the Mocking call for the mocked object created in Step1 and provided to controller 
    	  when(serviceIMPL.getEmployeeDetails()).thenReturn(getEmployeeStubData());
    	  
    	  //Step5: MockMvc created in Step2 will perform() the request created in Step3 and will yield MvcResult
    	  ResultActions rest= mockMVC.perform(request);
    	  MvcResult mvcREsult= rest.andReturn();
		  
    	  //Step6: Extract the actual response content and response status from MvcResult obtained above 
    	  //and compare with the expected response content and expected response status
		  String result= mvcREsult.getResponse().getContentAsString();
		  int actualStatus= mvcREsult.getResponse().getStatus();
    	 
    	  List<Employee> listEmp= JSONUtils.covertFromJsonToObject2(result, List.class);
    	  
    	  //Step7: Verify if the Controller is able to delegate the call to the mock
		   verify(serviceIMPL,times(1)).getEmployeeDetails();
    	  
    	  Assert.assertTrue(listEmp!=null);
    	  Assert.assertTrue(actualStatus==HttpStatus.OK.value());
    }
    
    
    @Test
    public void getAllEmployeeByIdTest() throws Exception{
    	  String uri="/emp/controller/getDetailsById/1003";
    	  
    	  //Step3: Use MockHttpServletRequestBuilder to create a request
    	  MockHttpServletRequestBuilder request= MockMvcRequestBuilders.get(uri);
    	  
    	  //Step4: Define the Mocking call for the mocked object created in Step1 and provided to controller 
    	  when(serviceIMPL.getEmployeeDetailByEmployeeId(1003)).thenReturn( new Employee("Rocky",1003, 90011.1,102));
    	  
          //Step5: MockMvc created in Step2 will perform() the request created in Step3 and will yield MvcResult
    	  ResultActions rest= mockMVC.perform(request);
    	  
    	  //Step6: Extract the actual response content and response status from MvcResult obtained above and compare with the expected response content and expected response status
    	  MvcResult mvcREsult= rest.andReturn();
		  String result= mvcREsult.getResponse().getContentAsString();
		  //actual status and name
		  int statusAct= mvcREsult.getResponse().getStatus();
		  Employee emp= JSONUtils.covertFromJsonToObject2(result, Employee.class);
		  
		  //expected status and name
		  String expectedName="Rocky";
		  int statusExp =200;
		  	  
		   //Step7: Verify if the Controller is able to delegate the call to the mock
		  //As we expect the controller to invoke the getEmployeeDetails() method of serviceIMPL wrapper once 
		  //So to check the same whether controller is actually able to invoke the same or not
		  //We have written the following statement.		   
		  verify(serviceIMPL,times(1)).getEmployeeDetailByEmployeeId(1003);
  	  
    	  Assert.assertTrue(emp.getEmployeeName().equals(expectedName));
    	  Assert.assertTrue(statusAct==statusExp);
    }
   
	public List<Employee> getEmployeeStubData(){
		return Arrays.asList(new Employee("Jack", 1001,90011.1,101),new Employee("Amy",1002, 90011.1,102),
							 new Employee("Justin",1004, 90011.1,101),new Employee("Cynthya",1005, 90011.1,102));
	}
    
}