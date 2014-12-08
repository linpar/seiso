# -*- mode: ruby -*-
# vi: set ft=ruby :
require 'yaml'

settings = YAML.load_file 'vagrant.yml'

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "chef/centos-7.0"
  
  config.vm.define "db" do |db|
    db.vm.network 'private_network', ip: settings['db']['ip_address']
    db.vm.synced_folder settings['db']['artifacts_dir']['host'], settings['db']['artifacts_dir']['guest']
  end
  
  config.vm.define "bus" do |bus|
    bus.vm.network 'private_network', ip: settings['bus']['ip_address']
  end

  config.vm.define "app" do |app|
    app.vm.network 'private_network', ip: settings['app']['ip_address']
    app.vm.synced_folder settings['app']['artifacts_dir']['host'], settings['app']['artifacts_dir']['guest']
  end
end
