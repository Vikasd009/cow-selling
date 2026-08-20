CREATE INDEX idx_cows_seller_id
    ON cows(seller_id);

CREATE INDEX idx_cows_status
    ON cows(status);

CREATE INDEX idx_cows_breed
    ON cows(breed);

CREATE INDEX idx_cows_price
    ON cows(price);

CREATE INDEX idx_enquiries_buyer
    ON enquiries(buyer_id);

CREATE INDEX idx_enquiries_cow
    ON enquiries(cow_id);

CREATE INDEX idx_notifications_user
    ON notifications(user_id);

CREATE INDEX idx_favorites_user
    ON favorites(user_id);