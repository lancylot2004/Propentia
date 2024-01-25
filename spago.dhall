{ name = "propentia"
, dependencies = [ "console", "effect", "prelude" ]
, packages = ./packages.dhall
, sources = [ "src/logic/**/*.purs", "src/logic/test/**/*.purs" ]
}
