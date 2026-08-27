CREATE TABLE invoice
(
    id              BIGSERIAL PRIMARY KEY,

    supplier        VARCHAR(255),
    invoice_number  VARCHAR(100),

    invoice_date    DATE,
    due_date        DATE,

    amount          NUMERIC(12, 2),
    currency        VARCHAR(3),

    status          VARCHAR(30)              NOT NULL,
    decision_reason VARCHAR(50)              NOT NULL,

    raw_ai_response TEXT,

    processed_at    TIMESTAMP WITH TIME ZONE NOT NULL
);