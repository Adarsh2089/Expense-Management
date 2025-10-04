-- V2: Insert seed data for testing

-- Insert sample company
INSERT INTO companies (name, country, default_currency, is_manager_approver) 
VALUES ('Demo Corp', 'United States', 'USD', true);

-- Insert admin user (password: admin123)
INSERT INTO users (email, password, full_name, role, company_id) 
VALUES ('admin@democorp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1G2oJLPb5SvRPj6jdZjKPqmb.vu9Y1a', 'Admin User', 'ADMIN', 1);

-- Insert manager user (password: manager123)
INSERT INTO users (email, password, full_name, role, company_id) 
VALUES ('manager@democorp.com', '$2a$10$E2UPv7arXmp3q0LBkqKLDeYHrI7NfoKDW6Lz.f8z7EaYKg0YB.QY2', 'Manager User', 'MANAGER', 1);

-- Insert employee users (password: employee123)
INSERT INTO users (email, password, full_name, role, company_id, manager_id) 
VALUES ('employee1@democorp.com', '$2a$10$lhh7gRk6.vQG1RrLkZfKse5FMVB0t.W1h5k5Q6xW6rHKN7l8E.6eG', 'John Doe', 'EMPLOYEE', 1, 2);

INSERT INTO users (email, password, full_name, role, company_id, manager_id) 
VALUES ('employee2@democorp.com', '$2a$10$lhh7gRk6.vQG1RrLkZfKse5FMVB0t.W1h5k5Q6xW6rHKN7l8E.6eG', 'Jane Smith', 'EMPLOYEE', 1, 2);

-- Insert sample expenses
INSERT INTO expenses (user_id, amount, currency, category, description, expense_date, status) 
VALUES 
    (3, 150.00, 'USD', 'Office Supplies', 'Laptop accessories and cables', '2025-10-01', 'PENDING'),
    (3, 85.50, 'USD', 'Meals', 'Client dinner', '2025-10-02', 'PENDING'),
    (4, 1200.00, 'USD', 'Travel', 'Flight to conference', '2025-09-28', 'APPROVED'),
    (4, 45.00, 'USD', 'Transportation', 'Taxi to airport', '2025-09-28', 'REJECTED');

-- Insert approval rules for the company
-- Rule 1: Manager approval for expenses under $500
INSERT INTO company_approval_rules (company_id, rule_type, threshold_amount, sequence, active) 
VALUES (1, 'SPECIFIC_APPROVER', 500.00, 1, true);

-- Rule 2: Admin approval for expenses over $500
INSERT INTO company_approval_rules (company_id, rule_type, threshold_amount, specific_approver_id, sequence, active) 
VALUES (1, 'PERCENTAGE', 500.00, 1, 2, true);

-- Insert approval steps for pending expenses
-- Expense 1 ($150) - Manager approval
INSERT INTO approval_steps (expense_id, approver_id, sequence, decision) 
VALUES (1, 2, 1, 'PENDING');

-- Expense 2 ($85.50) - Manager approval
INSERT INTO approval_steps (expense_id, approver_id, sequence, decision) 
VALUES (2, 2, 1, 'PENDING');

-- Expense 3 ($1200) - Already approved
INSERT INTO approval_steps (expense_id, approver_id, sequence, decision, decided_at) 
VALUES (3, 2, 1, 'APPROVED', CURRENT_TIMESTAMP);

INSERT INTO approval_steps (expense_id, approver_id, sequence, decision, decided_at) 
VALUES (3, 1, 2, 'APPROVED', CURRENT_TIMESTAMP);

-- Expense 4 ($45) - Already rejected
INSERT INTO approval_steps (expense_id, approver_id, sequence, decision, comments, decided_at) 
VALUES (4, 2, 1, 'REJECTED', 'Missing receipt', CURRENT_TIMESTAMP);
