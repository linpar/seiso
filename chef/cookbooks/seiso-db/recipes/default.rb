#
# Cookbook Name:: seiso-db
# Recipe:: default
# Author:: Willie Wheeler
#
# Copyright (c) 2014-2015 Expedia, Inc.
#

include_recipe "database::mysql"

db_name = node["seiso_db"]["db_name"]

mysql_connection = {
  :host => node["seiso_db"]["db_host"],
  :username => "root",
  :password => node["mysql"]["server_root_password"]
}

mysql_database db_name do
  connection mysql_connection
  action :create
end

node['seiso_db']['sql_scripts'].each do |s|
  mysql_database db_name do
    connection mysql_connection
    sql { ::File.open("#{node['seiso_db']['artifacts_dir']}/#{s}").read }
    action :query
  end
end

mysql_database_user node["seiso_db"]["seiso_username"] do
  connection mysql_connection
  password node["seiso_db"]["seiso_password"]
  database_name db_name
  host "%"
  privileges [ :select, :insert, :update ]
  action :grant
end
