INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book1', 10000, 100, 'kim1', '11111', 'Book', 1);

INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book2', 15000, 50, 'kim2', '22222', 'Book', 2);

INSERT INTO item (name, price, stock_quantity, author, isbn, dtype, item_id)
VALUES ('book3', 17500, 75, 'kim3', '33333', 'Book', 3);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('SILVER', 150000, 1);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('VIP', 450000, 2);

INSERT INTO membership (grade, total_spending, membership_id)
VALUES ('GOLD', 230000, 3);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member1', '서울시', '송파구', '1111', 1, 1);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member2', '서울시', '성북구', '2222', 2, 2);

INSERT INTO member (name, city, street, zipcode, member_id, membership_id)
VALUES ('member3', '경기도', '판교시', '333', 3, 3);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '성북구', '2222', 'READY', 1);

INSERT INTO delivery (city, street, zipcode, status, delivery_id)
VALUES ('서울시', '송파구', '1111', 'READY', 2);

INSERT INTO orders (member_id, delivery_id, status, order_id)
VALUES (2, 1, 'ORDER', 1);

INSERT INTO orders (member_id, delivery_id, status, order_id)
VALUES (1, 2, 'ORDER', 2);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 1, 10000, 5, 1);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 1, 16000, 10, 2);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (2, 2, 15000, 3, 3);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (1, 2, 9900, 7, 4);

INSERT INTO order_item (item_id, order_id, order_price, quantity, order_item_id)
VALUES (3, 2, 17500, 2, 5);




