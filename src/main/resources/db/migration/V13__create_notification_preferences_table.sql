CREATE TABLE notification_preferences (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT NOT NULL,
                                          channel VARCHAR(30) NOT NULL,
                                          enabled BOOLEAN NOT NULL DEFAULT TRUE,

                                          CONSTRAINT fk_notification_preferences_user
                                              FOREIGN KEY (user_id)
                                                  REFERENCES users(id),

                                          CONSTRAINT uk_notification_preferences_user_channel
                                              UNIQUE (user_id, channel)
);