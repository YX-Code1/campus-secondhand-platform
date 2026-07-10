USE campus_trade;

ALTER TABLE trade_record ADD COLUMN item_title VARCHAR(100) COMMENT '物品名称快照';

UPDATE trade_record tr 
SET tr.item_title = (SELECT i.title FROM item i WHERE i.id = tr.item_id);
