DROP TABLE IF EXISTS refresh_tokens;

CREATE TABLE refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                token VARCHAR(255) NOT NULL UNIQUE,
                                expiry_date DATETIME NOT NULL,
                                user_id BIGINT UNIQUE,
                                CONSTRAINT fk_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE
);