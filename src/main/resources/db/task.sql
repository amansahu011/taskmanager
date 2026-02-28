DROP TABLE IF EXISTS tasks;

CREATE TABLE tasks (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                       task_number VARCHAR(100) NOT NULL UNIQUE,

                       title VARCHAR(255) NOT NULL,

                       description TEXT NOT NULL,

                       status VARCHAR(50) NOT NULL,

                       created_by BIGINT NOT NULL,

                       created_at DATETIME NOT NULL,

                       updated_at DATETIME,

                       CONSTRAINT fk_task_user
                           FOREIGN KEY (created_by)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);
