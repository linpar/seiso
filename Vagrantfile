# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # https://vagrantcloud.com/chef
#  config.vm.box = "chef/centos-7.0"

  # Currently need to use CentOS 6.5, since the RabbitMQ cookbook isn't working with CentOS 7.0.
  # (RabbitMQ depends on Erlang, which depends on yum-erlang_solutions, and that has an attributes/erlang_solutions.rb
  # file that seems to hardcode CentosOS 6.) [WLW]
  config.vm.box = "puphpet/centos65-x64"

  config.vm.provider "virtualbox" do |v|
    v.cpus = 1
    v.memory = 1024
  end

  # Installs Chef Solo provisioner.
  config.omnibus.chef_version = :latest

  config.vm.define "db" do |db|
    db.vm.network "forwarded_port", guest: 22, host: 12222, auto_correct: true
    db.vm.network "forwarded_port", guest: 3306, host: 23141
    db.vm.synced_folder "#{File.dirname(__FILE__)}/src/main/sql", '/opt/seiso-db'
    db.vm.provision "chef_solo" do |chef|
      configure_chef(chef, "seiso_db")
      chef.json = {
        "seiso_db" => {
          "artifacts_dir" => '/opt/seiso-db'
        }
      }
    end
  end

  # TODO Add management console port
  config.vm.define "bus" do |bus|
    bus.vm.network "forwarded_port", guest: 22, host: 12223, auto_correct: true
    bus.vm.network "forwarded_port", guest: 5672, host: 23142
    bus.vm.provision "chef_solo" do |chef|
      configure_chef(chef, "seiso_bus")
    end
  end
end

def configure_chef(chef, role)
  chef.cookbooks_path = "chef/cookbooks"
  chef.roles_path = "chef/roles"
  chef.environments_path = "chef/environments"
  chef.add_role role
  chef.environment = "development"
end
