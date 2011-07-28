require "buildr/scala"

Buildr.settings.build["scala.version"] = "2.8.1"

define "oscon" do
  project.version = "0.1.0"

  test.using :junit

  compile.with Dir["lib/*.jar"]

  package :jar
end

