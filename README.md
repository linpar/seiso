seiso
=====

[![Build Status](https://travis-ci.org/ExpediaDotCom/seiso.svg?branch=master)](https://travis-ci.org/ExpediaDotCom/seiso)

Devops data integration repository.

Please see the [Seiso project site](http://expediadotcom.github.io/seiso/) for project information and documentation.

Dev setup
=========

There are two options available here. The first is a manual setup, where you install Java, MySQL, RabbitMQ and the app yourself. The other is via Vagrant.

Approach #1: Manual setup
-------------------------

**Installation.** You'll need to install the following:

* Java 8
* MySQL 5.6.x
  * Create the Seiso database schema by running `src/main/sql/seiso-schema.sql`.
  * For now, you can load sample data by running `src/main/sql/seiso-data-sample.sql`. We may replace this with a non-SQL data importer at some point in the future.
* RabbitMQ
  * For visibility we recommend installing the RabbitMQ management plugin.

**Configuration.** Create copies of the following files from `conf-sample` and modify them as appropriate:

* `application.yml`
* `keystore-dev.jks`
* `log4j.xml`

To run Seiso in development mode, you can place them directly in `src/main/resources`.

Approach #2: Vagrant setup
--------------------------

You can use [Vagrant](https://www.vagrantup.com/) to set up your development environment, which consists of a MySQL database and a RabbitMQ message bus.

As a preliminary, you will need to install the [Omnibus plugin](https://github.com/opscode/vagrant-omnibus), which will install the Chef provisioner on your VMs.

First create a copy of `vagrant.yml.sample` called `vagrant.yml` and place it in the same directory. Change the configuration as necessary. (The instructions are in `vagrant.yml.sample` itself.)

Then do

    $ vagrant up

to create your VMs.

Note that it takes a little while to create VMs, so if you want to speed things up, you can open two terminal windows and do

    $ vagrant up db

in one and

    $ vagrant up bus

in the other, just to parallelize the work.

Build Seiso
===========

    $ ./gradlew clean build

Run Seiso
=========

    $ ./gradlew bootRun

Point your HTML5-enabled browser to **https://localhost:8443** or whatever scheme/port combo you chose during configuration. You should see a home page with a list of services.
