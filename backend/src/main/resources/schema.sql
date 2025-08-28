CREATE TABLE IF NOT EXISTS image
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    size bigint,
    address VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_img_entries_name ON image (name);
CREATE INDEX IF NOT EXISTS idx_img_entries_size ON image (size);

