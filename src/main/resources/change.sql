ALTER TABLE `retailer`.`product` 
ADD COLUMN `category` VARCHAR(45) NULL AFTER `max_price`;



--1.
ALTER TABLE `retailer`.`sales_order` 
ADD COLUMN `created_by` varchar(255) DEFAULT NULL,
ADD COLUMN `creation_date` datetime DEFAULT NULL,
ADD COLUMN `last_modified_date` datetime DEFAULT NULL,
ADD COLUMN `updated_by` varchar(255) DEFAULT NULL;
2.
update  retailer.sales_order a,
(select order_id,created_by,updated_by, creation_date,last_modified_date 
from sales_order_details  ) b
set a.created_by = b.created_by,
a.creation_date = b.creation_date,
a.last_modified_date = b.last_modified_date,
a.updated_by = b.updated_by
where a.order_id = b.order_id;

3.
ALTER TABLE `retailer`.`customer` 
ADD COLUMN `created_by` varchar(255) DEFAULT NULL,
ADD COLUMN `creation_date` datetime DEFAULT NULL,
ADD COLUMN `last_modified_date` datetime DEFAULT NULL,
ADD COLUMN `updated_by` varchar(255) DEFAULT NULL;

update `retailer`.`customer` 
set `updated_by` = 'SYSTEM',
    `last_modified_date` = '2018-09-15',
    `creation_date` '2018-09-15',
    `created_by`'SYSTEM';
	
	
4.
ALTER TABLE `retailer`.`payment_transaction` 
ADD COLUMN `created_by` varchar(255) DEFAULT NULL,
ADD COLUMN `creation_date` datetime DEFAULT NULL,
ADD COLUMN `last_modified_date` datetime DEFAULT NULL,
ADD COLUMN `updated_by` varchar(255) DEFAULT NULL;


5.
ALTER TABLE `retailer`.`seller` 
ADD COLUMN `created_by` varchar(255) DEFAULT NULL,
ADD COLUMN `creation_date` datetime DEFAULT NULL,
ADD COLUMN `last_modified_date` datetime DEFAULT NULL,
ADD COLUMN `updated_by` varchar(255) DEFAULT NULL;

6,
ALTER TABLE `retailer`.`master_data` 
ADD COLUMN `created_by` varchar(255) DEFAULT NULL,
ADD COLUMN `creation_date` datetime DEFAULT NULL,
ADD COLUMN `last_modified_date` datetime DEFAULT NULL,
ADD COLUMN `updated_by` varchar(255) DEFAULT NULL;


7. DROP TABLE `retailer`.`vendors`;
8.
DROP TABLE `retailer`.`staff`;