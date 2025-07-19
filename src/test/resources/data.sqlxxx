-- Insert into client table
INSERT INTO client (id, client_name, created_datetime, updated_datetime) VALUES
  (1, 'Alice Johnson', '2024-01-01T09:00:00+00:00', '2024-01-01T09:00:00+00:00'),
  (2, 'Bob Smith', '2024-01-01T09:00:00+00:00', '2024-01-01T09:00:00+00:00'),
  (3, 'Charlie Brown', '2024-01-01T09:00:00+00:00', '2024-01-01T09:00:00+00:00');

-- Insert into contract table
INSERT INTO contract (id, client_id, contract_number, created_datetime, updated_datetime) VALUES
  (1, 1, 'C-1001', '2024-01-01T09:01:00+00:00', '2024-01-01T09:01:00+00:00'),
  (2, 1, 'C-1002', '2024-01-01T09:02:00+00:00', '2024-01-01T09:02:00+00:00'),
  (3, 2, 'C-2001', '2024-01-01T09:03:00+00:00', '2024-01-01T09:03:00+00:00'),
  (4, 3, 'C-3001', '2024-01-01T09:04:00+00:00', '2024-01-01T09:04:00+00:00');

-- Insert at least one payment_tracking (needed for payment.tracking_id)
INSERT INTO payment_tracking (id, tracking_number, created_datetime, updated_datetime) VALUES
  (1, 'TR-TEST-001', '2024-01-01T10:00:00+00:00', '2024-01-01T10:00:00+00:00');

-- Insert into payment table (tracking_id must reference existing payment_tracking)
INSERT INTO payment (id, payment_date, amount, type, contract_id, version, created_datetime, updated_datetime, tracking_id) VALUES
  (1, '2024-01-15', 1000.00, 'CREDIT', 1, 0, '2024-01-15T11:00:00+00:00', '2024-01-15T11:00:00+00:00', 1),
  (2, '2024-01-20', 500.00, 'DEBIT', 1, 0, '2024-01-20T12:00:00+00:00', '2024-01-20T12:00:00+00:00', 1),
  (3, '2024-02-01', 1200.00, 'CREDIT', 2, 0, '2024-02-01T13:00:00+00:00', '2024-02-01T13:00:00+00:00', 1),
  (4, '2024-02-10', 750.00, 'DEBIT', 3, 0, '2024-02-10T14:00:00+00:00', '2024-02-10T14:00:00+00:00', 1),
  (5, '2024-03-01', 900.00, 'CREDIT', 4, 0, '2024-03-01T15:00:00+00:00', '2024-03-01T15:00:00+00:00', 1);
