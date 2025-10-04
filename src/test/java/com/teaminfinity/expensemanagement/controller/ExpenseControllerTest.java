package com.teaminfinity.expensemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaminfinity.expensemanagement.dto.expense.SubmitExpenseRequest;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.Company;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ExpenseStatus;
import com.teaminfinity.expensemanagement.enums.Role;
import com.teaminfinity.expensemanagement.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for ExpenseController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ExpenseService expenseService;
    
    private AppUser testUser;
    private Company testCompany;
    private Expense testExpense;
    
    @BeforeEach
    void setUp() {
        // Setup test company
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Corp");
        testCompany.setCountry("United States");
        testCompany.setDefaultCurrency("USD");
        
        // Setup test user
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFullName("Test User");
        testUser.setRole(Role.EMPLOYEE);
        testUser.setCompany(testCompany);
        
        // Setup test expense
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(testUser);
        testExpense.setAmount(new BigDecimal("100.00"));
        testExpense.setCurrency("USD");
        testExpense.setCategory("Office Supplies");
        testExpense.setDescription("Test expense");
        testExpense.setExpenseDate(LocalDate.now());
        testExpense.setStatus(ExpenseStatus.PENDING);
        testExpense.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    @WithMockUser(username = "test@test.com", roles = {"EMPLOYEE"})
    void testSubmitExpense() throws Exception {
        // Prepare request
        SubmitExpenseRequest request = new SubmitExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setCategory("Office Supplies");
        request.setDescription("Test expense");
        request.setExpenseDate(LocalDate.now());
        
        // Mock service
        when(expenseService.submitExpense(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testExpense);
        
        // Perform request and verify
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.category").value("Office Supplies"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    @WithMockUser(username = "test@test.com", roles = {"EMPLOYEE"})
    void testGetMyExpenses() throws Exception {
        // Mock service
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseService.getUserExpenses(any())).thenReturn(expenses);
        
        // Perform request and verify
        mockMvc.perform(get("/api/expenses/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
    
    @Test
    @WithMockUser(username = "test@test.com", roles = {"EMPLOYEE"})
    void testGetExpenseById() throws Exception {
        // Mock service
        when(expenseService.getExpenseById(1L)).thenReturn(testExpense);
        
        // Perform request and verify
        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }
    
    @Test
    void testSubmitExpenseWithoutAuthentication() throws Exception {
        // Prepare request
        SubmitExpenseRequest request = new SubmitExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setCategory("Office Supplies");
        request.setExpenseDate(LocalDate.now());
        
        // Should return 401 Unauthorized
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
