CREATE TABLE book (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    isbn VARCHAR(255)
);

CREATE TABLE copy (
    id UUID PRIMARY KEY,
    bookId UUID,
    barCode VARCHAR(255),
    available BOOLEAN
);

CREATE TABLE loan (
    id UUID PRIMARY KEY,
    copyId UUID,
    userId UUID,
    createdAt TIMESTAMP,
    expectedReturnDate DATE,
    returnedAt TIMESTAMP,
    overdueFee DECIMAL(19, 2)
);
