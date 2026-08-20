
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,

                       name VARCHAR(100) NOT NULL,

                       email VARCHAR(150) NOT NULL UNIQUE,

                       password VARCHAR(255) NOT NULL,

                       phone VARCHAR(20),

                       role VARCHAR(30) NOT NULL,

                       created_at DATETIME NOT NULL,

                       updated_at DATETIME
);

CREATE TABLE cows (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,

                      name VARCHAR(100) NOT NULL,

                      breed VARCHAR(100) NOT NULL,

                      age INT NOT NULL,

                      gender VARCHAR(20) NOT NULL,

                      price DECIMAL(12,2) NOT NULL,

                      description TEXT,

                      location VARCHAR(150),

                      status VARCHAR(30) NOT NULL,

                      seller_id BIGINT NOT NULL,

                      created_at DATETIME NOT NULL,

                      updated_at DATETIME,

                      CONSTRAINT fk_cow_seller
                          FOREIGN KEY (seller_id)
                              REFERENCES users(id)
                              ON DELETE CASCADE
);

CREATE TABLE cow_images (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,

                            image_url VARCHAR(500) NOT NULL,

                            public_id VARCHAR(255),

                            cow_id BIGINT NOT NULL,

                            CONSTRAINT fk_image_cow
                                FOREIGN KEY (cow_id)
                                    REFERENCES cows(id)
                                    ON DELETE CASCADE
);

CREATE TABLE enquiries (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,

                           message TEXT NOT NULL,

                           status VARCHAR(30) NOT NULL,

                           buyer_id BIGINT NOT NULL,

                           cow_id BIGINT NOT NULL,

                           created_at DATETIME NOT NULL,

                           CONSTRAINT fk_enquiry_buyer
                               FOREIGN KEY (buyer_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_enquiry_cow
                               FOREIGN KEY (cow_id)
                                   REFERENCES cows(id)
                                   ON DELETE CASCADE
);

CREATE TABLE favorites (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,

                           user_id BIGINT NOT NULL,

                           cow_id BIGINT NOT NULL,

                           created_at DATETIME NOT NULL,

                           CONSTRAINT fk_favorite_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_favorite_cow
                               FOREIGN KEY (cow_id)
                                   REFERENCES cows(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT uk_user_cow
                               UNIQUE (user_id, cow_id)
);

CREATE TABLE reviews (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,

                         rating INT NOT NULL,

                         comment TEXT,

                         reviewer_id BIGINT NOT NULL,

                         cow_id BIGINT NOT NULL,

                         created_at DATETIME NOT NULL,

                         CONSTRAINT fk_review_user
                             FOREIGN KEY (reviewer_id)
                                 REFERENCES users(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT fk_review_cow
                             FOREIGN KEY (cow_id)
                                 REFERENCES cows(id)
                                 ON DELETE CASCADE,

                         CONSTRAINT uk_reviewer_cow
                             UNIQUE (reviewer_id, cow_id)
);

CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,

                               message VARCHAR(500) NOT NULL,

                               type VARCHAR(50) NOT NULL,

                               is_read BOOLEAN NOT NULL DEFAULT FALSE,

                               user_id BIGINT NOT NULL,

                               created_at DATETIME NOT NULL,

                               CONSTRAINT fk_notification_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE
);