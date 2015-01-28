-- https://github.com/ExpediaDotCom/seiso/issues/44
alter table service_instance modify column load_balanced tinyint(1) unsigned default null;
