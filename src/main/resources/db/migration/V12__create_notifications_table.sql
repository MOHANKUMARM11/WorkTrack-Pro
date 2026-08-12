CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(100) NOT NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               user_id BIGINT NOT NULL,

                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP,
                               created_by BIGINT,
                               updated_by BIGINT,
                               is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                               CONSTRAINT fk_notifications_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
);