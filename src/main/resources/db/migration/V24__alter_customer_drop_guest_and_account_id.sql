ALTER TABLE customer
    DROP CONSTRAINT client_account_id_fkey,
    DROP COLUMN account_id,
    DROP COLUMN is_guest;
