CREATE TABLE IF NOT EXISTS book (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    isbn VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS copy (
    id UUID PRIMARY KEY,
    book_id UUID,
    bar_code VARCHAR(255),
    available BOOLEAN
);

CREATE TABLE IF NOT EXISTS loan (
    id UUID PRIMARY KEY,
    copy_id UUID,
    user_id UUID,
    created_at TIMESTAMP,
    expected_return_date DATE,
    returned_at TIMESTAMP,
    overdue_fee DECIMAL(19, 2)
);
