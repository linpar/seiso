name "seiso_bus"
description "Seiso message bus"
run_list "recipe[erlang]"
override_attributes(
  "erlang" => {
    "install_method" => "package"
  },
  "rabbitmq" => {
    "packagebaseame" => "rabbitmq-server",
    "extension" => ".noarch.rpm",
    "version" => "3.4.0"
  }
)
